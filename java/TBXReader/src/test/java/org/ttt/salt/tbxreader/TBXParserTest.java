/*
 * Copyright 2010 Lance Finn Helsten (helsten@acm.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ttt.salt.tbxreader;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class TBXParserTest
{
	private static File cwd;
	private static File xmldir;
	private static File corestructfile;
	private static File xcsstructfile;
	
	private TBXParser parser;
        
    @BeforeClass
    public static void initialize() throws Exception
    {
        TBXReaderTest.configureLoggers("TBXParserTest");
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);

		cwd = new File(System.getProperty("user.dir"));
		xmldir = new File(cwd, "src/main/resources/xml/");
		corestructfile = new File(xmldir, "TBXcoreStructV02.dtd");
		xcsstructfile = new File(xmldir, "tbxxcsdtd.dtd");
    }
    
    @Before
    public void setUp() throws Exception
    {
        TBXReader.LOGGER.setLevel(java.util.logging.Level.INFO);
        TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.WARNING);
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    private void buildParser(String testfile) throws Exception
    {
        String file = String.format("/org/ttt/salt/tbxreader/TBXParserTest/%s", testfile);
        InputStream in = getClass().getResourceAsStream(file);
        assertNotNull("Unable to get stream for " + file, in);
        parser = new TBXParser(TBXReader.getInstance().getTBXFileSAXParser(null), in);
    }
	
	private String readResource(String name) throws Exception
	{
		InputStream in = getClass().getResourceAsStream(name);
		Reader reader = new InputStreamReader(in, "UTF-8");
		CharBuffer text = CharBuffer.allocate(4096);
		reader.read(text);
		reader.close();
		return text.toString();
	}
    
    private void errorChecking(String testfile) throws Exception
    {
        try
        {
            buildParser(testfile);
            MartifHeader header = parser.getMartifHeader();
            parser.getThread().join(100);
        }
        catch (TBXException err)
        {
            if (err.getCause() instanceof SAXParseException)
                throw (SAXParseException) err.getCause();
            else
                throw err;
        }
    }
    
	//===========================================
	// Error testing
	//===========================================
	
    @Test(expected=SAXParseException.class)
    public void emptyFile() throws Exception
    {
        errorChecking("EmptyFile.xml");
    }
    
    @Test(expected=IOException.class)
    public void badDTDPath() throws Exception
    {
        errorChecking("BadDTDPath.xml");
    }
    
    @Test(expected=SAXParseException.class)
    public void corruptStreamEOF() throws Exception
    {
        errorChecking("CorruptStreamEOF.xml");
    }
    
    @Test(expected=SAXParseException.class)
    public void corruptStreamIllform() throws Exception
    {
        errorChecking("CorruptStreamIllform.xml");
    }
    
    @Test(expected=SAXParseException.class)
    public void corruptStreamNoXml() throws Exception
    {
        errorChecking("CorruptStreamNoXml.xml");
    }
    
    @Test(expected=InvalidFileException.class)
    public void martifHeaderNotTBX() throws Exception
    {
        errorChecking("MartifHeaderNotTBX.xml");
    }
    
    @Test(expected=InvalidFileException.class)
    public void martifHeaderNoLang() throws Exception
    {
        errorChecking("MartifHeaderNoLang.xml");
    }
    
    @Test
    public void parseInvalidTermEntry() throws Exception
    {
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);
        buildParser("InvalidTermEntry.xml");
        MartifHeader header = parser.getMartifHeader();
        
        TermEntry entry;
        TBXParseException except;
        
        entry = parser.getNextTermEntry();
        assertEquals(22, entry.getLineNumber());
        assertEquals(45, entry.getColumnNumber());
        assertEquals(1, entry.getExceptions().size());
        except = (TBXParseException) entry.getExceptions().get(0);
        assertEquals(22, except.getLineNumber());
        assertEquals(45, except.getColumnNumber());

        entry = parser.getNextTermEntry();
        assertEquals(24, entry.getLineNumber());
        assertEquals(47, entry.getColumnNumber());
        assertEquals(1, entry.getExceptions().size());
        except = (TBXParseException) entry.getExceptions().get(0);
        assertEquals(25, except.getLineNumber());
        assertEquals(25, except.getColumnNumber());
    }


	//===========================================
	// Valid file testing
	//===========================================

    @Test
    public void validDTD() throws Exception
    {
        buildParser("ValidDTD.xml");
        MartifHeader header = parser.getMartifHeader();
        parser.getThread().join(100);
    }
	
	
	//===========================================
	// Infrastructure Testing
	//===========================================

    @Test
    public void threadManagement() throws Exception
    {
        buildParser("BlockingFile.xml");
        assertNotNull(parser.getMartifHeader());
        while (!parser.isTermEntryQueueFull())
            Thread.currentThread().sleep(1);
        assertEquals(TBXParser.QUEUE_SIZE, parser.getTermEntriesProcessed());
        Thread.currentThread().sleep(10);
        assertTrue(parser.getThread().isAlive());
        assertNotNull(parser.getNextTermEntry());
        Thread.currentThread().sleep(1);
        assertTrue(TBXParser.QUEUE_SIZE + 1 >= parser.getTermEntriesProcessed());
        parser.stop();
        
        int count = 0;
        while (parser.getNextTermEntry() != null)
            count++;
        assertTrue(count < TBXParser.QUEUE_SIZE + 2);
    }
	
	
	//===========================================
	// Entity Resolution Testing
	//===========================================

	private String readEntity(String publicId, String systemId) throws Exception
	{
		InputSource src = parser.resolveEntity(publicId, systemId);
		Reader reader = src.getCharacterStream();
		CharBuffer text = CharBuffer.allocate(4096);
		reader.read(text);
		reader.close();
		return text.toString();
	}
    
    @Test
    public void xmlResolveEntity() throws Exception
    {
		parser = new TBXParser();
		String corestruct = readResource("/xml/TBXcoreStructV02.dtd");
		String xcsstruct = readResource("/xml/tbxxcsdtd.dtd");
		
		assertEquals(corestruct,
			readEntity("ISO 30042:2008A//DTD TBX core//EN", null));
		assertEquals(corestruct,
			readEntity("ISO 30042:2008A//DTD TBX core//EN", "http://www.flyingtitans.com/"));
		assertEquals(corestruct,
			readEntity("ISO 30042:2008A//DTD TBX core//EN", "/myfile"));
		assertEquals(corestruct, readEntity(null, "/xml/TBXcoreStructV02.dtd"));
		assertEquals(corestruct, readEntity(null, corestructfile.getCanonicalPath()));
		
		assertEquals(xcsstruct,
			readEntity("ISO 30042:2008A//DTD TBX XCS//EN", null));
		assertEquals(xcsstruct,
			readEntity("ISO 30042:2008A//DTD TBX XCS//EN", "http://www.flyingtitans.com/"));
		assertEquals(xcsstruct,
			readEntity("ISO 30042:2008A//DTD TBX XCS//EN", "/myfile"));
		
    }

    @Test(expected=FileNotFoundException.class)
    public void xmlResolveEntityFileNotFoundURI() throws Exception
    {
		parser = new TBXParser();
		parser.resolveEntity("NOT IN SYSTEM", "file:///spam/eggs/bloodyvikings.xml");
    }

    @Test(expected=FileNotFoundException.class)
    public void xmlResolveEntityFileNotFoundResource() throws Exception
    {
		parser = new TBXParser();
		parser.resolveEntity("NOT IN SYSTEM", "/xml/spam.dtd");
    }

    @Test(expected=FileNotFoundException.class)
    public void xmlResolveEntityFileNotFoundRelativePath() throws Exception
    {
		parser = new TBXParser();
		parser.resolveEntity("NOT IN SYSTEM", "xml/spam.dtd");
    }

    @Test(expected=java.net.URISyntaxException.class)
    public void xmlResolveEntityURISyntax() throws Exception
    {
		try
		{
			parser = new TBXParser();
			parser.resolveEntity("NOT IN SYSTEM", "http://www.flyingtitans.com/a%b");
		}
		catch (org.xml.sax.SAXException err)
		{
			throw (java.net.URISyntaxException) err.getCause();
		}
    }
}

