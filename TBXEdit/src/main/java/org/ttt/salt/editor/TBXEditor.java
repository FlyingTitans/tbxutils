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
package org.ttt.salt.editor;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.SortedSet;
import java.util.Map;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Observable;
import java.awt.Graphics;
import java.awt.print.Printable;
import java.awt.print.Pageable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.EditorKit;
import org.ttt.salt.TBXFile;
import org.ttt.salt.editor.tbxedit.TBXWindow;

/**
 * This is the main class for the TBXEditor. It handles mundane activities
 * such as holding on to the DOM Document and knowing the main TBX editing
 * window for that document.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class TBXEditor extends Observable
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    public static final String TITLE_PROPERTY = "Title";

    /** Observer argument that indicates a title change. */
    public static final String TITLE_CHANGE = "TitleChange";

    /** Prefix for temporary files. */
    private static final String PREFIX = "TBX";

    /** Suffix for temporary files. */
    private static final String SUFFIX = ".xml";
    
    /**
     * Open a specific file. If the file is null then this will create a
     * new TBX file.
     *
     * @param file The {@link java.io.File} to a TBX file to open.
     * @throws IOException Any I/O exceptions that occur.
     */
    public static void open(File file) throws IOException
    {
        if (file == null)
        {
            TBXEditor editor = new TBXEditor();
            TBXWindow tbx = new TBXWindow(editor);
            editor.window = tbx;
            tbx.setVisible(true);
        }
        else
        {
            TBXFile tbxfile = new TBXFile(file);
            tbxfile.parseAndValidate();
            if (tbxfile.isValid())
            {
                TBXEditor editor = new TBXEditor(file, tbxfile);
                TBXWindow tbx = new TBXWindow(editor);
                editor.window = tbx;
                tbx.setVisible(true);
            }
            else
            {
                ErrorsWindow errors = new ErrorsWindow(tbxfile);
                errors.setVisible(true);
            }
        }
    }
    
    /** Properties of this editor. */
    private Map<String, Object> properties = new java.util.HashMap<String, Object>();
    
    /** File that tbxdocument is written to. */
    private File original;

    /** TBX master document. */
    private TBXDocument tbxdocument;
    
    /** Window the TBX file is displayed in. */
    private TBXWindow window;

    /**
     * This will create a new editor with an unamed file.
     *
     * @throws IOException Any I/O exceptions that occur.
     */
    public TBXEditor() throws IOException
    {
        File file = createUnamedTBX();
        tbxdocument = new TBXDocument(file);
        tbxdocument.getTBXFile().isValid();
        putProperty(TITLE_PROPERTY, "Unnamed");
    }

    /**
     * This will create a new editor with the given File as the original.
     *
     * @param tbxfile Validated TBXFile to edit.
     * @throws IOException Any I/O exceptions that occur.
     */
    public TBXEditor(TBXFile tbxfile) throws IOException
    {
        tbxdocument = new TBXDocument(tbxfile);
        putProperty(TITLE_PROPERTY, "Unnamed");
    }

    /**
     * This will create a new editor with the given File as the original.
     *
     * @param file The original file to write to.
     * @param tbxfile Validated TBXFile to edit.
     * @throws IOException Any I/O exceptions that occur.
     */
    public TBXEditor(File file, TBXFile tbxfile) throws IOException
    {
        this.original = file;
        tbxdocument = new TBXDocument(tbxfile);
        putProperty(TITLE_PROPERTY, file.getCanonicalPath());
    }
    
    /**
     * Get a property of this editor.
     *
     * @param key The key to the property.
     * @return The value of the property or null if property does not exists.
     */
    public Object getProperty(String key)
    {
        return properties.get(key);
    }
    
    /**
     * Add or change a property of this editor.
     *
     * @param key The key to the property to change.
     * @param value The value for the property.
     */
    public void putProperty(String key, Object value)
    {
        properties.put(key, value);
    }
    
    /**
     * Has the document been changed, and needs to be saved.
     *
     * @return true => document has been changed.
     */
    public boolean isDirty()
    {
        return getTBXDocument().isDirty();
    }
    
    /**
     * Get the TBX document used in this editor.
     *
     * @return The TBX document.
     */
    public TBXDocument getTBXDocument()
    {
        return tbxdocument;
    }

    /**
     * Open a file.
     *
     * If the current document is empty and is not dirty that this will
     * open the file into this document and not open a new window.
     */
    public void open()
    {
        JFileChooser choose = new JFileChooser(original);
        addFileFilters(choose);
        int res = choose.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = choose.getSelectedFile();
                open(file);
                if (!isDirty())
                    window.close();
            }
            catch (IOException err)
            {
                System.err.println("NOT DONE: IOException occured.");
            }
            catch (SecurityException err)
            {
                System.err.println("NOT DONE: SecurityException occured.");
            }
        }
    }

    /**
     * Save the file.
     *
     * If the file is new and has never been saved then this will cause
     * saveAs to save the file.
     *
     * @param ewin The TBXWindow that generated the event.
     */
    public void save(TBXWindow ewin)
    {
        try
        {
            if (original == null)
            {
                saveAs(ewin);
            }
            else
            {
                try
                {
                    getTBXDocument().write(original);
                }
                catch (SecurityException err)
                {
                    Object[] parms = {getProperty(TITLE_PROPERTY)};
                    String title = ewin.getBundle().getString(
                            "WritePermsAlertTitle");
                    String msg = MessageFormat.format(
                        ewin.getBundle().getString("WritePermsAlertMessage")
                        + ewin.getBundle().getString("WritePermsAlertQuestion"),
                        parms);
                    int option = JOptionPane.showConfirmDialog(
                            ewin.getPrimaryComponent(), msg, title,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    if (option == JOptionPane.YES_OPTION)
                    {
                        if (addWritePerms(original.getCanonicalPath(), ewin))
                        {
                            getTBXDocument().write(original);
                        }
                    }
                }
            }
        }
        catch (IOException err)
        {
            System.err.println("NOT DONE: IOException occured.");
        }
    }

    /**
     * Save the document to a new file.
     *
     * This will save the document then it will replace the current document
     * with a new one with the contents of the old document.
     *
     * @param ewin The TBXWindow that generated the event.
     */
    public void saveAs(TBXWindow ewin)
    {
        JFileChooser choose = new JFileChooser(original);
        addFileFilters(choose);
        if (original != null)
            choose.setSelectedFile(original);
        int res = choose.showSaveDialog(ewin.getPrimaryComponent());
        if (res == JFileChooser.APPROVE_OPTION)
        {
            File file = null;
            try
            {
                file = choose.getSelectedFile();
                getTBXDocument().write(file);
                if (file != original)
                {
                    putProperty(TITLE_PROPERTY, file.getCanonicalPath());
                    setChanged();
                    notifyObservers(TITLE_CHANGE);
                    clearChanged();
                    original = file;
                }
            }
            catch (IOException err)
            {
                System.err.println("NOT DONE: IOException occured.");
            }
            catch (SecurityException err)
            {
                alertSecurityViolation(ewin, err, file);
            }
        }
    }

    /**
     * Save a copy of the document.
     *
     * This will save the document to a new file, but will not change the
     * document as it resides in memory.
     *
     * @param ewin The TBXWindow that generated the event.
     */
    public void saveCopy(TBXWindow ewin)
    {
        JFileChooser choose = new JFileChooser(original);
        addFileFilters(choose);
        if (original != null)
            choose.setSelectedFile(original);
        int res = choose.showSaveDialog(ewin.getPrimaryComponent());
        if (res == JFileChooser.APPROVE_OPTION)
        {
            File file = null;
            try
            {
                file = choose.getSelectedFile();
                getTBXDocument().write(file);
            }
            catch (IOException err)
            {
                System.err.println("NOT DONE: IOException occured.");
            }
            catch (SecurityException err)
            {
                alertSecurityViolation(ewin, err, file);
            }
        }
    }

    /**
     * Throw away all the changes since the last save and reload the file.
     *
     * @param ewin The window to revert.
     */
    public void revert(TBXWindow ewin)
    {
    /*
        try
        {
            checkSecurity(original, false);
            
            document.remove(0, document.getLength());
            readDocument(original, 0);
        }
        catch (BadLocationException err)
        {
            System.err.println("PROGRAMMER FAULT");
            err.printStackTrace(System.err);
        }
        catch (IOException err)
        {
            System.err.println("NOT DONE: IOException occured.");
        }
        catch (SecurityException err)
        {
            alertSecurityViolation(ewin, err, null);
        }
    */
    }

    /**
     * Print the document.
     *
     * @param printjob The printer job that this document needs to be printed
     *  to.
     * @throws PrinterException An exception in the printing system occurred.
     */
    public void print(PrinterJob printjob) throws PrinterException
    {
        Printer printer = new Printer(printjob);
        printjob.printDialog();
        //printjob.setJobName((String) document.getProperty(Document.TITLE_PROPERTY));
        printjob.setPageable(printer);
        printjob.print();
    }
    
    /**
     * Add file filters to the chooser.
     *
     * @param choose The file chooser in which filter are added.
     */
    private void addFileFilters(JFileChooser choose)
    {
        javax.swing.filechooser.FileFilter usefilter = choose.getFileFilter();
        Collection<ExtensionFileFilter> extns = ExtensionFileFilter.createFileFilters(
                ResourceBundle.getBundle("org.ttt.salt.editor.TBXEditor"));
        SortedSet<ExtensionFileFilter> sort = new java.util.TreeSet<ExtensionFileFilter>(extns);
        Iterator<ExtensionFileFilter> filters = sort.iterator();
        while (filters.hasNext())
        {
            javax.swing.filechooser.FileFilter filter = filters.next();
            choose.addChoosableFileFilter(filter);
            if (filter.accept(original))
                usefilter = filter;
        }
        choose.setFileFilter(usefilter);
    }

    /**
     * Check read and write access on a file.
     *
     * @param f The file to check.
     * @param write If set the write access should be checked also.
     * @throws IOException Any I/O exceptions that occur.
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

    /**
     * Display an alert showing a security violation on a specific file.
     *
     * @param ewin The root window to display the alert on.
     * @param err The security error to display.
     * @param file The file the error occured on.
     */
    private static void alertSecurityViolation(TBXWindow ewin, SecurityException err, File file)
    {
        Object[] parms = {file};
        String title = ewin.getBundle().getString("WritePermsAlertTitle");
        String msg = MessageFormat.format(
                ewin.getBundle().getString("WritePermsAlertMessage"),
                parms);
        JOptionPane.showMessageDialog(ewin.getPrimaryComponent(),
                msg, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Change the write permissions of a file.
     *
     * @param path The path to the file to modify.
     * @param ewin The root window for error alerts.
     * @return true => the permissions were changed.
     * @throws IOException Any I/O exceptions that occur.
     */
    private static boolean addWritePerms(String path, TBXWindow ewin) throws IOException
    {
        boolean ret = false;
        try
        {
            //PLATFORM: UNIX specific code in this block
            Runtime runtime = Runtime.getRuntime();
            Process chmod = runtime.exec("chmod u+w " + path);
            int res = chmod.waitFor();
            if (res == 0)
            {
                ret = true;
            }
            else
            {
                JOptionPane.showMessageDialog(ewin.getPrimaryComponent(),
                        ewin.getBundle().getString("ChangePermsErrorMessage"),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (InterruptedException e)
        {
            System.err.println("NOT DONE: InterruptedException occured.");
        }
        return ret;
    }

    /**
     * @param mimeType The mime type for the new editor kit.
     * @return The created editor kit.
     */
    private static EditorKit createEditorKitForMimeType(String mimeType)
    {
        EditorKit kit = JEditorPane.createEditorKitForContentType(mimeType);
        return kit;
    }

    /**
     * Defines a printable that will render the individual error pages as
     * necessary.
     */
    private class Printer implements Pageable, Printable
    {
        //CHECKSTYLE: RedundantThrows unchecked OFF
        /** The controlling object on this print run. */
        private PrinterJob printjob;

        /**
         * @param pj The controlling object for this print run.
         */
        public Printer(PrinterJob pj)
        {
            printjob = pj;
        }
        
        /** {@inheritDoc} */
        public int getNumberOfPages()
        {
            return UNKNOWN_NUMBER_OF_PAGES;
        }

        /** {@inheritDoc} */
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
        {
            return printjob.defaultPage();
        }

        /** {@inheritDoc} */
        public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
        {
            return this;
        }

        /** {@inheritDoc} */
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
        {
            System.out.println("print " + pageIndex);
            return pageIndex < 3 ? PAGE_EXISTS : NO_SUCH_PAGE;
        }
        //CHECKSTYLE: RedundantThrows unchecked ON
    }

    /**
     * This will create an Editor that does not have a file name to start
     * with.
     *
     * @return The {@link java.io.File} for the new empty TBX file.
     * @throws IOException Any I/O exceptions that occur.
     */
    private static File createUnamedTBX() throws IOException
    {
        File ret = File.createTempFile("UnamedTBX", ".xml");
        ret.deleteOnExit();
        PrintWriter out = new PrintWriter(new FileWriter(ret));
        out.println("<?xml version='1.0'?>");
        out.println("<!DOCTYPE martif");
        out.println("  PUBLIC 'ISO 12200:1999A//DTD MARTIF core (DXFcdV04)//EN'");
        out.println("  '../../../../dtd/XLTCDV04.DTD'>");
        out.println("<martif type='TBX' xml:lang='en'>");
        out.println("  <martifHeader>");
        out.println("    <fileDesc>");
        out.println("      <sourceDesc>");
        out.println("        <p>Unknown</p>");
        out.println("      </sourceDesc>");
        out.println("    </fileDesc>");
        out.println("    <encodingDesc>");
        out.println("      <p type='XCSName'>");
        out.println("        PUBLIC \"Demo XCS\" \"file://TBXDCSv05.xml\"");
        out.println("      </p>");
        out.println("    </encodingDesc>");
        out.println("  </martifHeader>");
        out.println("  <text>");
        out.println("    <body>");
        out.println("      <termEntry id='AUTO_termEntry_ID_0'>");
        out.println("        <langSet xml:lang='en'>");
        out.println("          <ntig>");
        out.println("            <termGrp>");
        out.println("              <term>");
        out.println("              </term>");
        out.println("            </termGrp>");
        out.println("          </ntig>");
        out.println("        </langSet>");
        out.println("      </termEntry>");
        out.println("    </body>");
        out.println("  </text>");
        out.println("</martif>");
        out.flush();
        out.close();
        return ret;
    }
}

