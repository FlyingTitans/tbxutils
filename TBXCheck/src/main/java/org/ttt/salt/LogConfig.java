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
package org.ttt.salt;

import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogManager;

/**
 * Defines the base level log configuration when the associated Main class
 * is executed.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class LogConfig
{
    /** SCM information. */
    public static final String RCSID = "$Id";

    /** Manager to be used for logging. */
    private LogManager mgr = LogManager.getLogManager();

    /** Primary formatter. */
    private Formatter simplefmt = new SimpleFormatter();

    /** Send all log messages to the console. */
    private Handler console = new ConsoleHandler();

    /** Send all lof messages to a file. */
    private Handler xmlfile;

    /**
     * Setup the logging configuration.
     *
     * @throws IOException Any I/O exceptions that occur
     */
    public LogConfig() throws IOException
    {
        LogManager.getLogManager().reset();
        String logType = System.getProperty("org.ttt.logging.config", "RELEASE");
        if (logType.equals("DEBUG"))
        {
            console.setLevel(Level.FINEST);
            console.setFormatter(simplefmt);

            Logger.getLogger("org.ttt").setLevel(Level.INFO);
            Logger.getLogger("org.ttt").addHandler(console);
            
            Logger.getLogger("org.ttt.salt", "org.ttt.salt.Logging");
            Logger.getLogger("org.ttt.salt").setLevel(Level.INFO);
        }
        else if (logType.equals("RELEASE"))
        {
            console.setLevel(Level.FINEST);
            console.setFormatter(simplefmt);

            Logger.getLogger("org.ttt").setLevel(Level.INFO);
            Logger.getLogger("org.ttt").addHandler(console);

            Logger.getLogger("org.ttt.salt", "org.ttt.salt.Logging");
            Logger.getLogger("org.ttt.salt").setLevel(Level.WARNING);
        }
        else if (logType.endsWith(".properties"))
        {
            ClassLoader loader = getClass().getClassLoader();
            InputStream config = loader.getResourceAsStream(logType);
            assert config != null;
            mgr.readConfiguration(config);
        }
        else
        {
            String msg = String.format("Invalid value for org.ttt.logging.config : %s", logType);
            throw new IOException(msg);
        }
    }
}

