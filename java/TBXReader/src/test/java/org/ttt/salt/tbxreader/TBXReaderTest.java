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
import java.util.logging.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class TBXReaderTest
{
    @BeforeClass
    public static void initialize() throws Exception
    {
        configureLoggers("TBXReaderTest");
    }
    
    @Test
    public void configureParsers() throws Exception
    {
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
}

