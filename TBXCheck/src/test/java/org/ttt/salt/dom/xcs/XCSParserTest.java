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
package org.ttt.salt.dom.xcs;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.logging.*;
import org.xml.sax.*;
import org.ttt.salt.XCSDocument;

/**
 * @author  Lance Finn Helsten
 * @version $Id$
 */
public class XCSParserTest
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.dom.xcs");

    XCSParser parser;
    
    XCSParser.TestAccess access;
    
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
    public XCSParserTest()
    {
    }

    @BeforeClass
    public static void classSetup() throws IOException
    {
        LogManager.getLogManager().reset();
        Formatter simplefmt = new SimpleFormatter();
        Handler logfile = new FileHandler(System.getProperty("user.dir")
                + "/target/surefire-reports/"
                + "org.ttt.salt.dom.xcs.XCSParserTest.log");
        logfile.setLevel(Level.FINEST);
        logfile.setFormatter(simplefmt);
        Logger.getLogger("org.ttt.salt.dom.xcs").setLevel(Level.INFO);
        Logger.getLogger("org.ttt.salt.dom.xcs").addHandler(logfile);
    }

    @AfterClass
    public static void classTeardown()
    {
        LogManager.getLogManager().reset();
    }

    @Before
    public void setUp() throws SAXNotRecognizedException, SAXNotSupportedException
    {
        parser = new XCSParser();
        access = parser.new TestAccess();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void createParser()
    {   //Make sure the parser may be built
        assertNotNull("XCSParser did not build", parser);
        assertNotNull("XCSParser$TestAccess did not build", access);
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
        access.stack().push(new XCSElement(access.document(), "SPAM", locator));
        parser.startDocument();
    }
    
    @Test(expected=AssertionError.class)
    public void endDocument() throws SAXException
    {   //NOTDONE: need to determine how to turn on assertions in test code
        access.document(new XCSDocument());
        parser.startDocument();
        assertTrue(access.stack().isEmpty());
        access.stack().push(new XCSElement(access.document(), "SPAM", locator));
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
    public void checkTBXDCSv05() throws SAXException, IOException
    {   //Make sure we have the XCS file we will test against
        LOGGER.setLevel(Level.INFO);
        InputStream instm = getClass().getResourceAsStream("/xml/TBXDCSv05.xml");
        assertNotNull("TBXDCSv05 not found", instm);
        InputSource input = new InputSource(instm);
        parser.parse(input);
        LOGGER.setLevel(Level.INFO);
    }
}
