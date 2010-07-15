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
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class TBXParserTest
{
    private static SAXParserFactory saxfactory;
        
    @BeforeClass
    public static void initialize() throws Exception
    {
        java.util.logging.Formatter simplefmt = new java.util.logging.SimpleFormatter();
        java.util.logging.Handler handler = new java.util.logging.ConsoleHandler();
        handler.setLevel(java.util.logging.Level.FINEST);
        handler.setFormatter(simplefmt);
        TBXParser.PARSE_LOG.addHandler(handler);
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);

        saxfactory = SAXParserFactory.newInstance();
    }
    
    @Before
    public void setUp() throws Exception
    {
        TBXReader.LOGGER.setLevel(java.util.logging.Level.INFO);
        TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.WARNING);
        saxfactory.setNamespaceAware(true);
        saxfactory.setValidating(true);
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    private TBXParser buildParser(String testfile) throws Exception
    {
        String file = String.format("/org/ttt/salt/tbxreader/TBXParserTest/%s", testfile);
        InputStream in = getClass().getResourceAsStream(file);
        assertNotNull("Unable to get stream for " + file, in);
        TBXParser parser = new TBXParser(saxfactory.newSAXParser(), in);
        return parser;
    }
    
    private void errorChecking(String testfile) throws Exception
    {
        try
        {
            TBXParser parser = buildParser(testfile);
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
    public void parseValidFileDTD() throws Exception
    {
        TBXParser parser = buildParser("ValidDTD.xml");
        MartifHeader header = parser.getMartifHeader();
        parser.getThread().join(100);
    }
    
    @Test
    public void parseInvalidTermEntry() throws Exception
    {
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);
        TBXParser parser = buildParser("InvalidTermEntry.xml");
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
    
    @Test
    public void threadManagement() throws Exception
    {
        TBXParser parser = buildParser("BlockingFile.xml");
        assertNotNull(parser.getMartifHeader());
        while (!parser.isTermEntryQueueFull())
            Thread.currentThread().sleep(1);
        assertEquals(TBXParser.QUEUE_SIZE, parser.getTermEntriesProcessed());
        Thread.currentThread().sleep(10);
        assertTrue(parser.getThread().isAlive());
        assertNotNull(parser.getNextTermEntry());
        Thread.currentThread().sleep(1);
        assertEquals(TBXParser.QUEUE_SIZE + 1, parser.getTermEntriesProcessed());
        parser.stop();
        
        int count = 0;
        while (parser.getNextTermEntry() != null)
            count++;
        assertTrue(count < TBXParser.QUEUE_SIZE + 2);
    }    
}

