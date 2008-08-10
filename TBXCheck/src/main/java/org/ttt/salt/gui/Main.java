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
package org.ttt.salt.gui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.UIManager;
import org.flyingtitans.util.GetOpt;

/**
 * This is the main entry point for the "TBX Editor" IDE.
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
    public static final String RCSID = "$Id$";

    /** Single character options. */
    private static final String OPTIONS = "h";

    /** Long options. */
    private static final String[] LONG_OPTIONS = {"help", "version",
            "environment", "lang=", "country=", "variant="};

    /** Single allowed instance of this Class. */
    private static Main instance;
        
    /**
     * Main entry point for the application.
     *
     * @param argv Command line arguments.
     * @exception IOException Any unhandled IO Exceptions.
     */
    public static void main(String[] argv) throws IOException
    {
        //CHECKSTYLE: IllegalCatch OFF
        try
        {
            instance = new Main(argv);
            Main obj = getInstance();
            obj.dispatch();
        }
        catch (SecurityException e)
        {
            System.err.println(e);
        }
        catch (Throwable e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        //CHECKSTYLE: IllegalCatch ON
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

    /** Localized resource bundle to be used for this class. */
    private final ResourceBundle bundle;

    /** Number of primary windows open. */
    private int fileCount;

    /** Indicates it is time to quit. */
    private boolean quit;

    /**
     * Construct single instance of Main.
     *
     * @param argv Command line arguments.
     */
    private Main(String[] argv)
    {
        try
        {
            options.parseArgs(argv, OPTIONS, LONG_OPTIONS);
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
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Illegal command line parameter.");
            System.err.println("Use -h for more information.");
            System.exit(2);
        }
        bundle = ResourceBundle.getBundle("org.ttt.salt.gui.Main",
                Locale.getDefault());
    }

    /**
     * Notify all parts of the application that the application is quitting
     * and will terminate.
     */
    public void signalQuit()
    {
        if (!quit)
        {
            synchronized (this)
            {
                quit = true;
                notifyAll();
            }
        }
    }
    
    /**
     * Dispatch based on the settings determined by arguments.
     *
     * @exception IOException Any unhandled IO Exceptions.
     */
    private void dispatch() throws IOException
    {
        boolean exit = false;
        
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
            Iterator<Object> iter = env.keySet().iterator();
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
        TBXValidator.validateFiles(files);
        TBXValidator.getInstance().setVisible(true);
        synchronized (this)
        {
            boolean done = false;
            while (!done)
            {
                try
                {
                    while (!quit)
                        wait();
                    done = TBXValidator.getInstance().closeAllWindows();
                    quit = false;
                }
                catch (InterruptedException err)
                {
                    Thread.interrupted();
                }
            }
            System.exit(0);
        }
    }

    /**
     * Check to see if the system has the resources necessary to run this
     * application.
     */
    private void checkSystem()
    {
    }

    /**
     * Do any system initialization such as platform specific initializations.
     */
    private void initSystem()
    {
        //CHECKSTYLE: IllegalCatch OFF
        try
        {
            if (System.getProperty("os.name").equals("Mac OS X"))
            {
                new Aqua();
            }
            String name = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(name);
            //Preferences.getInstance().setDefaults();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //CHECKSTYLE: IllegalCatch ON
    }

    /**
     * Check all the given file paths to ensure that each is a valid and
     * readable file.
     *
     * @param files A list of paths to potential files.
     * @return A list of valid files that may be processed.
     * @throws IOException There was a problem with the path.
     */
    private List<File> checkFiles(List<String> files) throws IOException
    {
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
            if (!file.exists())
            {   //tell user file does not exist
                System.out.println("File does not exists: " + file);
            }
            else if (!file.canRead())
            {   //tell user read permission violated
                System.out.println("Security violation: " + file);
            }
            else
            {
                ret.add(file);
            }
        }
        return ret;
    }
}

