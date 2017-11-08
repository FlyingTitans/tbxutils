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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import org.flyingtitans.swing.text.IntegerFilterDocument;

/**
 * Editor to open a single file for editing in a single pane.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class EditorWindow extends JFrame implements WindowListener, Observer
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Prefix for temporary files. */
    private static final String PREFIX = "ftedit";

    /** Suffix for temporary files. */
    private static final String SUFFIX = null;

    /** Set of all editing windows open. */
    private static final Collection windows = new HashSet();

    /** resource bundle that is used by all menus */
    private static ResourceBundle bundle;

    /** */
    private final Editor editor;

    /** Menu Bar controller. */
    private final EditorMenuBar mbar;

    /**
     * Close all editing windows that have been opened.
     *
     * @return true => all windows are closed.
     */
    public static boolean closeAll()
    {
        Object[] wins = windows.toArray();
        for (int i = 0; i < wins.length; i++)
        {
            JFrame frame = (JFrame) wins[i];
            frame.toFront();
            frame.dispatchEvent(
                new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        return windows.isEmpty();
    }

    /**
     */
    public static ResourceBundle getBundle()
    {
        return bundle;
    }

    /**
     * This will create a new editor with the given File as the original.
     *
     * @param   editor Editor to extract the JEditorPane from.
     * @throws  IOException Any I/O exceptions that occur.
     * @throws  SecurityException There is a problem opening the file.
     */
    public EditorWindow(Editor editor) throws IOException, SecurityException
    {
        super("Unnamed");
        this.editor = editor;
        editor.addObserver(this);
        mbar = new EditorMenuBar(this);
        this.setJMenuBar(mbar.getMenuBar());
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        JScrollPane editpane = new EditorPane(editor, this);
        this.getContentPane().add(editpane, BorderLayout.CENTER);
        this.addWindowListener(this);
        //org.ttt.salt.editor.Main.getInstance().fileOpened();
        this.pack();
        this.setTitle(
            (String) editor.getDocument().getProperty(
                Document.TitleProperty));
        positionFrame();
        this.setVisible(true);
        editpane.getViewport().getView().requestFocus();
        windows.add(this);
    }

    /**
     * Send a message to close this window.
     */
    public void close()
    {
        this.dispatchEvent(
            new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Get the JComponent that should be used as the parent for secondary
     * windows.
     */
    public JComponent getPrimaryComponent()
    {
        return getRootPane();
    }

    /**
     * Get the editor that this window is associated with.
     */
    public Editor getEditor()
    {
        return editor;
    }
    
    /**
     * Get the menu bar for this editing window.
     */
    public EditorMenuBar getEditorMenu()
    {
        return mbar;
    }

    /** {@inheritDoc} */
    public void windowOpened(WindowEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void windowClosed(WindowEvent evt)
    {
        windows.remove(this);
        editor.deleteObserver(this);
        removeWindowListener(this);
        //org.ttt.salt.editor.Main.getInstance().fileClosed();
    }

    /** {@inheritDoc} */
    public void windowClosing(WindowEvent evt)
    {
        if (editor.isDirty())
        {
            String msg = getBundle().getString("SaveAsk");
            Object[] parms = {
                getEditor().getDocument().getProperty(Document.TitleProperty)
            };
            int res = JOptionPane.showConfirmDialog(getPrimaryComponent(),
                    MessageFormat.format(msg, parms),
                    " ", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            switch (res)
            {
                case JOptionPane.OK_OPTION:
                    getEditor().save(this);
                    setVisible(false);
                    dispose();
                    break;
                case JOptionPane.NO_OPTION:
                    setVisible(false);
                    dispose();
                    break;
                case JOptionPane.CANCEL_OPTION:
                    break;
                case JOptionPane.CLOSED_OPTION:
                    break;
            }
        }
        else
        {
            setVisible(false);
            dispose();
        }
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
    
    /** {@inheritDoc} */
    public void update(Observable o, Object arg)
    {
        if (o instanceof Editor)
        {
            if (arg == Editor.TITLE_CHANGE)
            {
                Document doc = getEditor().getDocument();
                String title = (String) doc.getProperty("Document.Title");
                setTitle(title);
            }
        }
    }

    /**
     * Position the frame in a resonable location on the screen.
     */
    private void positionFrame()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
    }

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.EditorWindow");
    }
}

