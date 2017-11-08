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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.*;


/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Caret_Text_Plain extends DefaultCaret
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private int clicks;

    /** */
    private int startMark;

    /** */
    private int startDot;

    /** true => caret is in insert mode, false => caret is overwrite */
    private boolean mode = true;

    /**
     */
    public Caret_Text_Plain()
    {
        super();
        setBlinkRate(UIManager.getInt("EditorPane.caretBlinkRate"));
    }

    /**
     */
    public void setInsertMode(boolean flag)
    {
        if (flag != mode)
        {
            setVisible(false);
            mode = flag;
            fireStateChanged();
            setVisible(true);
        }
    }

    /**
     */
    public boolean getInsertMode()
    {
        return mode;
    }

    /**
     */
    public void mousePressed(MouseEvent e)
    {
    if (SwingUtilities.isLeftMouseButton(e))
    {
        clicks = e.getClickCount() % 3;
        if (clicks == 1)
        {
            positionCaret(e);
        }
        else if (clicks == 2)
        {
            Action a = getAction(DefaultEditorKit.selectWordAction);
            a.actionPerformed(null);
            startMark = getMark();
            startDot = getDot();
        }
        else if (clicks == 0)
        {
            Action a = getAction(DefaultEditorKit.selectLineAction);
            a.actionPerformed(null);
            startMark = getMark();
            startDot = getDot();
        }
        if ((getComponent() != null) && getComponent().isEnabled())
        {
        getComponent().requestFocus();
        }
    }
    }

    /**
     */
    public void mouseReleased(MouseEvent e)
    {
        //DO NOT USE DEFAULT
    }

    /**
     */
    public void mouseClicked(MouseEvent e)
    {
        //DO NOT USE DEFAULT
    }

    /**
     */
    //public void mouseEntered(MouseEvent e)
    //{
    //}

    /**
     */
    //public void mouseExited(MouseEvent e)
    //{
    //}

    /**
     */
    public void mouseDragged(MouseEvent e)
    {
    if (SwingUtilities.isLeftMouseButton(e))
    {
        if (clicks == 1)
        {
            moveCaret(e);
        }
        else if (clicks == 2)
        {
                positionCaret(e);
            Action a = getAction(DefaultEditorKit.selectWordAction);
            a.actionPerformed(null);
            int mark = getMark();
            int dot = getDot();
            if (mark != startMark)
            {
                setDot(startMark);
                moveDot(mark);
            }
        }
        else if (clicks == 0)
        {
                positionCaret(e);
            Action a = getAction(DefaultEditorKit.selectLineAction);
            a.actionPerformed(null);
            int mark = getMark();
            int dot = getDot();
            if (mark < startMark)
            {
                setDot(mark);
                moveDot(startDot);
            }
            else if (mark > startMark)
            {
                setDot(startMark);
                moveDot(dot);
            }
        }
    }
    }

    /**
     */
    public void paint(Graphics g)
    {
    if(isVisible())
    {
        try
        {
        TextUI mapper = getComponent().getUI();
        Rectangle r = mapper.modelToView(getComponent(), getDot(),
                Position.Bias.Forward);
        if (mode)
        {
            g.setColor(getComponent().getCaretColor());
            g.drawLine(r.x, r.y, r.x, r.y + r.height - 1);
        }
        else
        {
            JTextComponent edit = (JTextComponent) getComponent();
            String c = edit.getDocument().getText(getDot(), 1);
            Font font = edit.getFont();
                    FontMetrics metrics = edit.getFontMetrics(font);
                    int cw = metrics.charWidth(c.charAt(0)) - 1;
            g.setColor(getComponent().getCaretColor());
            g.drawRect(r.x, r.y, cw, r.height - 1);
            g.fillRect(r.x, r.y, cw, r.height - 1);
            g.setFont(getComponent().getFont());
            g.setColor(getComponent().getBackground());
            g.drawString(c, r.x, r.y + metrics.getAscent());
        }
        }
        catch (BadLocationException e)
        {
        System.err.println("FAULT: render problem " +
                getClass().getName() + "#paint");
        }
    }
    }

    /**
     */
    protected void damage(Rectangle r)
    {
        if (r != null)
    {
            x = r.x - 4;
            y = r.y;
        width = 10;
        height = r.height;
        if (!mode)
        {
            try
            {
                    JTextComponent edit = (JTextComponent) getComponent();
                    Document doc = edit.getDocument();
                    String c = doc.getText(getDot(), 1);
                    Font font = edit.getFont();
                    FontMetrics metrics = edit.getFontMetrics(font);
                    int cw = metrics.charWidth(c.charAt(0));
                    width = width + cw;
            }
            catch (BadLocationException e)
            {
            System.err.println("FAULT: render problem " +
                    getClass().getName() + "#damage");
            }
            }
        repaint();
        }
    }

    /**
     */
    private Action getAction(String name)
    {
        JEditorPane editor = (JEditorPane) getComponent();
        EditorKit kit = editor.getEditorKit();
        Action[] actions = kit.getActions();
        Action ret = null;
        for (int i = 0; (i < actions.length) && (ret == null); i++)
        {
            if (actions[i].getValue(Action.NAME) == name)
                ret = actions[i];
        }
        if (ret == null)
        {
            System.err.println("EditorKit does not contain necessary action "
                + name);
        }
        return ret;
    }
}

