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

import java.awt.Font;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Date;
import java.awt.Graphics;
import java.awt.print.Printable;
import java.awt.print.Pageable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import org.ttt.salt.TBXFile;
import org.ttt.salt.TBXException;

/**
 * This is the main frame to allow files to be validated and the results
 * displayed.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXResults extends javax.swing.JFrame implements WindowListener
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    public static final String TITLE_PROPERTY = "Title";

    /** Observer argument that indicates a title change. */
    public static final String TITLE_CHANGE = "TitleChange";
    
    /* Components */
    private JScrollPane scroll;

    /**
     * This will display the results of validating the file.
     *
     * @param file The file that was checked.
     * @param tbx The TBX validator that operated on the file.
     * @param log The full log of all that happened in the parse.
     */
    public TBXResults(File file, TBXFile tbx, String log)
    {
        super(String.format("%1s %2$tF %2$tT", file.getName(), new Date()));
        ResourceBundle bndl = ResourceBundle.getBundle("org.ttt.salt.gui.TBXValidator");
        javax.swing.JTextArea text = new javax.swing.JTextArea();
        text.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        if (tbx != null)
        {
            Iterator<TBXException> errs = tbx.getInvalidatingExceptions().iterator();
            while (errs.hasNext())
            {
                TBXException err = errs.next();
                text.append(err.getLocalizedMessage());
                text.append("\n");
            }
        }
        text.append("\n");
        text.append("****************************************\n");
        text.append("Log messages.\n\n");
        text.append(log);
        setTextProperties(text);

        scroll = new JScrollPane(text,
                    javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        //scroll.setRowHeaderView(new javax.swing.Box.Filler(
        //                                new java.awt.Dimension(10, 10),
        //                                new java.awt.Dimension(10, 10),
        //                                new java.awt.Dimension(10, 10)));
        //scroll.setPreferredSize(new java.awt.Dimension(250, 250));
        add(scroll);
        addWindowListener(this);
        addWindowListener(TBXValidator.getInstance());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }
    
    /**
     * Setup the display properties for the text area.
     *
     * @param text The {@link javax.swing.JTextArea} to set properties on.
     */
    private void setTextProperties(javax.swing.JTextArea text)
    {   //NOTDONE: This needs to get these values from preferences
        final int fontSize = 12;
        final int columns = 80;
        final int rows = 50;
    
        text.setFont(new Font("Serif", Font.PLAIN, fontSize));
        text.setColumns(columns);
        text.setRows(rows);
        //text.setLineWrap(true);
        //text.setWrapStyleWord(true);
    }
        

    /** {@inheritDoc} */
    public void windowOpened(WindowEvent evt)
    {
        scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMinimum());
        scroll.getHorizontalScrollBar().setValue(scroll.getHorizontalScrollBar().getMinimum());
    }

    /** {@inheritDoc} */
    public void windowClosed(WindowEvent evt)
    {
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
     * Save the file.
     *
     * If the file is new and has never been saved then this will cause
     * saveAs to save the file.
     */
    public void save()
    {
    /*
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
                    tbxfile.write(original);
                }
                catch (SecurityException err)
                {
                    Object[] parms = {getProperty(TITLE_PROPERTY)};
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
                        {
                            tbxfile.write(original);
                        }
                    }
                }
            }
        }
        catch (IOException err)
        {
            System.err.println("NOT DONE: IOException occured.");
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
}

