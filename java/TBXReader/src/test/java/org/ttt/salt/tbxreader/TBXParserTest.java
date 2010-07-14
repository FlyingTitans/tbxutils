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
        if (true)
        {
            java.util.logging.Formatter simplefmt = new java.util.logging.SimpleFormatter();
            java.util.logging.Handler handler = new java.util.logging.ConsoleHandler();
            handler.setLevel(java.util.logging.Level.FINEST);
            handler.setFormatter(simplefmt);
            TBXReader.LOGGER.addHandler(handler);
        }
        saxfactory = SAXParserFactory.newInstance();
    }
    
    @Before
    public void setUp() throws Exception
    {
        TBXReader.LOGGER.setLevel(java.util.logging.Level.INFO);
        saxfactory.setNamespaceAware(true);
        saxfactory.setValidating(true);
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    @Test
    public void parseValidFileDTD() throws Exception
    {
        TBXReader.LOGGER.setLevel(java.util.logging.Level.FINEST);
        InputStream in = getClass().getResourceAsStream("/org/ttt/salt/tbxreader/ValidDTD.xml");
        assertNotNull(in);
        TBXParser parser = new TBXParser(saxfactory.newSAXParser(), in);
        parser.join(100);
    }
}

