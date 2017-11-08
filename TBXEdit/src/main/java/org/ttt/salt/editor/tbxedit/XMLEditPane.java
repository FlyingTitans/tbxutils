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
package org.ttt.salt.editor.tbxedit;

import java.io.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * This represents a single pane that will use Editor to get its document
 * information.
 * <p>
 * This is in essence a ScrollPane with the added benifit of pane specific
 * controls.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class XMLEditPane extends JScrollPane implements ActionListener,
    FocusListener, CaretListener, Cloneable
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private JEditorPane editor;

    /**
     * @param document The document that holds the data to back the editor.
     */
    public XMLEditPane(Document document)
    {
        //CHECKSTYLE: MagicNumber OFF
        super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        editor = new JEditorPane();
        editor.setContentType("text/plain");
        editor.setDocument(document);
        editor.addCaretListener(this);
        editor.addFocusListener(org.ttt.salt.editor.find.Dialog.getInstance());
        getViewport().setView(editor);
        getViewport().setCursor(
                Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        Insets margin = editor.getMargin();
        int lrm = margin.left + margin.right;
        int tbm = margin.top + margin.bottom;
        FontMetrics metrics = editor.getFontMetrics(editor.getFont());
        int cw = metrics.charWidth('m');
        int lh = metrics.getHeight();
        getViewport().setMinimumSize(new Dimension(40 * cw + lrm,
                10 * lh + tbm));
        getViewport().setPreferredSize(new Dimension(80 * cw + lrm,
                25 * lh + tbm));
        getViewport().setMaximumSize(new Dimension(132 * cw + lrm,
                100 * lh + tbm));
        //CHECKSTYLE: MagicNumber ON
    }
    
    /**
     * @return The {@link javax.swing.text.JTextComponent} that handles the
     *  view and controller for this edit pane.
     */
    public JTextComponent getTextComponent()
    {
        return editor;
    }

    /** {@inheritDoc} */
    public void actionPerformed(ActionEvent evt)
    {
    }
    
    /** {@inheritDoc} */
    public void focusGained(FocusEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void focusLost(FocusEvent evt)
    {
    }

    /** {@inheritDoc} */
    public void caretUpdate(CaretEvent e)
    {
    }
}

