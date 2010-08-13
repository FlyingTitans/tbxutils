/*
 * TermBase eXchange conformance checker library.
 * Copyright (C) 2010 Lance Finn Helsten (helsten@acm.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

