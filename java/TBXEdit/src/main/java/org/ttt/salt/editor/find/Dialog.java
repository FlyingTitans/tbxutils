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
package org.ttt.salt.editor.find;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
public class Dialog extends JDialog implements ActionListener, FocusListener
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** The single instance of this dialog */
    private static Dialog instance = new Dialog();

    /** Main frame for the find dialog. */
    private final JFrame frame = new JFrame("Find");

    /** */
    private WindowListener winListen;

    /** The editing pane the search shall occur in. */
    private JEditorPane activeEditing;

    /** */
    private final FindPanel findPanel = new FindPanel(this);

    /** */
    private final FilesPanel filesPanel = new FilesPanel(this);

    /** */
    private final PatternsPanel patternsPanel = new PatternsPanel();

    /** */
    private final JButton findBtn = createJButton("FindButton");

    /** */
    private final JButton replaceBtn = createJButton("ReplaceButton");

    /** */
    private final JButton replaceFindBtn = createJButton("ReplaceFindButton");

    /** */
    private final JButton replaceAllBtn = createJButton("ReplaceAllButton");

    /** */
    private final JButton helpBtn = createJButton("HelpButton");

    /** */
    private boolean found = false;

    /** Offset to the find dot -- valid only if found is true  */
    private int findStart;

    /** Offset to the find mark -- valid only if found is true */
    private int findEnd;

    /**
     */
    public static Dialog getInstance()
    {
        return instance;
    }

    /**
     */
    private Dialog()
    {
        this.getContentPane().setLayout(new BorderLayout());
        this.setTitle(getBundle().getString("DialogTitle"));
        this.setResizable(false);

        JTabbedPane tabpane = new JTabbedPane();
        this.getContentPane().add(tabpane, BorderLayout.CENTER);
        tabpane.add(findPanel, getBundle().getString("FindTab"));
        tabpane.add(filesPanel, getBundle().getString("FilesTab"));
        tabpane.add(patternsPanel, getBundle().getString("PatternsTab"));

        //setup the command buttons panel
        findBtn.setDefaultCapable(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(findBtn, null);
        buttonPanel.add(replaceBtn, null);
        buttonPanel.add(replaceFindBtn, null);
        buttonPanel.add(replaceAllBtn, null);
        buttonPanel.add(helpBtn, null);
        findBtn.setDefaultCapable(true);
        updateButtonStatus();
        this.getRootPane().setDefaultButton(findBtn);
        this.pack();
        //position the dialog
    }

    /**
     * This will place the given string into the search string.
     */
    public void setFindString(String str)
    {
        if (str == null)
            str = "";
        findPanel.setSearchString(str);
    }

    /**
     */
    public String getFindString()
    {
        return findPanel.getSearchString();
    }

    /**
     * This will place the given string into the replace string.
     */
    public void setReplaceString(String str)
    {
        if (str == null)
            str = "";
        findPanel.setReplaceString(str);
    }

    /**
     */
    public String getReplaceString()
    {
        return findPanel.getReplaceString();
    }

    /**
     * This will find the next string in the active view that matches
     * the search string.
     */
    public void findNext()
    {
        if (activeEditing != null)
        {
            try
            {
                String str = getFindString();
                Document doc = activeEditing.getDocument();
                Caret caret = activeEditing.getCaret();
                Segment seg = new Segment();
                int offset = 0;
                int length = 0;
                switch (findPanel.getLimits())
                {
                    case FindPanel.LIMIT_STOP_AT_BOTTOM:
                    case FindPanel.LIMIT_WRAP_AROUND:
                        if (caret.getMark() < caret.getDot())
                            caret.setDot(caret.getDot());
                        else if (caret.getMark() > caret.getDot())
                            caret.setDot(caret.getMark());
                        offset = caret.getMark();
                        length = doc.getLength() - caret.getMark();
                        break;
                    case FindPanel.LIMIT_SEARCH_SELECTION:
                        if (caret.getMark() < caret.getDot())
                        {
                            offset = caret.getMark();
                            length = caret.getDot() - caret.getMark();
                        }
                        else
                        {
                            offset = caret.getDot();
                            length = caret.getMark() - caret.getDot();
                        }
                        break;
                }
                doc.getText(offset, length, seg);
                boolean found = find(seg, str, true, offset - seg.offset);
                if (findPanel.getLimits() == FindPanel.LIMIT_WRAP_AROUND
                    && !found)
                {
                    offset = 0;
                    doc.getText(0, doc.getLength(), seg);
                    found = find(seg, str, true, offset - seg.offset);
                }
                if (found)
                {
                    caret.setDot(findStart);
                    caret.moveDot(findEnd);
                }
                else
                {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            catch (BadLocationException err)
            {
                System.err.println("PROGRAMMER FAULT");
                err.printStackTrace(System.err);
            }
        }
    }

    /**
     * This will find the previous string in the active view that matches
     * the search string.
     */
    public void findPrev()
    {
        if (activeEditing != null)
        {
            try
            {
                String str = getFindString();
                Document doc = activeEditing.getDocument();
                Caret caret = activeEditing.getCaret();
                Segment seg = new Segment();
                int offset = 0;
                int length = 0;
                switch (findPanel.getLimits())
                {
                    case FindPanel.LIMIT_STOP_AT_BOTTOM:
                    case FindPanel.LIMIT_WRAP_AROUND:
                        if (caret.getMark() < caret.getDot())
                            caret.setDot(caret.getMark());
                        else if (caret.getMark() > caret.getDot())
                            caret.setDot(caret.getDot());
                        offset = 0;
                        length = caret.getMark();
                        break;
                    case FindPanel.LIMIT_SEARCH_SELECTION:
                        if (caret.getMark() < caret.getDot())
                        {
                            offset = caret.getMark();
                            length = caret.getDot() - caret.getMark();
                        }
                        else
                        {
                            offset = caret.getDot();
                            length = caret.getMark() - caret.getDot();
                        }
                        break;
                }
                doc.getText(offset, length, seg);
                boolean found = find(seg, str, false, offset - seg.offset);
                if (findPanel.getLimits() == FindPanel.LIMIT_WRAP_AROUND
                    && !found)
                {
                    doc.getText(caret.getMark(),
                            doc.getLength() - caret.getMark(), seg);
                    found = find(seg, str, false, offset - seg.offset);
                }
                if (found)
                {
                    caret.setDot(findStart);
                    caret.moveDot(findEnd);
                }
                else
                {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            catch (BadLocationException err)
            {
                System.err.println("PROGRAMMER FAULT");
                err.printStackTrace(System.err);
            }
        }
    }

    /**
     * This will replace the given selection with the string in the replace
     * string then it will search in the active view for the next string
     * that matches the search string.
     */
    public void replaceFindNext()
    {
        if (activeEditing != null)
        {
            replace();
            findNext();
        }
    }

    /**
     * This will replace the given selection with the string in the replace
     * string then it will search in the active view for the previous string
     * that matches the search string.
     */
    public void replaceFindPrev()
    {
        if (activeEditing != null)
        {
            replace();
            findPrev();
        }
    }

    /**
     * This will replace the current selection with the string in the
     * replace string.
     */
    public void replace()
    {
        if (activeEditing != null)
        {
            Document doc = activeEditing.getDocument();
            Caret caret = activeEditing.getCaret();
            if (caret.getMark() != caret.getDot())
            {
                try
                {
                    String str = getReplaceString();
                    int mark = caret.getMark();
                    int dot = caret.getDot();
                    int off = (mark < dot) ? mark : dot;
                    int len = (mark < dot) ? dot - mark : mark - dot;
                    doc.remove(off, len);
                    doc.insertString(off, str, null);
                    caret.setDot(off);
                    caret.moveDot(off + str.length());
                }
                catch (BadLocationException err)
                {
                    System.err.println("PROGRAMMER FAULT");
                    err.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * This will replace all strings matching the search string in the
     * active view with the replace string.
     */
    public void replaceAll()
    {
        if (activeEditing != null)
        {
            try
            {
                String sstr = getFindString();
                String rstr = getReplaceString();
                Document doc = activeEditing.getDocument();
                Caret caret = activeEditing.getCaret();
                Segment seg = new Segment();
                doc.getText(0, doc.getLength(), seg);
                boolean found = find(seg, sstr, true, 0);
                while (found)
                {
                    doc.remove(findStart, findEnd - findStart);
                    doc.insertString(findStart, rstr, null);
                    doc.getText(0, doc.getLength(), seg);
                    found = find(seg, sstr, true, 0);
                }
                Toolkit.getDefaultToolkit().beep();
            }
            catch (BadLocationException err)
            {
                System.err.println("PROGRAMMER FAULT");
                err.printStackTrace(System.err);
            }
        }
    }

    /**
     */
    public void actionPerformed(ActionEvent evt)
    {
        try
        {
            Class[] parameters = {ActionEvent.class};
            Object[] arguments = {evt};
            String name = "handleAction__" + evt.getActionCommand();
            Method meth = getClass().getDeclaredMethod(name, parameters);
            meth.invoke(this, arguments);
        }
        catch (NoSuchMethodException err)
        {
            System.err.println("No method for action "
                + evt.getActionCommand());
        }
        catch (InvocationTargetException err)
        {
            //the action handler method threw an exception
        }
        catch (IllegalAccessException err)
        {
            //This should not happen
        }
        updateButtonStatus();
    }

    /**
     */
    public void focusGained(FocusEvent evt)
    {
        if (evt.getComponent() instanceof JEditorPane)
        {
            activeEditing = (JEditorPane) evt.getComponent();
        }
    }

    /**
     */
    public void focusLost(FocusEvent evt)
    {
    }

    /**
     */
    static java.util.ResourceBundle getBundle()
    {
        return java.util.ResourceBundle.getBundle(
            "org.ttt.salt.editor.find.Dialog");
    }

    /**
     */
    void updateButtonStatus()
    {
        if (findPanel.getSearchString().length() == 0)
        {
            findBtn.setEnabled(false);
            replaceBtn.setEnabled(false);
            replaceFindBtn.setEnabled(false);
            replaceAllBtn.setEnabled(false);
        }
        else
        {
            findBtn.setEnabled(true);
            replaceBtn.setEnabled(true);
            replaceFindBtn.setEnabled(true);
            replaceAllBtn.setEnabled(true);
        }
    }

    /**
     */
    private JButton createJButton(String key)
    {
        JButton ret = new JButton(getBundle().getString(key));
        ret.setActionCommand(key);
        ret.addActionListener(this);
        return ret;
    }

    /**
     */
    private void handleAction__FindButton(ActionEvent evt)
    {
        findNext();
    }

    /**
     */
    private void handleAction__ReplaceButton(ActionEvent evt)
    {
        replace();
    }

    /**
     */
    private void handleAction__ReplaceFindButton(ActionEvent evt)
    {
        replaceFindNext();
    }

    /**
     */
    private void handleAction__ReplaceAllButton(ActionEvent evt)
    {
        replaceAll();
    }

    /**
     */
    private void handleAction__HelpButton(ActionEvent evt)
    {
        System.out.println("Find Help");
    }

    /**
     * @param seg The segment that holds the text.
     * @param str The string to search for.
     * @param first true => find the first match in the segment
     * @param correction The number of characters before the segment to
     *  correct the findStart and findEnd.
     */
    private boolean find(Segment seg, String str, boolean first,
            int correction)
    {
        boolean ret;
        boolean word = findPanel.isWholeWord();
        if (findPanel.isRegularExpression())
        {
            if (findPanel.isIgnoreCase())
            {
                ret = findRegularExpressionIgnoreCase(seg, str, first);
            }
            else
            {
                ret = findRegularExpression(seg, str, first);
            }
        }
        else
        {
            if (findPanel.isIgnoreCase())
            {
                ret = findStringIgnoreCase(seg, str, word, first);
            }
            else
            {
                ret = findString(seg, str, word, first);
            }
        }
        findStart += correction;
        findEnd += correction;
        return ret;
    }

    /**
     */
    private boolean isWord(Segment seg, int offset, int len)
    {
        boolean ret = false;
        if (offset > 0)
        {
            ret = !Character.isLetterOrDigit(seg.array[offset - 1])
                 && !Character.isLetterOrDigit(seg.array[offset + len]);
        }
        return ret;
    }

    /**
     */
    private boolean findString(Segment seg, String str, boolean wholeword,
            boolean first)
    {
        /*
         * This is using a brute force search algorithm which may
         * be changed during the optimization phase.
         */
        char[] c = new char[str.length()];
        str.getChars(0, str.length(), c, 0);
        findStart = -1;
        for (int i = seg.offset; i < seg.offset + seg.count
            && findStart == -1; i++)
        {
            int si = i;
            int ci = 0;
            while ((ci < c.length) && (si < seg.offset + seg.count)
                && (seg.array[si] == c[ci]))
            {
                si++;
                ci++;
            }
            if ((ci == c.length)
                && (!wholeword || isWord(seg, i, str.length())))
            {
                findStart = i;
            }
        }
        findEnd = findStart + str.length();
        return (findStart >= 0);
    }

    /**
     */
    private boolean findStringIgnoreCase(Segment seg, String str,
           boolean wholeword, boolean first)
    {
        /*
         * This is using a brute force search algorithm which may
         * be changed during the optimization phase.
         */
        char[] c = new char[str.length()];
        str.getChars(0, str.length(), c, 0);
        findStart = -1;
        for (int i = seg.offset; i < seg.offset + seg.count
            && findStart == -1; i++)
        {
            int si = i;
            int ci = 0;
            while ((ci < c.length) && (si < seg.offset + seg.count)
                && (Character.toLowerCase(seg.array[si])
                    == Character.toLowerCase(c[ci])))
            {
                si++;
                ci++;
            }
            if ((ci == c.length)
                && (!wholeword || isWord(seg, i, str.length())))
            {
                findStart = i;
            }
        }
        findEnd = findStart + str.length();
        return (findStart >= 0);
    }

    /**
     */
    private boolean findRegularExpression(Segment seg, String str,
        boolean first)
    {
        return false;
    }

    /**
     */
    private boolean findRegularExpressionIgnoreCase(Segment seg, String str,
        boolean first)
    {
        return false;
    }
}

