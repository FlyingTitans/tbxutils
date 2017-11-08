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
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * This is the main frame to allow files to be validated and the results
 * displayed.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public final class TBXValidator extends JFrame implements WindowListener
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Prefix for temporary files. */
    private static final String PREFIX = "TBX";

    /** Suffix for temporary files. */
    private static final String SUFFIX = ".xml";
        
    /** Singleton instance of this class. */
    private static final TBXValidator INSTANCE = new TBXValidator();
    
    /**
     * Get the single instance of the validator GUI interface.
     *
     * @return Singleton instance of this class.
     */
    public static TBXValidator getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * Open a specific file. If the file is null then this will create a
     * new TBX file.
     *
     * @param file File to be validated.
     * @throws IOException An I/O exception occured.
     */
    public static void validateFile(File file) throws IOException
    {
        checkSecurity(file, false);
    }
    
    /**
     * Validate the collection of files and show the results.
     *
     * @param files The collection of TBX files to be validated.
     * @throws IOException An I/O exception occured.
     */
    public static void validateFiles(Collection<File> files) throws IOException
    {
        Iterator<File> paths = files.iterator();
        while (paths.hasNext())
        {
            File file = paths.next();
            validateFile(file);
        }
    }

    /**
     * Check read and write access on a file, and if there is a problem throw
     * a {@link java.lang.SecurityException}.
     *
     * @param f The file to check.
     * @param write Write permissions as well as read permissions need to be checked.
     * @throws IOException An I/O exception occured.
     */
    private static void checkSecurity(File f, boolean write) throws IOException
    {
        if (f != null)
        {
            if (f.isDirectory())
            {
                String msg = "Path {0} is a directory.";
                Object[] arg = {f.getAbsolutePath()};
                String fmt = MessageFormat.format(msg, arg);
                throw new SecurityException(fmt);
            }
            if (f.exists())
            {
                if (!f.canRead())
                {
                    String msg = "You do not have read permission to {0}.";
                    Object[] arg = {f.getAbsolutePath()};
                    String fmt = MessageFormat.format(msg, arg);
                    throw new SecurityException(fmt);
                }
                if (write && !f.canWrite())
                {
                    String msg = "You do not have write permission to {0}.";
                    Object[] arg = {f.getAbsolutePath()};
                    String fmt = MessageFormat.format(msg, arg);
                    throw new SecurityException(fmt);
                }
            }
            else
            {
                String msg = "File does not exist: {0}.";
                Object[] arg = {f.getAbsolutePath()};
                String fmt = MessageFormat.format(msg, arg);
                throw new IOException(fmt);
            }
        }
    }
    
    /** The properties for this validator. */
    private final Properties properties = new Properties();
    
    /** Collection of all windows in the validator. */
    private final Collection<JFrame> windows = new java.util.HashSet<JFrame>();

    /**
     * This will a validator to GUI interface.
     */
    private TBXValidator()
    {
        super();
        ResourceBundle bndl = ResourceBundle.getBundle("org.ttt.salt.gui.TBXValidator");
        TBXAbstractAction.setResourceBundle(bndl);
        
        setTitle(bndl.getString("Title"));
        setLayout(new BorderLayout());
        add(new TBXValidatorPanel(bndl));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
//        JFileChooser c = new JFileChooser();
//        c.showOpenDialog(null);
        
//        FileDialog d = new FileDialog(this);
//        d.setVisible(true);
        
    }

    /**
     * Get a value for a property in the validator.
     *
     * @param key The key for the property to access.
     * @return The value for the associated property or null if the property
     *  key is not in the validator.
     */
    public Object getProperty(String key)
    {
        return properties.get(key);
    }
    
    /**
     * Set a validator property.
     *
     * @param key The key for the property to set.
     * @param value The new value for the property.
     */
    public void putProperty(String key, String value)
    {
        properties.put(key, value);
    }

    /** {@inheritDoc} */
    public void windowOpened(WindowEvent evt)
    {
        JFrame win = (JFrame) evt.getWindow();
        windows.add(win);
    }

    /** {@inheritDoc} */
    public void windowClosed(WindowEvent evt)
    {
        JFrame win = (JFrame) evt.getWindow();
        windows.remove(win);
    }

    /** {@inheritDoc} */
    public void windowClosing(WindowEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void windowActivated(WindowEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void windowDeactivated(WindowEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void windowDeiconified(WindowEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void windowIconified(WindowEvent evt)
    {
    }
        
    /**
     * Close all editing windows that have been opened.
     *
     * @return true => all windows are closed.
     */
    public boolean closeAllWindows()
    {
        Object[] wins = windows.toArray();
        for (int i = 0; i < wins.length; i++)
        {
            JFrame frame = (JFrame) wins[i];
            frame.toFront();
            frame.dispatchEvent(
                new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            switch (frame.getDefaultCloseOperation())
            {
                case EXIT_ON_CLOSE: frame.dispose(); break;
                case DISPOSE_ON_CLOSE: frame.dispose(); break;
                case DO_NOTHING_ON_CLOSE: break;
                case HIDE_ON_CLOSE: frame.setVisible(false); break;
                default:
                    throw new UnsupportedOperationException(
                        "Unknown JFrame default close operation: "
                        + frame.getDefaultCloseOperation());
            }
        }
        return windows.isEmpty();
    }
}

