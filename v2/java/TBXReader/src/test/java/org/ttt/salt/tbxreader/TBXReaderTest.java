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
import java.util.logging.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.xml.sax.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class TBXReaderTest
{
    private static SAXParserFactory saxfactory;

    @BeforeClass
    public static void initialize() throws Exception
    {
        configureLoggers("TBXReaderTest");
    }

    /**
     * Configure the logging system for tests.
     */
    public static void configureLoggers(String name) throws IOException
    {
        File dir = new File("./target/test-logs");
        File file = new File(dir, name + ".log");
    
        if (!dir.exists())
            dir.mkdirs();
        if (dir.isFile())
            throw new IllegalStateException("Test logging directory is a file.");
        if (file.isDirectory())
            throw new IllegalArgumentException("Log file is a directory.");
        if (file.exists())
            file.delete();
    
        LogManager.getLogManager().reset();
        Formatter simplefmt = new SimpleFormatter();
        OutputStream out = new FileOutputStream(file);
        Handler handler = new StreamHandler(out, simplefmt);
        handler.setLevel(Level.FINEST);        

        TBXReader.LOGGER.setLevel(Level.INFO);
        TBXReader.LOGGER.addHandler(handler);
    }
    
    @Test
    public void configureParsers() throws Exception
    {
    }
	
	@Test
	public void tbxsaxparser() throws Exception
	{
		SAXParser parse0 = TBXReader.getInstance().getTBXFileSAXParser(null);
		SAXParser parse1 = TBXReader.getInstance().getTBXFileSAXParser(null);
		assertNotNull(parse0);
		assertNotNull(parse1);
		assertNotSame(parse0, parse1);
	}
	
	@Test
	public void xpath() throws Exception
	{
		XPath xpath0 = TBXReader.getInstance().getXPath();
		XPath xpath1 = TBXReader.getInstance().getXPath();
		assertNotNull(xpath0);
		assertNotNull(xpath1);
		assertNotSame(xpath0, xpath1);
	}
}

