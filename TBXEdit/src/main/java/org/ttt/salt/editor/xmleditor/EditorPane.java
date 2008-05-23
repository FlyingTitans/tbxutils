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
import org.flyingtitans.swing.text.IntegerFilterDocument;

/**
 * This represents a single pane that will use Editor to get its document information.
 * <p>
 * This is in essence a ScrollPane with the added benifit of pane specific controls.</p>
 * <p>
 * <strong>CAUTION:</strong> If you add any listeners to this they will not be
 * preserved over spliting and joining of editors.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class EditorPane extends JScrollPane implements ActionListener, FocusListener,
    CaretListener, Cloneable
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private static final String SPLIT_VERT = "SplitVert";
    
    /** */
    private static final String SPLIT_HORZ = "SplitHorz";
    
    /** */
    private static final String SPLIT_CLOSE = "SplitClose";
    
    /** resource bundle that is used by all menus */
    private static ResourceBundle bundle;
    
    /** */
    private Editor document;
    
    /** */
    private JEditorPane editor;

    /** Line number label */
    private JTextField line;

    /** Column number label */
    private JTextField column;

    /**
     */
    public EditorPane(Editor document, EditorWindow frame)
    {
        super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        this.document = document;
        editor = new JEditorPane();
        editor.setCaret(new Text_Caret());
        editor.setContentType(document.getMimeType());
        editor.setDocument(document.getDocument());
        editor.addCaretListener(this);
        editor.addFocusListener(org.ttt.salt.editor.find.Dialog.getInstance());
        editor.addFocusListener(frame.getEditorMenu());
        try
        {
            editor.getHighlighter().addHighlight(0, Integer.MAX_VALUE,
                            LewisBar.getInstance());
        }
        catch (BadLocationException err)
        {
            //no op
        }
        setColumnHeaderView(initToolbar(frame));
        getViewport().setView(editor);
        getViewport().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        Insets margin = editor.getMargin();
        int lrm = margin.left + margin.right;
        int tbm = margin.top + margin.bottom;
        FontMetrics metrics = editor.getFontMetrics(editor.getFont());
        int cw = metrics.charWidth('m');
        int lh = metrics.getHeight();
        getViewport().setMinimumSize(new Dimension(40 * cw + lrm, 25 * lh + tbm));
        getViewport().setPreferredSize(new Dimension(80 * cw + lrm, 50 * lh + tbm));
        getViewport().setMaximumSize(new Dimension(132 * cw + lrm, 100 * lh + tbm));
        Keymap keymap = JTextComponent.getKeymap(Text_EditorKit.EDITOR_KEYMAP);
        if (keymap != null)
             editor.setKeymap(keymap);
    }

    /**
     * Goto the given line in the active viewport. This will handle line
     * numbers that don't exist in the document.
     */
    public void setLine(int line)
    {
        try
        {
            Segment segment = new Segment();
            editor.getDocument().getText(0, editor.getDocument().getLength(), segment);
            int dot = segment.offset;
            int stop = segment.offset + segment.count;
            while (line > 1 && dot < stop)
            {
                if (segment.array[dot] == '\n')
                    line--;
                dot++;
            }
            column.setText("1");
            editor.getCaret().setDot(dot);
            updateStatus();
        }
        catch (BadLocationException err)
        {
        }
    }

    /**
     * Goto the given column on the current line in the active viewport.
     * This will handle column numbers that don't exist on the current line.
     */
    public void setColumn(int column)
    {
        try
        {
            Segment segment = new Segment();
            editor.getDocument().getText(0, editor.getDocument().getLength(), segment);
            int dot = editor.getCaret().getDot();
            while (dot > segment.offset && segment.array[dot] != '\n')
            {
                dot--;
            }
            dot++;
            int stop = segment.offset + segment.count;
            while (column > 1 && dot < stop && segment.array[dot] != '\n')
            {
                dot++;
                column--;
            }
            editor.getCaret().setDot(dot);
            updateStatus();
        }
        catch (BadLocationException err)
        {
        }
    }

    /** {@inheritDoc} */
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getActionCommand().equals(SPLIT_HORZ))
        {
            split(JSplitPane.HORIZONTAL_SPLIT);
        }
        else if (evt.getActionCommand().equals(SPLIT_VERT))
        {
            split(JSplitPane.VERTICAL_SPLIT);
        }
        else if (evt.getActionCommand().equals(SPLIT_CLOSE))
        {
            splitClose();
        }
        else if (evt.getSource() == line)
        {
            //evt.getActionCommand()
            setLine(Integer.parseInt(column.getText()));
        }
        else if (evt.getSource() == column)
        {
            //evt.getActionCommand()
            setColumn(Integer.parseInt(column.getText()));
        }
    }
    
    /** {@inheritDoc} */
    public void focusGained(FocusEvent evt)
    {
        if (evt.getComponent() == line)
        {
            line.selectAll();
        }
        else if (evt.getComponent() == column)
        {
            column.selectAll();
        }
    }

    /** {@inheritDoc} */
    public void focusLost(FocusEvent evt)
    {
        if (evt.getComponent() == line)
        {
            String str = line.getText();
            if (str.length() > 0)
                setLine(Integer.parseInt(str));
            else
                updateStatus();
        }
        else if (evt.getComponent() == column)
        {
            String str = column.getText();
            if (str.length() > 0)
                setColumn(Integer.parseInt(str));
            else
                updateStatus();
        }
    }

    /** {@inheritDoc} */
    public void caretUpdate(CaretEvent e)
    {
        updateStatus();
    }
    
    /**
     */
    private void split(int orientation)
    {
        JComponent parent = (JComponent) getParent();
        EditorWindow frame = getEditorWindow();
        EditorPane e = new EditorPane(document, frame);
        e.editor.setCaretPosition(editor.getCaretPosition());
        if (parent instanceof JSplitPane)
        {
            JSplitPane p = (JSplitPane) parent;
            boolean left = (p.getLeftComponent() == this);
            p.remove(this);
            JSplitPane split = new JSplitPane(orientation, this, e);
            if (left)
                p.setLeftComponent(split);
            else
                p.setRightComponent(split);
        }
        else
        {
            parent.remove(this);
            JSplitPane split = new JSplitPane(orientation, this, e);
            parent.add(split, BorderLayout.CENTER);
        }
        parent.validate();
    }
    
    /**
     */
    private void splitClose()
    {
        JComponent parent = (JComponent) getParent();
        if (parent instanceof JSplitPane)
        {
            JSplitPane split = (JSplitPane) parent;
            parent = (JComponent) split.getParent();
            EditorPane other = (split.getLeftComponent() == this)
                    ? (EditorPane) split.getRightComponent()
                    : (EditorPane) split.getLeftComponent();
            if (parent instanceof JSplitPane)
            {
                JSplitPane p = (JSplitPane) parent;
                boolean left = (p.getLeftComponent() == split);
                p.remove(split);
                split.removeAll();
                if (left)
                    p.setLeftComponent(other);
                else
                    p.setRightComponent(other);
            }
            else
            {
                parent.remove(split);
                split.removeAll();
                parent.add(other);
            }
            parent.validate();
        }
    }
    
    /**
     */
    private EditorWindow getEditorWindow()
    {
        Object frame = getParent();
        while (!(frame instanceof EditorWindow))
        {
            frame = (Object) ((JComponent) frame).getParent();
        }
        return (EditorWindow) frame;
    }

    /**
     * Update the status bar. This will include the line number and the
     * column number that the caret is located on.
     */
    private void updateStatus()
    {
        Text_Caret caret = (Text_Caret) editor.getCaret();
        line.setText(Integer.toString(caret.getLineNumber()));
        column.setText(Integer.toString(caret.getColumnNumber()));
    }
    
    /**
     * Build the status pane with all the items needed within it.
     */
    private JToolBar initToolbar(EditorWindow frame)
    {
        JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
        //bar.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
        
        JButton splitVert = new JButton((Icon) bundle.getObject("SplitVertIcon"));
        splitVert.setActionCommand(SPLIT_VERT);
        splitVert.setToolTipText(bundle.getString("SplitVertToolTip"));
        splitVert.addActionListener(this);
        bar.add(splitVert);
        
        JButton splitHorz = new JButton((Icon) bundle.getObject("SplitHorzIcon"));
        splitHorz.setActionCommand(SPLIT_HORZ);
        splitHorz.setToolTipText(bundle.getString("SplitHorzToolTip"));
        splitHorz.addActionListener(this);
        bar.add(splitHorz);
        
        JButton splitClose = new JButton((Icon) bundle.getObject("SplitCloseIcon"));
        splitClose.setActionCommand(SPLIT_CLOSE);
        splitClose.setToolTipText(bundle.getString("SplitCloseToolTip"));
        splitClose.addActionListener(this);
        bar.add(splitClose);

        line = new JTextField(
            new IntegerFilterDocument(frame.getRootPane()), "1", 4);
        line.setToolTipText(bundle.getString("LineToolTip"));
        bar.add(createStatusBox(bundle.getString("LineLabel"), line));
        
        column = new JTextField(
            new IntegerFilterDocument(frame.getRootPane()), "1", 4);
        column.setToolTipText(bundle.getString("ColumnToolTip"));
        bar.add(createStatusBox(bundle.getString("ColumnLabel"), column));

        return bar;
    }
    
    /**
     */
    private JComponent createStatusBox(String title, JTextField field)
    {
        JComponent panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = new JLabel(title);
        panel.add(label);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.addActionListener(this);
        field.addFocusListener(this);
        panel.add(field);
        return panel;
    }
    
    /**
     */
    private static Icon createIcon(String name)
    {
        return null;
    }

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle(
                "org.flyingtitans.ide.EditorPaneBundle");
    }
}

