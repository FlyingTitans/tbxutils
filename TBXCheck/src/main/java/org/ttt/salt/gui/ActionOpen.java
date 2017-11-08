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

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import org.ttt.salt.Configuration;
import org.ttt.salt.TBXFile;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class ActionOpen extends TBXAbstractAction implements FilenameFilter
{
    /*
     */
    
    /**
     * Format log messages in a manner that is easier for non-programmer
     * (i.e. normal humans) to read.
     *
     * @author Lance Finn Helsten
     * @version $Id$
     * @license Licensed under the Apache License, Version 2.0.
     */
    private class LogFormatter extends Formatter
    {
        /** Builder to use on each log record. */
        private StringBuilder builder = new StringBuilder();
        
        /** {@inheritDoc} */
        public String format(LogRecord record)
        {
            String errmsg = "";
            String fmt;
            if (record.getThrown() != null)
            {
                fmt = "#%3$2d  %4$s---[%1$s#%2$s] %5$s%n\tException: %6$s%n";
                errmsg = record.getThrown().getLocalizedMessage();
            }
            else if (record.getLevel().intValue() > Level.FINE.intValue())
            {
                fmt = "#%3$2d  %4$s---%5$s%n";
            }
            else
            {
                fmt = "#%3$2d  %4$s---[%1$s#%2$s] %5$s%n";
            }
            return String.format(fmt,
                    record.getSourceClassName(), record.getSourceMethodName(),
                    record.getSequenceNumber(),
                    record.getLevel().getLocalizedName(),
                    formatMessage(record), errmsg);
        }
    }

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Preferences key for the last directory the user opened a file from. */
    private static final String DIRECTORY = "ActionOpen_Directory";
        
    /** Formatter for the log messages. */
    private Formatter formatter;
    
    /** Handler for log messages. */
    private Handler handler;
    
    /** Level for the logging. */
    private Level level;
    
    /** Flag for xml:lang validation. */
    private boolean checkXmlLang = true;
    
    /** Buffer for log messages. */
    private ByteArrayOutputStream logbuffer;
    
    /**
     */
    public ActionOpen()
    {
        super("ActionOpen");
        try
        {
            logbuffer = new ByteArrayOutputStream();
            //formatter = new java.util.logging.SimpleFormatter();
            formatter = new LogFormatter();
            handler = new java.util.logging.StreamHandler(logbuffer, formatter);
            handler.setLevel(Level.FINEST);
            handler.setEncoding("UTF-8");
        }
        catch (java.io.UnsupportedEncodingException err)
        {   //Ignore: UTF-8 is always available
            err.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("ActionOpen"))
        {
            FileDialog d = new FileDialog(JOptionPane.getRootFrame(),
                                getResourceBundle().getString("ActionOpenFileDialogTitle"),
                                FileDialog.LOAD);
            Preferences pref = Preferences.userNodeForPackage(getClass());
            d.setDirectory(pref.get(DIRECTORY, System.getProperty("user.home")));
            d.setFilenameFilter(this);
            d.setVisible(true);
            pref.put(DIRECTORY, d.getDirectory());
            if (d.getFile() != null)
            {
                File file = new File(d.getDirectory(), d.getFile());
                Object[] args = {file};
                if (!file.exists())
                {
                    JOptionPane.showMessageDialog(null,
                        MessageFormat.format(getResourceBundle().getString("PathMissing"), args),
                        getResourceBundle().getString("PathMissingTitle"),
                        JOptionPane.ERROR_MESSAGE);
                }
                else if (!file.isFile())
                {
                    JOptionPane.showMessageDialog(null,
                        MessageFormat.format(getResourceBundle().getString("PathNotNormalFile"), args),
                        getResourceBundle().getString("PathNotNormalFileTitle"),
                        JOptionPane.ERROR_MESSAGE);
                }
                else if (!file.canRead())
                {
                    JOptionPane.showMessageDialog(null,
                        MessageFormat.format(getResourceBundle().getString("PathSecurityViolation"), args),
                        getResourceBundle().getString("PathSecurityViolationTitle"),
                        JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    logbuffer.reset();
                    Logger.getLogger("org.ttt.salt").addHandler(handler);
                    Logger.getLogger("org.ttt.salt").setLevel(level);
                    Logger.getLogger("org.ttt.salt.dom.tbx").setLevel(level);
                    Logger.getLogger("org.ttt.salt.dom.xcs").setLevel(level);
                    
                    TBXFile dv = null;
                    try
                    {
                        Configuration config = new Configuration();
                        config.setCheckLang(checkXmlLang);
                        
                        dv = new TBXFile(file.toURI().toURL(), config);
                        dv.parseAndValidate();
                        if (dv.isValid())
                        {
                            JOptionPane.showMessageDialog(null,
                                MessageFormat.format(getResourceBundle().getString("FileValid"), args),
                                getResourceBundle().getString("FileValidTitle"),
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch (IOException err)
                    {
                        String msg = MessageFormat.format(
                                getResourceBundle().getString("IOException"),
                                err.getLocalizedMessage(), file);
                        JOptionPane.showMessageDialog(null, msg,
                            getResourceBundle().getString("IOExceptionTitle"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    //CHECKSTYLE: IllegalCatch OFF
                    catch (Throwable err)
                    {
                        String msg = MessageFormat.format(
                                getResourceBundle().getString("UnknownError"),
                                err.getLocalizedMessage(), file);
                        JOptionPane.showMessageDialog(null, msg,
                            getResourceBundle().getString("UnknownErrorTitle"),
                            JOptionPane.ERROR_MESSAGE);
                        System.err.format("Unknown error for file %s%n", file);
                        err.printStackTrace();
                    }
                    //CHECKSTYLE: IllegalCatch ON
                    finally
                    {
                        try
                        {
                            handler.flush();
                            String log = logbuffer.toString("UTF-8");
                            Logger.getLogger("org.ttt.salt").removeHandler(handler);
                            new TBXResults(file, dv, log);
                        }
                        catch (java.io.UnsupportedEncodingException err)
                        {   //Ignore: UTF-8 is always available
                            err.printStackTrace();
                        }
                    }
                }
            }
        }
        else if (e.getActionCommand().equals("NoLangCheckChanged"))
        {
            JCheckBox check = (JCheckBox) e.getSource();
            checkXmlLang = !check.isSelected();
        }
        else if (e.getActionCommand().equals("comboBoxChanged"))
        {
            try
            {
                JComboBox cbox = (JComboBox) e.getSource();
                String selstr = (String) cbox.getSelectedItem();
                level = Level.parse(selstr);
            }
            catch (NullPointerException err)
            {   //Invalid level string so don't change anything
                err.printStackTrace();
            }
            catch (IllegalArgumentException err)
            {   //Invalid level string so don't change anything
                err.printStackTrace();
            }
        }
    }
    
    /** {@inheritDoc} */
    public boolean accept(File dir, String name)
    {
        return name.matches(".+?\\.((xml)|(XML)|(tbx)|(TBX))");
    }
}
