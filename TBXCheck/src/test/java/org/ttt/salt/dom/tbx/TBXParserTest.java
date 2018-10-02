/*
 * $Id$
 *-----------------------------------------------------------------------------
 * Copyright 2000 Lance Finn Helsten (helsten@acm.org)
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
package org.ttt.salt.dom.tbx;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.net.URL;
import java.util.logging.*;
import org.xml.sax.*;
import org.ttt.salt.*;

/**
 * @author  Lance Finn Helsten
 * @version $Id$
 */
public class TBXParserTest
{
    /** SCM information. */
    public static final String RCSID = "$Id$";
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.dom.xcs");

    Configuration config;
    TBXResolver resolver;
    TBXParser parser;
    TBXParser.TestAccess access;
    
    final int line = (int) (Math.random() * 10000);
    final int column = (int) (Math.random() * 256);
    Locator locator = new Locator()
        {
            public String getPublicId() {throw new UnsupportedOperationException();}
            public String getSystemId() {throw new UnsupportedOperationException();}
            public int getLineNumber() {return line;}
            public int getColumnNumber() {return column;}
        };

    
    /** Main constructor. */
    public TBXParserTest()
    {
    }

    @BeforeClass
    public static void classSetup() throws IOException
    {
        LogManager.getLogManager().reset();
        Formatter simplefmt = new SimpleFormatter();
        Handler logfile = new FileHandler(System.getProperty("user.dir")
                + "/target/test-logs/"
                + "org.ttt.salt.dom.tbx.TBXParserTest.log");
        logfile.setLevel(Level.FINEST);
        logfile.setFormatter(simplefmt);
        Logger.getLogger("org.ttt.salt.dom.tbx").setLevel(Level.INFO);
        Logger.getLogger("org.ttt.salt.dom.tbx").addHandler(logfile);
    }

    @AfterClass
    public static void classTeardown()
    {
        LogManager.getLogManager().reset();
    }

    @Before
    public void setUp() throws Exception
    {
        Logger.getLogger("org.ttt.salt.dom.tbx").setLevel(Level.INFO);
        config = new Configuration();
        config.setCheckEachTerm(false);
        
        URL cwd = new File(System.getProperty("user.dir")).toURI().toURL();
        resolver = new TBXResolver(cwd);
        parser = new TBXParser(resolver, config);
        access = parser.new TestAccess();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void createParser()
    {   //Make sure the parser may be built
        assertNotNull("TBXParser did not build", parser);
        assertNotNull("TBXParser$TestAccess did not build", access);
    }    
    
    @Test
    public void checkLocator()
    {   //Check that document locator gets set correctly
        assertNull(access.locator());
        parser.setDocumentLocator(locator);
        assertNotNull(access.locator());
        assertEquals(line, access.locator().getLineNumber());
        assertEquals(column, access.locator().getColumnNumber());
    }
    
    @Test(expected=AssertionError.class)
    public void checkStartEndDocument() throws SAXException
    {   //NOTDONE: need to determine how to turn on assertions in test code
        access.stack().push(new TBXElement(access.document(), "SPAM", locator));
        parser.startDocument();
    }
    
    @Test(expected=AssertionError.class)
    public void endDocument() throws SAXException
    {   //NOTDONE: need to determine how to turn on assertions in test code
        access.document(new TBXDocument(true));
        parser.startDocument();
        assertTrue(access.stack().isEmpty());
        access.stack().push(new TBXElement(access.document(), "SPAM", locator));
        parser.endDocument();
    }
    
    @Test
    public void startEndElement() throws SAXException
    {
        parser.startDocument();
        assertNull(access.current());
        //parser.startElement();
        //assertNotNull(access.current());
        //assertTrue(access.stack.isEmpty());
        //parser.startElement();
        //assertFalse(access.stack.isEmpty());
        //parser.endElement();
        //assertTrue(access.stack.isEmpty());
        //parser.endElement();
        //assertNull(access.current());
        parser.endDocument();
    }
    
    @Test
    public void checkValidDTD() throws SAXException, IOException
    {   //Make sure we have the TBX file we will test against
        LOGGER.setLevel(Level.INFO);
        InputStream instm = getClass().getResourceAsStream("/org/ttt/salt/dom/tbx/ValidDTD.xml");
        assertNotNull("ValidDTD not found", instm);
        InputSource input = new InputSource(instm);
        TBXDocument doc = parser.parse(input);
        assertNotNull(doc);
        assertTrue(doc.getParseExceptions().isEmpty());
        LOGGER.setLevel(Level.INFO);
    }
    
    @Test
    public void checkWarning() throws SAXException, IOException
    {   //Make sure we have the TBX file we will test against
        LOGGER.setLevel(Level.INFO);
        InputStream instm = getClass().getResourceAsStream("/org/ttt/salt/dom/tbx/ErrorWarning.xml");
        assertNotNull("ErrorWarning not found", instm);
        InputSource input = new InputSource(instm);
        TBXDocument doc = parser.parse(input);
        assertNotNull(doc);
        assertFalse(doc.getParseExceptions().isEmpty());
        LOGGER.setLevel(Level.INFO);
    }
}


