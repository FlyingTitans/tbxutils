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
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * This provides the base for all mime types that match text/xxxx, where
 * the a sub-class of this kit will handle the sub-types.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public abstract class Text_EditorKit extends EditorKit
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    public static final String EDITOR_KEYMAP =
            "org.ttt.salt.editor.Editor.keyMap";
    
    /** Default key typed event action--no keymap entry */
    public static final TextAction defaultKeyTypedAction =
        new TextAction("defaultKeyTypeing")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if ((target != null) && (e != null)
                    && target.isEditable()
                    && target.isEnabled())
                {
                    Text_Caret caret = (Text_Caret) target.getCaret();
                    if (!caret.getInsertMode()
                        && (caret.getDot() == caret.getMark()))
                    {
                        caret.moveDot(caret.getDot() + 1);
                    }
                    String content = e.getActionCommand();
                    int mod = e.getModifiers();
                    int alt = mod & ActionEvent.ALT_MASK;
                    int ctl = mod & ActionEvent.CTRL_MASK;
                    if ((content != null) && (content != "") && (alt == ctl))
                    {
                        char c = content.charAt(0);
                        if ((0x20 <= c) && (c != 0x7F))
                        {
                            target.replaceSelection(content);
                        }
                    }
                }
            }
        };
    
    /** */
    public static final TextAction dumpModelAction =
        new TextAction("dump-model")
        {
            //TODO
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    Document d = target.getDocument();
                    if (d instanceof AbstractDocument)
                    {
                        ((AbstractDocument) d).dump(System.err);
                    }
                }
            }
        };

    /** */
    public static final TextAction actionBeep =
        new TextAction("beep")
        {
            public void actionPerformed(ActionEvent e)
            {
                Toolkit.getDefaultToolkit().beep();
            }
        };

    /** Action to change editing mode from insert to overwrite. */
    public static final TextAction actionInsToggle =
        new TextAction("insert-overwrite-toggle")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent editor = getTextComponent(e);
                if (editor != null)
                {
                    Text_Caret caret = (Text_Caret) editor.getCaret();
                    caret.setInsertMode(!caret.getInsertMode());
                }
            }
        };

    /** */
    public static final TextAction actionInsertBreak =
        new TextAction("insert-break")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    if (target.isEditable() && target.isEnabled())
                        target.replaceSelection("\n");
                    else
                        target.getToolkit().beep();
                }
            }
        };

    /** */
    public static final TextAction actionInsertContent =
        new TextAction("insert-content")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if ((target != null) && (e != null))
                {
                    String content = e.getActionCommand();
                    if ((content != null) && target.isEditable()
                        && target.isEnabled())
                    {
                        target.replaceSelection(content);
                    }
                    else
                    {
                        target.getToolkit().beep();
                    }
                }
            }
        };

    /** */
    public static final TextAction actionInsertTab =
        new TextAction("insert-tab")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    if (target.isEditable() && target.isEnabled())
                        target.replaceSelection("\t");
                    else
                        target.getToolkit().beep();
                }
            }
        };

    /** Take selection of current line and execute it through the shell */
    public static final TextAction actionExcuteSelection =
        new TextAction("executeSelection")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** */
    public static final TextAction actionSetReadOnly =
        new TextAction("set-read-only")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    target.setEditable(false);
                }
            }
        };

    /** */
    public static final TextAction actionSetWritable =
        new TextAction("set-writable")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    target.setEditable(true);
                }
            }
        };

    /** Move caret up one line */
    public static final TextAction actionCaretUp =
        new TextAction("caretUp")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to start of block or to start of previous block */
    public static final TextAction actionCaretUpBlock =
        new TextAction("cartUpBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to start of document */
    public static final TextAction actionCaretHome =
        new TextAction("caretHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret down one line*/
    public static final TextAction actionCaretDown =
        new TextAction("caretDown")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to end of block or to end of next block */
    public static final TextAction actionCaretDownBlock =
        new TextAction("caretDownBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to end of document */
    public static final TextAction actionCaretEnd =
        new TextAction("caretEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };
    
    /** Move caret left one character */
    public static final TextAction actionCaretLeft =
        new TextAction("caretLeft")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };
    
    /** Move caret left to start of word or to start of previous word */
    public static final TextAction actionCaretLeftWord =
        new TextAction("caretLeft")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to start of line */
    public static final TextAction actionCaretLineHome =
        new TextAction("caretLineHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret right one character */
    public static final TextAction actionCaretRight =
        new TextAction("caretRight")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret right to end of word or to end of next word */
    public static final TextAction actionCaretRightWord =
        new TextAction("caretRightWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Move caret to end of line */
    public static final TextAction actionCaretLineEnd =
        new TextAction("caretLineEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window up one line */
    public static final TextAction actionScrollUpLine =
        new TextAction("scrollUpLine")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to start of block or to start of previous block */
    public static final TextAction actionScrollUpBlock =
        new TextAction("scrollUpBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window up one page */
    public static final TextAction actionScrollUpPage =
        new TextAction("scrollUpPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageUpPrevLocation(target);
                    if (loc >= 0)
                        target.setCaretPosition(loc);
                }
            }
        };

    /** Scroll window to top of document */
    public static final TextAction actionScrollUpHome =
        new TextAction("scrollUpHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window down one line */
    public static final TextAction actionScrollDownLine =
        new TextAction("scrollDownLine")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to end of block or to end of next block */
    public static final TextAction actionScrollDownBlock =
        new TextAction("scrollDownBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window down one page */
    public static final TextAction actionScrollDownPage =
        new TextAction("scrollDownPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageDownNextLocation(target);
                    if (loc >= 0)
                        target.setCaretPosition(loc);
                }
            }
        };

    /** Scroll window to end of document */
    public static final TextAction actionScrollDownEnd =
        new TextAction("scrollDownEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window left one character */
    public static final TextAction actionScrollLeft =
        new TextAction("scrollLeft")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window left to start of word or to start of previous word */
    public static final TextAction actionScrollLeftWord =
        new TextAction("scrollLeftWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to start of line */
    public static final TextAction actionScrollLeftHome =
        new TextAction("scrollLeftHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to left one page */
    public static final TextAction actionScrollLeftPage =
        new TextAction("scrollLeftPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageHorzLocation(target, true);
                    if (loc >= 0)
                        target.setCaretPosition(loc);
                }
            }
        };

    /** Scroll window right one character */
    public static final TextAction actionScrollRight =
        new TextAction("scrollRight")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window right to end of word or to end of next word */
    public static final TextAction actionScrollRightWord =
        new TextAction("scrollRightWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to end of line */
    public static final TextAction actionScrollRightEnd =
        new TextAction("scrollRightEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Scroll window to right one page */
    public static final TextAction actionScrollRightPage =
        new TextAction("scrollRightPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageHorzLocation(target, false);
                    if (loc >= 0)
                        target.setCaretPosition(loc);
                }
            }
        };

    /** Select current word */
    public static final TextAction actionSelectWord =
        new TextAction("selectWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select current line */
    public static final TextAction actionSelectLine =
        new TextAction("selectLine")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select current block */
    public static final TextAction actionSelectBlock =
        new TextAction("selectBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select all text in the document */
    public static final TextAction actionSelectAll =
        new TextAction("selectAll")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select left one character */
    public static final TextAction actionSelectLeft =
        new TextAction("selectLeft")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select left to start of word or select to start of prev word */
    public static final TextAction actionSelectLeftWord =
        new TextAction("selectLeftWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select left to start of line */
    public static final TextAction actionSelectLeftHome =
        new TextAction("selectLeftHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select right one character */
    public static final TextAction actionSelectRight =
        new TextAction("selectRight")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select right to end of word or select to end of next word*/
    public static final TextAction actionSelectRightWord =
        new TextAction("selectRightWord")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select right to end of line */
    public static final TextAction actionSelectRightEnd =
        new TextAction("selectRightEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select up to same column on prev line*/
    public static final TextAction actionSelectUp =
        new TextAction("selectUp")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select to start of current line or select to start of prev line */
    public static final TextAction actionSelectUpLine =
        new TextAction("selectUpLine")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select up to start of block */
    public static final TextAction actionSelectUpBlock =
        new TextAction("selectUpBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select up to start of page or select to start of prev page */
    public static final TextAction actionSelectUpPage =
        new TextAction("selectUpPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageUpPrevLocation(target);
                    if (loc >= 0)
                        target.moveCaretPosition(loc);
                }
            }
        };

    /** Select up to start of document */
    public static final TextAction actionSelectUpHome =
        new TextAction("selectUpHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select down to same column on next line */
    public static final TextAction actionSelectDown =
        new TextAction("selectDown")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select to end of current line or select to end of next line */
    public static final TextAction actionSelectDownLine =
        new TextAction("selectDownLine")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select down to end of block */
    public static final TextAction actionSelectDownBlock =
        new TextAction("selectDownBlock")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Select up to end of page or select to end of next page */
    public static final TextAction actionSelectDownPage =
        new TextAction("selectDownPage")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    int loc = calcPageDownNextLocation(target);
                    if (loc >= 0)
                        target.moveCaretPosition(loc);
                }
            }
        };

    /** Select down to end of document */
    public static final TextAction actionSelectDownEnd =
        new TextAction("selectDownEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Copy selection to the clipboard */
    public static final TextAction actionCopy =
        new TextAction("copy")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    target.copy();
                }
            }
        };

    /** Cut selection to the clipboard */
    public static final TextAction actionCut =
        new TextAction("cut")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    target.cut();
                }
            }
        };

    /** Paste clipboard to the selection */
    public static final TextAction actionPaste =
        new TextAction("paste")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if (target != null)
                {
                    target.paste();
                }
            }
        };

    /** Delete the character after the cursor */
    public static final TextAction actionDeleteCharNext =
        new TextAction("deleteCharNext")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if ((target != null) && target.isEditable())
                {
                    try
                    {
                        Document doc = target.getDocument();
                        Caret caret  = target.getCaret();
                        int dot      = caret.getDot();
                        int mark     = caret.getMark();
                        if (dot != mark)
                        {
                            int start = Math.min(dot, mark);
                            int end = Math.abs(dot - mark);
                            doc.remove(start, end);
                        }
                        else if (dot < doc.getLength())
                        {
                            doc.remove(dot, 1);
                        }
                        else
                        {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                    catch (BadLocationException bl)
                    {
                    }
                }
            }
        };

    /** Delete the character before the cursor */
    public static final TextAction actionDeleteCharPrev =
        new TextAction("deleteCharPrev")
        {
            public void actionPerformed(ActionEvent e)
            {
                JTextComponent target = getTextComponent(e);
                if ((target != null) && target.isEditable())
                {
                    try
                    {
                        Document doc = target.getDocument();
                        Caret caret  = target.getCaret();
                        int dot      = caret.getDot();
                        int mark     = caret.getMark();
                        if (dot != mark)
                        {
                            int start = Math.min(dot, mark);
                            int end = Math.abs(dot - mark);
                            doc.remove(start, end);
                        }
                        else if (dot > 0)
                        {
                            doc.remove(dot - 1, 1);
                        }
                        else
                        {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                    catch (BadLocationException bl)
                    {
                    }
                }
            }
        };

    /** Delete current word and move to next word */
    public static final TextAction actionDeleteWordNext =
        new TextAction("deleteWordNext")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete current word and move to previous word */
    public static final TextAction actionDeleteWordPrev =
        new TextAction("deleteWordPrev")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete current line and move down one line */
    public static final TextAction actionDeleteLineNext =
        new TextAction("deleteLineNext")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete current line and move up one line */
    public static final TextAction actionDeleteLinePrev =
        new TextAction("deleteLinePrev")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete current block and move to next block */
    public static final TextAction actionDeleteBlockNext =
        new TextAction("deleteBlockNext")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete current block and move to prev block */
    public static final TextAction actionDeleteBlockPrev =
        new TextAction("deleteBlockPrev")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to start of word */
    public static final TextAction actionDeleteToWordHome =
        new TextAction("deleteToWordHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to end of word */
    public static final TextAction actionDeleteToWordEnd =
        new TextAction("deleteToWordEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to start of block */
    public static final TextAction actionDeleteToBlockHome =
        new TextAction("deleteToBlockHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to end of block */
    public static final TextAction actionDeleteToBlockEnd =
        new TextAction("deleteToBlockEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to start of line */
    public static final TextAction actionDeleteToLineHome =
        new TextAction("deleteToLineHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to end of line */
    public static final TextAction actionDeleteToLineEnd =
        new TextAction("deleteToLineEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to start of document */
    public static final TextAction actionDeleteToHome =
        new TextAction("deleteToHome")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };

    /** Delete from current position to end of document */
    public static final TextAction actionDeleteToEnd =
        new TextAction("deleteToEnd")
        {
            public void actionPerformed(ActionEvent e)
            {
                //TODO
            }
        };
        
    /** Property to correctly deterimine EOL on write. */
    public static final String EOLStringProperty = "Text_EditorKit.EOL";
      
    /** */
    private static final TextAction caretRightVisual
        = new NextVisualPositionAction(
                (String) actionCaretRight.getValue(Action.NAME), false,
                SwingConstants.EAST);
    
    private static final TextAction caretLeftVisual
        = new NextVisualPositionAction(
                (String) actionCaretLeft.getValue(Action.NAME), false,
                SwingConstants.WEST);
    
    private static final TextAction selectRightVisual
        = new NextVisualPositionAction(
                (String) actionSelectRight.getValue(Action.NAME), true,
                SwingConstants.EAST);
    
    private static final TextAction selectLeftVisual
        = new NextVisualPositionAction(
                (String) actionSelectLeft.getValue(Action.NAME), true,
                SwingConstants.WEST);
    
    private static final TextAction caretUpVisual
        = new NextVisualPositionAction(
                (String) actionCaretUp.getValue(Action.NAME), false,
                SwingConstants.NORTH);
    
    private static final TextAction caretDownVisual
        =  new NextVisualPositionAction(
                (String) actionCaretDown.getValue(Action.NAME), false,
                SwingConstants.SOUTH);
    
    private static final TextAction selectUpVisual
        = new NextVisualPositionAction(
                (String) actionSelectUp.getValue(Action.NAME), true,
                SwingConstants.NORTH);
    
    private static final TextAction selectDownVisual
        = new NextVisualPositionAction(
                (String) actionSelectDown.getValue(Action.NAME), true,
                SwingConstants.SOUTH);
    
    /** Default actions for this kit */
    private static final Action[] defaultActions =
    {
        defaultKeyTypedAction,    dumpModelAction,    actionBeep,
        actionInsToggle,    actionInsertBreak,    actionInsertContent,
        actionInsertTab,    actionExcuteSelection,    actionSetReadOnly,
        actionSetWritable,
        
        actionCopy,        actionCut,        actionPaste,

        caretRightVisual,    caretLeftVisual,
        selectRightVisual,    selectLeftVisual,
        caretUpVisual,        caretDownVisual,
        selectUpVisual,        selectDownVisual,
        
        actionCaretUp,        actionCaretUpBlock,    actionCaretHome,
        actionCaretDown,    actionCaretDownBlock,    actionCaretEnd,
        actionCaretLeft,    actionCaretLeftWord,    actionCaretLineHome,
        actionCaretRight,    actionCaretRightWord,    actionCaretLineEnd,
        
        actionScrollUpLine,    actionScrollUpBlock,    actionScrollUpPage,
        actionScrollUpHome,    actionScrollDownLine,    actionScrollDownBlock,
        actionScrollDownPage,    actionScrollDownEnd,    actionScrollLeft,
        actionScrollLeftWord,    actionScrollLeftHome,    actionScrollLeftPage,
        actionScrollRight,    actionScrollRightWord,    actionScrollRightEnd,
        actionScrollRightPage,
        
        actionSelectWord,    actionSelectLine,    actionSelectBlock,
        actionSelectAll,    actionSelectLeft,    actionSelectLeftWord,
        actionSelectLeftHome,    actionSelectRight,    actionSelectRightWord,
        actionSelectRightEnd,    actionSelectUp,        actionSelectUpLine,
        actionSelectUpBlock,    actionSelectUpPage,    actionSelectUpHome,
        actionSelectDown,    actionSelectDownLine,    actionSelectDownBlock,
        actionSelectDownPage,    actionSelectDownEnd,
        
        actionDeleteCharNext,    actionDeleteCharPrev,    actionDeleteWordNext,
        actionDeleteWordPrev,    actionDeleteLineNext,    actionDeleteLinePrev,
        actionDeleteBlockNext,    actionDeleteBlockPrev,    actionDeleteToWordHome,
        actionDeleteToWordEnd,    actionDeleteToBlockHome,actionDeleteToBlockEnd,
        actionDeleteToLineHome,    actionDeleteToLineEnd,    actionDeleteToHome,
        actionDeleteToEnd
    };

    /**
     */
    public Text_EditorKit()
    {
        super();
    }
    
    /**
     */
    public static JTextComponent.KeyBinding[] getEditorKeyBinding()
    {
        java.util.List bindlist = new ArrayList();
        JTextComponent editor = new JEditorPane();
        JTextComponent.KeyBinding[] bindings =
                new JTextComponent.KeyBinding[0];
        Keymap keymap = JTextComponent.addKeymap(EDITOR_KEYMAP,
                JTextComponent.getKeymap("BasicEditorPaneUI"));
        bindings = (JTextComponent.KeyBinding[]) bindlist.toArray(bindings);
        JTextComponent.loadKeymap(keymap, bindings, editor.getActions());
        
        keymap.setDefaultAction(defaultKeyTypedAction);
        
        KeyStroke key = KeyStroke.getKeyStroke("INSERT");
        TextAction action = (TextAction) actionInsToggle;
        String actionname = (String) action.getValue(Action.NAME);
        bindlist.add(new JTextComponent.KeyBinding(key, actionname));
        keymap.addActionForKeyStroke(key, actionInsToggle);
                
        return (JTextComponent.KeyBinding[]) bindlist.toArray(bindings);
    }
    
    /**
     */
    private static void bindKeyStrokes(Keymap keymap, java.util.List list)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.EditorKeys");
        for (int i = 0; i < defaultActions.length; i++)
        {
            Action action = defaultActions[i];
            String name   = (String) action.getValue(Action.NAME);
            String keys   = bundle.getString(name);
            if (keys != null && keys != "")
            {
                KeyStroke key = KeyStroke.getKeyStroke(keys);
                list.add(new JTextComponent.KeyBinding(key, name));
                keymap.addActionForKeyStroke(key, action);
            }
        }
    }

    /** {@inheritDoc} */
    public Object clone()
    {
        /*
         * Normally java.lang.Object handles all this, but I cannot call
         * EditorKit.clone() since it is declared abstract. It is an
         * interface bug that should not exist.
         *
         * JAVA 1.3 fixes this problem.
         */
        Text_EditorKit nobj = null;
        try
        {
            nobj = (Text_EditorKit) getClass().newInstance();
            Class clazz = getClass();
            while (clazz != null)
            {
                copyFields(clazz.getDeclaredFields(), nobj);
                clazz = clazz.getSuperclass();
            }
            //nobj.defaultKit = (DefaultEditorKit) defaultKit.clone();
        }
        catch (InstantiationException err)
        {
            System.err.println("editor.Text_EditorKit.clone");
            System.err.println("\t" + err);
            nobj = null;
        }
        catch (IllegalAccessException err)
        {
            System.err.println("editor.Text_EditorKit.clone");
            System.err.println("\t" + err);
            nobj = null;
        }
        catch (ExceptionInInitializerError err)
        {
            System.err.println("editor.Text_EditorKit.clone");
            System.err.println("\t" + err);
            nobj = null;
        }
        catch (SecurityException err)
        {
            System.err.println("editor.Text_EditorKit.clone");
            System.err.println("\t" + err);
            nobj = null;
        }
        return nobj;
    }
    
    /**
     */
    private void copyFields(Field[] fields, Object dst)
        throws IllegalAccessException
    {
        for (int i = 0; i < fields.length; i++)
        {
            Field f = fields[i];
            int mod = f.getModifiers();
            if (Modifier.isFinal(mod))
            {
                //final field--no op
            }
            else if (Modifier.isStatic(mod))
            {
                //static fields--no op
            }
            else
            {
                Object v = f.get(this);
                f.set(dst, v);
            }
        }
    }

    /** {@inheritDoc} */
    public Caret createCaret()
    {
        return null;
        //return new Text_Caret();
    }
    
    /** {@inheritDoc} */
    public Document createDefaultDocument()
    {
        /* TODO -- need to switch to Text_Document when done */
        return new Text_Document();
    }
    
    /** {@inheritDoc} */
    public void install(JEditorPane c)
    {
        /* TODO */
        super.install(c);
    }
    
    /** {@inheritDoc} */
    public void deinstall(JEditorPane c)
    {
        /* TODO */
        super.deinstall(c);
    }
    
    /** {@inheritDoc} */
    public Action[] getActions()
    {
        return defaultActions;
    }
    
    /** {@inheritDoc} */
    public String getContentType()
    {
        return "text/plain";
    }
    
    /** {@inheritDoc} */
    public ViewFactory getViewFactory()
    {
        /* TODO */
        return null;
    }
    
    /** {@inheritDoc} */
    public void read(InputStream in, Document doc, int pos)
        throws IOException, BadLocationException
    {
        read(new InputStreamReader(in), doc, pos);
    }
    
    /** {@inheritDoc} */
    public void read(Reader in, Document doc, int pos)
        throws IOException, BadLocationException
    {
        final int CR        = 0;
        final int LF        = 1;
        final int CRLF        = 2;
        int eoltype         = CR;
        LineNumberReader lnr    = new LineNumberReader(in);
        String line        = lnr.readLine();
        while (line != null)
        {
            if (line.endsWith("\r\n"))
            {
                line = line.replace('\r', '\n');
                line = line.substring(0, line.length() - 1);
                eoltype = CRLF;
            }
            else if (line.endsWith("\r"))
            {
                line = line.replace('\r', '\n');
                eoltype = LF;
            }
            doc.insertString(pos, line, null);
            pos += line.length();
            line = lnr.readLine();
        }
        switch (eoltype)
        {
            case CR:   doc.putProperty(EOLStringProperty, "\n");   break;
            case LF:   doc.putProperty(EOLStringProperty, "\r");   break;
            case CRLF: doc.putProperty(EOLStringProperty, "\r\n"); break;
        }
    }
    
    /** {@inheritDoc} */
    public void write(OutputStream out, Document doc, int pos, int len)
         throws IOException, BadLocationException
    {
        write(new OutputStreamWriter(out), doc, pos, len);
    }
    
    /** {@inheritDoc} */
    public void write(Writer out, Document doc, int pos, int len)
         throws IOException, BadLocationException
    {
        if ((pos < 0) || (doc.getLength() < pos + len))
            throw new BadLocationException("Text_EditorKit.write", pos);
        
        //Deterimine the end of line marker
        String eol = "\n";
        if (doc.getProperty(EOLStringProperty) == null)
        {
            try
            {
                eol = System.getProperty("line.separator");
            }
            catch (SecurityException se)
            {
            }
        }
        else if (doc.getProperty(EOLStringProperty) instanceof String)
        {
            eol = (String) doc.getProperty(EOLStringProperty);
        }
        
        //Now write out the text
        Segment seg = new Segment();
        if (eol.equals("\n"))
        {
            while (len > 0)
            {
                doc.getText(pos, len, seg);
                out.write(seg.array, seg.offset, seg.count);
                pos += seg.count;
                len -= seg.count;
            }
        }
        else
        {
            while (len > 0)
            {
                doc.getText(pos, len, seg);
                int start = seg.offset;
                for (int i = seg.offset; i < seg.offset + seg.count; i++)
                {
                    if (seg.array[i] == '\n')
                    {
                        if (i > start)
                        {
                            out.write(seg.array, start, i - start);
                        }
                        out.write(eol);
                        start = i + 1;
                    }
                    else
                    {
                        out.write(seg.array[i]);
                    }
                }
                if (start < seg.offset + seg.count)
                {
                    out.write(seg.array, start, seg.offset + seg.count - start);
                }
                pos += seg.count;
                len -= seg.count;
            }
        }
        out.flush();
    }
    
    /**
     * Find where the previous page begins.
     */
    private static int calcPageUpPrevLocation(JTextComponent target)
    {
        int ret = target.getCaretPosition();
        Rectangle visible = new Rectangle();
        target.computeVisibleRect(visible);
        int scrollOffset = visible.y;
        visible.y -= visible.height;
        if (visible.y < 0)
            visible.y = 0;
        scrollOffset = scrollOffset - visible.y;
        target.scrollRectToVisible(visible);
        try
        {
            int selectedIndex = ret;
            if(selectedIndex != -1)
            {
                Document doc = target.getDocument();
                Rectangle r = target.modelToView(selectedIndex);
                r.y = (scrollOffset == 0 && visible.y == 0
                            && r.y > 0)
                    ? 0
                    : r.y - scrollOffset;
                selectedIndex = target.viewToModel(
                        new Point(r.x,r.y));
                if ((selectedIndex != 0) &&
                    (selectedIndex  > (doc.getLength()-1)))
                {
                    selectedIndex = doc.getLength()-1;
                }
                if(selectedIndex < 0)
                {
                    selectedIndex = 0;
                }
                ret = selectedIndex;
            }
        }
        catch(BadLocationException bl)
        {
            target.getToolkit().beep();
        }
        return ret;
    }
    
    /**
     * Find where the next page begins.
     */
    private static int calcPageDownNextLocation(JTextComponent target)
    {
        int ret = target.getCaretPosition();
        Rectangle visible = new Rectangle();
        target.computeVisibleRect(visible);
        int scrollOffset = visible.y;
        visible.y += visible.height;
        int maxHeight = target.getHeight();
        if((visible.y+visible.height) > maxHeight)
            visible.y = (maxHeight - visible.height);
        scrollOffset = visible.y - scrollOffset;
        target.scrollRectToVisible(visible);
        try
        {
            int selectedIndex = ret;
            if(selectedIndex != -1)
            {
                Document doc = target.getDocument();
                Rectangle r = target.modelToView(selectedIndex);
                r.y += scrollOffset;
                if (scrollOffset == 0 &&
                    (visible.y + visible.height == maxHeight))
                {
                    r.y = visible.y + visible.height;
                }
                selectedIndex = target.viewToModel(new Point(r.x,r.y));
                if ((selectedIndex != 0) &&
                    (selectedIndex  > (doc.getLength()-1)))
                {
                    selectedIndex = doc.getLength()-1;
                }
                if (selectedIndex  < 0)
                {
                    selectedIndex = 0;
                }
                ret = selectedIndex;
            }
        }
        catch(BadLocationException bl)
        {
            target.getToolkit().beep();
        }
        return ret;
    }
    
    /**
     * Calculate page width left or right.
     */
    private static int calcPageHorzLocation(JTextComponent target, boolean left)
    {
        int ret = target.getCaretPosition();
        Rectangle visible = new Rectangle();
        target.computeVisibleRect(visible);
        visible.x = (left)
                ? Math.max(0, visible.x - visible.width)
                : visible.x + visible.width;
        target.scrollRectToVisible(visible);
        if(ret != -1)
        {
            Document doc = target.getDocument();
            ret = (left)
                    ? target.viewToModel(new Point(visible.x, visible.y))
                    : target.viewToModel(
                            new Point(visible.x + visible.width - 1,
                                    visible.y + visible.height - 1));
            if ((ret != 0) && (ret > (doc.getLength() - 1)))
            {
                ret = doc.getLength() - 1;
            }
            else if(ret  < 0)
            {
                ret = 0;
            }
        }
        return ret;
    }
     
    /**
     * Action to move the selection by way of the
     * getNextVisualPositionFrom method. Constructor indicates direction
     * to use.
     */
    private static class NextVisualPositionAction extends TextAction
    {
        private boolean select;
    private int direction;

        /**
         * Create this action with the appropriate identifier.
         * @param nm  the name of the action, Action.NAME.
         * @param select whether to extend the selection when
         *  changing the caret position.
         */
        NextVisualPositionAction(String nm, boolean select, int direction)
        {
            super(nm);
            this.select = select;
        this.direction = direction;
        }

        /** The operation to perform when this action is triggered. */
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent target = getTextComponent(e);
            if (target != null)
            {
        Caret caret = target.getCaret();
        int dot = caret.getDot();
        Position.Bias[] bias = new Position.Bias[1];
        Point magicPosition = caret.getMagicCaretPosition();
        try
                {
            if(magicPosition == null &&
               (direction == SwingConstants.NORTH ||
            direction == SwingConstants.SOUTH))
                    {
            Rectangle r = target.modelToView(dot);
            magicPosition = new Point(r.x, r.y);
            }
                    dot = target.getUI().getNextVisualPositionFrom(target, dot,
                            Position.Bias.Forward, direction, bias);
            if(bias[0] == null)
                    {
            bias[0] = Position.Bias.Forward;
            }
                    if (select)
                        caret.moveDot(dot);
                    else
                        caret.setDot(dot);
            if(magicPosition != null &&
               (direction == SwingConstants.NORTH ||
            direction == SwingConstants.SOUTH))
                    {
            target.getCaret().setMagicCaretPosition(magicPosition);
            }
                }
                catch (BadLocationException ex)
                {
                }
            }
        }
    }
}

