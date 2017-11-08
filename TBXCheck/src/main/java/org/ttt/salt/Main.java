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

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.xml.sax.SAXException;
import org.flyingtitans.util.GetOpt;

/**
 * This is the main entry point for the TBX Validator.
 * <p>
 * For usage information see Main.properties or use -h parameter when
 * executing.</p>
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public final class Main
{
    /** SCM information. */
    public static final String RCSID = "$Id";

    /** Single character options. */
    private static final String OPTIONS = "h";

    /** Long options. */
    private static final String[] LONG_OPTIONS = {
            "nolang",
            "help", "version", "environment",
            "lang=", "country=", "variant=", "loglevel="
        };

    /** Main logger for this class. */
    private static final Logger LOGGER;

    /** Single allowed instance of this Class. */
    private static Main instance;
    
    static
    {
        try
        {
            System.setProperty("java.util.logging.config.class", "org.ttt.salt.LogConfig");
            LogManager.getLogManager().readConfiguration();
        }
        catch (IOException err)
        {
            err.printStackTrace();
            System.exit(1);
        }
        LOGGER = Logger.getLogger("org.ttt.salt");
    }

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
            Main obj = getInstance();
            obj.dispatch();
        }
        catch (IllegalArgumentException err)
        {
            System.err.println("Illegal command line parameter.");
            System.err.println("Use -h for more information.");
        }
        catch (FileNotFoundException err)
        {
            System.err.println(err.getLocalizedMessage());
        }
        catch (SecurityException err)
        {
            System.err.println(err);
        }
        catch (IOException err)
        {
            System.err.println(err);
        }
        catch (SAXException err)
        {
            System.err.println(err);
        }
    }

    /**
     * Returns the single instance of the Main application object.
     * @return Instance of Main.
     */
    public static Main getInstance()
    {
        return instance;
    }


    /** Parsed options. */
    private final GetOpt options = new GetOpt();
    
    /**
     * Construct single instance of Main.
     * @param argv Command line arguments.
     * @throws IOException Any I/O exceptions that occur.
     */
    private Main(String[] argv) throws IOException
    {
        options.parseArgs(argv, OPTIONS, LONG_OPTIONS);
        
        //setup the default locale state
        String lang = options.containsOption("--lang")
            ? options.getParameter("--lang")
            : Locale.getDefault().getLanguage();
        String country = options.containsOption("--country")
            ? options.getParameter("--country")
            : Locale.getDefault().getCountry();
        String variant = options.containsOption("--variant")
            ? options.getParameter("--variant")
            : Locale.getDefault().getVariant();
        Locale def = new Locale(lang, country, variant);
        Locale.setDefault(def);

        //setup the loglevel state
        String loglevel = options.containsOption("--loglevel")
            ? options.getParameter("--loglevel")
            : "SEVERE";
        Level level = Level.parse(loglevel);
        LOGGER.setLevel(level);
    }

    /**
     * Dispatch based on the settings determined by arguments.
     *
     * @throws IOException Any I/O exceptions that occur.
     */
    private void dispatch() throws IOException, SAXException
    {
        boolean exit = false;
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.Main");
        
        if (options.containsOption("-h") || options.containsOption("--help"))
        {
            System.out.println(bundle.getString("Usage"));
            exit = true;
        }

        if (options.containsOption("--version"))
        {
            String rcsidpat = "^\\$Id(: (\\S+) (\\d+) (\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})Z (\\S+))?\\s*\\$$";
            Matcher matcher = Pattern.compile(rcsidpat).matcher(RCSID);
            if (matcher.matches() && matcher.group(1) != null)
            {
                //CHECKSTYLE: MagicNumber OFF
                String version = matcher.group(3);
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.set(Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        Integer.parseInt(matcher.group(6)),
                        Integer.parseInt(matcher.group(7)),
                        Integer.parseInt(matcher.group(8)),
                        Integer.parseInt(matcher.group(9)));
                String msg = bundle.getString("Version");
                System.out.println(MessageFormat.format(msg, matcher.group(3), cal.getTime()));
                //CHECKSTYLE: MagicNumber ON
            }
            else
            {
                String msg = bundle.getString("VersionBad");
                System.out.println(MessageFormat.format(msg, RCSID));
            }
            exit = true;
        }

        if (options.containsOption("--environment"))
        {
            System.out.println("Arguments:");
            System.out.println("\t" + options);
            System.out.println();
            System.out.println("Environment:");
            String spaces = "                            ";
            SortedMap<Object, Object> env = new java.util.TreeMap<Object, Object>(System.getProperties());
            Iterator iter = env.keySet().iterator();
            while (iter.hasNext())
            {
                String name = iter.next().toString();
                String value = env.get(name).toString();
                System.out.print(name);
                int pad = name.length() < spaces.length()
                        ? name.length()
                        : spaces.length() - 1;
                System.out.print(spaces.substring(pad));
                System.out.print("= ");
                System.out.println(value);
            }
            System.out.println();
        }
        
        if (exit)
        {
            System.exit(0);
        }
        
        checkSystem();
        initSystem();
        List<String> paths = java.util.Arrays.asList(options.getArgs());
        List<File> files = checkFiles(paths);
        processFiles(files);
    }

    /**
     */
    private void checkSystem()
    {
    }

    /**
     */
    private void initSystem()
    {
    }

    /**
     * Check the list of paths to ensure they are valid and then return a
     * list of {@link java.io.File} objects for further processing.
     *
     * @param files List of file paths to check for access and existence.
     * @return List of {@link java.io.File} objects.
     * @exception IOException Any unhandled IO Exceptions.
     */
    private List<File> checkFiles(List<String> files) throws IOException
    {
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.Main");
        List<File> ret = new java.util.ArrayList<File>();
        ListIterator<String> paths = files.listIterator();
        while (paths.hasNext())
        {
            File file;
            String path = paths.next();
            if (path.startsWith(File.separator))
            {
                file = new File(path);
            }
            else
            {
                file = new File(System.getProperty("user.dir"), path);
            }

            Object[] args = {file};
            if (!file.exists())
            {
                String msg = MessageFormat.format(bundle.getString("PathMissing"), args);
                System.out.println(msg);
            }
            else if (!file.isFile())
            {
                String msg = MessageFormat.format(bundle.getString("PathNotNormalFile"), args);
                System.out.println(msg);
            }
            else if (!file.canRead())
            {
                String msg = MessageFormat.format(bundle.getString("PathSecurityViolation"), args);
                System.out.println(msg);
            }
            else
            {
                ret.add(file);
            }
        }
        return ret;
    }

    /**
     * Process each file in the TBX system for validity.
     *
     * @param files List of {@link java.io.File} objects to process.
     * @throws IOException Any I/O exceptions that occur.
     */
    private void processFiles(List<File> files) throws IOException, SAXException
    {
        Configuration config = new Configuration();
        config.setCheckLang(!options.containsOption("--nolang"));
    
        Iterator<File> iter = files.iterator();
        while (iter.hasNext())
        {
            File file = iter.next();
            TBXFile dv = new TBXFile(file.toURI().toURL(), (Configuration) config.clone());
            dv.parseAndValidate();
            if (dv.isValid())
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.Main");
                Object[] args = {file};
                String msg = MessageFormat.format(bundle.getString("FileValid"), args);
                System.out.println(msg);
            }
            else
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.Main");
                Object[] args = {file.getCanonicalPath()};
                String msg = MessageFormat.format(bundle.getString("FileInvalid"), args);
                System.out.println(msg);
                
                Iterator errs = dv.getInvalidatingExceptions().iterator();
                while (errs.hasNext())
                {
                    TBXException err = (TBXException) errs.next();
                    System.err.println(err.getLocalizedMessage());
                    LOGGER.log(Level.FINER, "", err);
                }
            }
        }
    }
}

