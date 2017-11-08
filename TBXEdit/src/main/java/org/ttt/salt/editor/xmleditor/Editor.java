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
package org.ttt.salt.editor.xmleditor;

import java.io.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import org.ttt.salt.editor.preferences.*;
import org.flyingtitans.swing.ExtensionFileFilter;
import org.flyingtitans.swing.text.IntegerFilterDocument;

/**
 * This associates a file being edited with the Document and JEditorPanes
 * looking at the document.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Editor extends Observable implements DocumentListener
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Observer argument that indicates a title change. */
    public static final String TITLE_CHANGE = "TitleChange";

    /** Prefix for temporary files. */
    private static final String PREFIX = "ftedit";

    /** Suffix for temporary files. */
    private static final String SUFFIX = null;

    /** Naming number */
    private static int nameNumber = 1;

    /** Original file: <strong>not edited directly</strong>. */
    private File original;

    /** Mime type of the file. */
    private String mimeType;

    /** Editor kit for this mimetype */
    private EditorKit editorKit;

    /** Document that is editing the file. */
    private Document document;

    /** Undo manager for the document. */
    private UndoManager undo = new UndoManager();

    /** Dirty document indicator */
    private boolean dirty = false;

    /**
     * This will create an Editor that does not have a file name to start
     * with.
     */
    public Editor() throws IOException
    {
        original = null;
        mimeType = "text/plain";
        editorKit = createEditorKitForMimeType(mimeType);
        document = editorKit.createDefaultDocument();
        document.addUndoableEditListener(undo);
        document.addDocumentListener(this);
        document.putProperty(Document.TitleProperty, getTitle());
    }

    /**
     * This will create a new editor with the given File as the original.
     *
     * @param   f       File object pointing to file to edit.
     * @throws  IOException Any I/O exceptions that occur.
     * @throws  SecurityException There is a problem opening the file.
     */
    public Editor(File f) throws IOException, SecurityException
    {
        original = f;
        checkSecurity(f, false);
        mimeType = whatMimeType(original);
        editorKit = createEditorKitForMimeType(mimeType);
        document = editorKit.createDefaultDocument();
        document.addDocumentListener(this);
        readDocument(original, 0);
        dirty = false;
        document.addUndoableEditListener(undo);
        document.putProperty(Document.TitleProperty, getTitle());
    }
    
    /**
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * Has the document changed at all.
     */
    public boolean isDirty()
    {
        return dirty;
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
                if (document.getLength() == 0 && !isDirty())
                {
                    readDocument(file, 0);
                    dirty = false;
                    original = file;
                    document.putProperty(Document.TitleProperty, getTitle());
                    setChanged();
                    notifyObservers(TITLE_CHANGE);
                }
                else
                {
                    Editor editor = new Editor(file);
                    EditorWindow editwin = new EditorWindow(editor);
                }
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
     * Insert a file into the current document.
     *
     * @param caret Location to start the insertion of the file.
     * @param primary The component that the insert dialog should be a
     *  secondary window for.
     */
    public void insert(Caret caret, JComponent primary)
    {
        JFileChooser choose = new JFileChooser(original);
        addFileFilters(choose);
        int res = choose.showOpenDialog(primary);
        if (res == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = choose.getSelectedFile();
                int dot = caret.getDot();
                readDocument(file, dot);
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
     * @param ewin The EditorWindow that generated the event.
     */
    public void save(EditorWindow ewin)
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
                    writeDocument(original);
                }
                catch (SecurityException err)
                {
                    Object[] parms = {
                        getDocument().getProperty(Document.TitleProperty)
                    };
                    String title = ewin.getBundle().getString(
                            "WritePermsAlertTitle");
                    String msg = MessageFormat.format(
                        ewin.getBundle().getString("WritePermsAlertMessage") +
                        ewin.getBundle().getString("WritePermsAlertQuestion"),
                        parms);
                    int option = JOptionPane.showConfirmDialog(
                            ewin.getPrimaryComponent(), msg, title,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    if (option == JOptionPane.YES_OPTION)
                    {
                        if (addWritePerms(original.getCanonicalPath(), ewin))
                            writeDocument(original);
                    }
                }
                dirty = false;
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
     * @param ewin The EditorWindow that generated the event.
     */
    public void saveAs(EditorWindow ewin)
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
                if (file != original)
                {
                    writeDocument(file);
                    String newmime = whatMimeType(file);
                    if (newmime != mimeType)
                    {
                        mimeType = newmime;
                        editorKit = createEditorKitForMimeType(mimeType);
                        document = editorKit.createDefaultDocument();
                        readDocument(file, 0);
                        document.addUndoableEditListener(undo);
                    }
                    dirty = false;
                    original = file;
                    document.putProperty(Document.TitleProperty, getTitle());
                    setChanged();
                    notifyObservers(TITLE_CHANGE);
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
     * @param ewin The EditorWindow that generated the event.
     */
    public void saveCopy(EditorWindow ewin)
    {
        JFileChooser choose = new JFileChooser(original);
        addFileFilters(choose);
        if (original != null)
            choose.setSelectedFile(original);
        int res = choose.showSaveDialog(ewin.getPrimaryComponent());
        if (res == JFileChooser.APPROVE_OPTION)
        {
            File path = null;
            try
            {
                path = choose.getSelectedFile();
                checkSecurity(path, true);
                FileWriter out = new FileWriter(path);
                try
                {
                    editorKit.write(out, document, 0, document.getLength());
                }
                catch (BadLocationException err)
                {
                    System.err.println("UNEXPECTED INTERNAL ERROR");
                    err.printStackTrace(System.err);
                }
                out.close();
            }
            catch (IOException err)
            {
                System.err.println("NOT DONE: IOException occured.");
            }
            catch (SecurityException err)
            {
                alertSecurityViolation(ewin, err, path);
            }
        }
    }

    /**
     * Throw away all the changes since the last save and reload the file.
     */
    public void revert(EditorWindow ewin)
    {
        try
        {
            checkSecurity(original, false);
            document.remove(0, document.getLength());
            readDocument(original, 0);
            dirty = false;
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
        printjob.setJobName(
                (String) document.getProperty(Document.TitleProperty));
        printjob.setPageable(printer);
        printjob.print();
    }

    /**
     * Get the document that this editor is using.
     */
    public Document getDocument()
    {
        return document;
    }

    /**
     * Get the undo manager that is in use on this document.
     */
    public UndoManager getUndo()
    {
        return undo;
    }

    /** {@inheritDoc} */
    public void changedUpdate(DocumentEvent evt)
    {
        dirty = true;
    }

    /** {@inheritDoc} */
    public void insertUpdate(DocumentEvent evt)
    {
        dirty = true;
    }

    /** {@inheritDoc} */
    public void removeUpdate(DocumentEvent evt)
    {
        dirty = true;
    }

    /**
     */
    private void addFileFilters(JFileChooser choose)
    {
        javax.swing.filechooser.FileFilter usefilter =
            choose.getFileFilter();
        Collection extns = ExtensionFileFilter.createFileFilters(
                ResourceBundle.getBundle("org.ttt.salt.editor.xmleditor.Editor"));
        SortedSet sort = new TreeSet(extns);
        Iterator filters = sort.iterator();
        while (filters.hasNext())
        {
            javax.swing.filechooser.FileFilter filter =
                    (javax.swing.filechooser.FileFilter) filters.next();
            choose.addChoosableFileFilter(filter);
            if (filter.accept(original))
            {
                usefilter = filter;
            }
        }
        choose.setFileFilter(usefilter);
    }

    /**
     * Check read and write access on a file.
     *
     */
    private static void checkSecurity(File f, boolean write)
        throws IOException, SecurityException
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
     */
    private static void alertSecurityViolation(EditorWindow ewin,
        SecurityException err, File file)
    {
        Object[] parms = {file};
        String title = ewin.getBundle().getString(
                "WritePermsAlertTitle");
        String msg = MessageFormat.format(
                ewin.getBundle().getString("WritePermsAlertMessage"),
                parms);
        JOptionPane.showMessageDialog(ewin.getPrimaryComponent(),
                msg, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     */
    private static boolean addWritePerms(String path, EditorWindow ewin)
        throws IOException
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
     * Read the given file into the document at the character position.
     */
    private void readDocument(File path, int dot) throws IOException,
        SecurityException
    {
        try
        {
            checkSecurity(path, false);
            FileReader in = new FileReader(path);
            editorKit.read(in, document, dot);
            in.close();
        }
        catch (BadLocationException err)
        {
            System.err.println("PROGRAMMER FAULT");
            err.printStackTrace(System.err);
        }
    }

    /**
     * Write the document to the given file.
     */
    private void writeDocument(File file) throws IOException,
        SecurityException
    {
        checkSecurity(file, true);
        FileWriter out = new FileWriter(file);
        try
        {
            editorKit.write(out, document, 0, document.getLength());
        }
        catch (BadLocationException err)
        {
            System.err.println("UNEXPECTED INTERNAL ERROR");
            err.printStackTrace(System.err);
        }
        out.close();
    }

    /**
     * Get the title.
     */
    private String getTitle()
    {
        String title;
        if (original != null)
        {
            try
            {
                title = original.getCanonicalPath();
            }
            catch (IOException err)
            {
                title = "";
            }
        }
        else
        {
            title = "Unknown <" + nameNumber + ">";
            nameNumber++;
        }
        return title;
    }

    /**
     * Copy from one file to another, paying attention to the mime type so
     * eol handling is handled correctly.
     */
    private static void copyFile(File f1, File f2, String mime)
        throws IOException
    {
        String eol = System.getProperty("line.separator");
        boolean cr = false;
        InputStream in = new FileInputStream(f1);
        DataOutputStream out = new DataOutputStream(
                new FileOutputStream(f2));
        int c = in.read();
        while (c != -1)
        {
            if (c == '\r')
            {
                cr = true;
                out.writeBytes(eol);
            }
            else if (c == '\n')
            {
                if (!cr)
                    out.writeBytes(eol);
                cr = false;
            }
            else
            {
                out.write((char) c);
                cr = false;
            }
            c = in.read();
        }
        in.close();
        out.close();
    }

    /**
     * Determine the mime type for the given file. This will determine the
     * type from the extension; if the extension is unknown then the
     * <code>file</code> magic will be used to determine the file type.
     */
    private static String whatMimeType(File f) throws IOException
    {
        String ret = "text/plain";
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.MimeTypes");
            String name = f.getName().toLowerCase();
            String ext = name.substring(name.lastIndexOf('.') + 1,
                name.length());
            ret = bundle.getString(ext);
        }
        catch (MissingResourceException err)
        {
            System.out.println(err);
        }
        return ret;
    }

    /**
     */
    private static EditorKit createEditorKitForMimeType(String mimeType)
    {
        EditorKit kit = JEditorPane.createEditorKitForContentType(mimeType);
        //kit = JEditorPane.createEditorKitForContentType("text/plain");
        if (kit == null)
        {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "org.ttt.salt.editor.MimeTypes");
            String clazzname = bundle.getString(mimeType);
            JEditorPane.registerEditorKitForContentType(mimeType,
                    bundle.getString(mimeType), EditorClassLoader.getInstance());
            kit = JEditorPane.createEditorKitForContentType(mimeType);
            if (kit == null)
            {
                kit = JEditorPane.createEditorKitForContentType("text/plain");
            }
        }
        return kit;
    }

    /**
     * Defines a printable that will render the individual error pages as
     * necessary.
     */
    private class Printer implements Pageable, Printable
    {
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
    }
}

