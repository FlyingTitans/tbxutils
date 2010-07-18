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

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class MartifHeaderTest
{
	private TBXParser parser;
	private MartifHeader header;
        
    @BeforeClass
    public static void initialize() throws Exception
    {
        TBXReaderTest.configureLoggers("MartifHeaderTest");
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);
    }
    
    @Before
    public void setUp() throws Exception
    {
        String file = String.format("/org/ttt/salt/tbxreader/MartifHeaderTest/MartifHeader.xml");
        InputStream in = getClass().getResourceAsStream(file);
        assertNotNull("Unable to get stream for " + file, in);
        parser = new TBXParser(TBXReader.getInstance().getTBXFileSAXParser(null), in);
		header = parser.getMartifHeader();
    }
	    
    @After
    public void tearDown() throws Exception
    {
		parser.stop();
    }
    
	@Test
	public void title() throws Exception
	{
		assertEquals("TBX File Title", header.getTitle());
	}
}

