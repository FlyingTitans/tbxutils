/*
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
package org.ttt.salt.tbxcheck;

import java.io.File;
import java.io.Writer;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


/**
 * Main entry point for the command line tool to vheck a TBX file to be
 * XML valid and XCS compliant.
 * 
 * @author Lance Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public final class Main
{
    /** Logger to use in this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.tbxcheck");
    
    /** Single instance of this object. */
    private static Main instance;
    
    /** Resource bundle to be used by this object. */
    private ResourceBundle bundle;
    
    /** Command line parser for this object. */
    private CmdLineParser parser;
    
    /** Option to display help and exit. */
    @Option(name="--help", usage="usageHelp")
    private boolean help;
    
    /** Option to display version and exit. */
    @Option(name="-V", aliases="--version", usage="usageVersion")
    private boolean version;
        
    /** Option to set the logging level, these should match log4j levels. */
    @Option(name="-v", aliases="--verbose", usage="usageVerbose")
    private String loglevel;
        
    /** List of arguments to the application. */
    @Argument
    private List<String> arguments = new java.util.ArrayList<String>();
    
    /**
     * Main entry point for the application.
     *
     * @param argv Command line arguments.
     */
    public static void main(String[] argv)
    {
        try
        {
            instance = new Main(argv);
            LOGGER.setLevel(Level.parse(instance.loglevel));
            Main obj = getInstance();
            obj.dispatch();
        }
        catch (CmdLineException err)
        {
            System.err.println(err.getMessage());
            System.err.println("java -jar tbxcheck.jar [options...] config-file...");
        }
        catch (FileNotFoundException err)
        {
            System.err.println(err.getMessage());
        }
        catch (SecurityException err)
        {
            System.err.println(err);
        }
        catch (IOException err)
        {
            System.err.println(err);
        }
    }

    /**
     * Returns the single instance of the Main application object.
     *
     * @return Instance of Main.
     */
    public static Main getInstance()
    {
        return instance;
    }
    
    /**
     * Construct single instance of Main.
     */
    private Main(String[] argv) throws CmdLineException
    {
        bundle = ResourceBundle.getBundle("org.ttt.salt.tbxcheck.Main");
        parser = new CmdLineParser(this);
        parser.parseArgument(argv);
    }

    /**
     * Dispatch based on the options and arguments.
     *
     * @throws IOException Any I/O exceptions that occur.
     */
    private void dispatch() throws IOException
    {
        if (help)
        {
            Writer out = new java.io.OutputStreamWriter(System.out);
            parser.printUsage(out, bundle);
            System.exit(0);
        }

        if (version)
        {
			//TODO: Get the version from the JAR file.
            System.out.println("2.0-SNAPSHOT");
            System.exit(0);
        }
                
        checkSystem();
        initSystem();
        List<File> files = checkFiles(arguments);
        processFiles(files);
    }

    /**
     * Check that the system is appropriate for operations. Are the necessary
     * environment variables set, are the appropriate files in place, etc.
     *
     * @exception IOException Any unhandled IO Exceptions.
     */
    private void checkSystem() throws IOException
    {
        LOGGER.entering("TBXCheck", "Checking system");
    }

    /**
     * Perform any initialization of the system before processing arguments.
     */
    private void initSystem()
    {
        LOGGER.entering("TBXCheck", "Initialize system");
    }

    /**
     * Check the list of paths to ensure they are valid and then return a
     * list of {@link java.io.File} objects for further processing.
     *
     * @param paths List of file paths to check for access and existence.
     * @return List of {@link java.io.File} objects.
     * @exception IOException Any unhandled IO Exceptions.
     */
    private List<File> checkFiles(List<String> paths) throws IOException
    {
        LOGGER.entering("TBXCheck", "Checking XML files");
        List<File> ret = new java.util.ArrayList<File>();
        for (String path : paths)
        {
            File file;
            if (path == null)
                continue;
            
            //Convert the string path to a file object
            if (path.equals("--"))
                file = null;
            else if (path.startsWith(File.separator))
                file = new File(path);
            else if (System.getProperty("os.name").startsWith("Windows") && path.matches("^[a-zA-Z]:.*$"))
                file = new File(path);
            else
                file = new File(System.getProperty("user.dir"), path);
            
            //Check if the file exists, is regular, and is readable
            if (checkFile(file))
                ret.add(file);
        }
        return ret;
    }
    
    /**
     * Check to see if the given file exists, is a regular file, and can be
     * read by the current user.
     *
     * @param file The {@link java.io.File} to check to see if it is valid,
     *  if this is null then true will be returned.
     * @return true => The file exists, is regular, and readable, or the file
     *  object was null.
     * @exception IOException Any unhandled IO Exceptions.
     */
    private boolean checkFile(File file) throws IOException
    {
        LOGGER.entering("TBXCheck", "Checking File", file);
        boolean ret = true;
        String fmt = null;
        if (!file.exists())
            fmt = bundle.getString("PathMissing");
        else if (!file.isFile())
            fmt = bundle.getString("PathNotNormalFile");
        else if (!file.canRead())
            fmt = bundle.getString("PathSecurityViolation");
        if (fmt != null)
        {
            String msg = MessageFormat.format(fmt, file);
            System.err.println(msg);
            ret = false;
        }
        return ret;
    }

    /**
     * Process authzgenerator file in the list.
     *
     * @param files List of {@link java.io.File} objects to process.
     * @throws IOException Any I/O exceptions that occur.
     */
    private void processFiles(List<File> files) throws IOException
    {
        LOGGER.entering("TBXCheck", "Processing files");
		for (File file : files)
		{
        }
    }

    /**
     * Process a single input stream.
     *
     * @param in The {@link java.io.InputStream} to process.
     * @throws IOException Any I/O exceptions that occur.
     */
    private void processFile(InputStream in) throws IOException
    {
    }
}

