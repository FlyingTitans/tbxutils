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
import java.lang.reflect.*;
import java.util.*;
import java.awt.Event;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * @author Lance FInn Helsten
 * @version $Id$
 */
class EditorMenuBar implements ActionListener, FocusListener, MouseListener
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private static final String UPDATE_STATUS = "UpdateStatus";

    /** resource bundle that is used by all menus */
    private static ResourceBundle bundle;

    /** the editing window this is associated with */
    private final EditorWindow ewin;

    /** current text component in use */
    private JTextComponent text;

    private final JMenuBar bar = new JMenuBar();

    private final JMenu file = new JMenu(bundle.getString("File"));
    private final JMenuItem file_new = createMenuItem("File_New");
    private final JMenuItem file_newstationary = createMenuItem("File_NewS");
    private final JMenuItem file_open = createMenuItem("File_Open");
    private final JMenu file_openrecent = new JMenu(
            bundle.getString("File_OpenRecent"));
    private final JMenuItem file_openselect = createMenuItem("File_OpenSelect");
    private final JMenuItem file_openrelated = createMenuItem(
            "File_OpenRelated");
    private final JMenuItem file_insert = createMenuItem("File_Insert");
    private final JMenuItem file_close = createMenuItem("File_Close");
    private final JMenuItem file_save = createMenuItem("File_Save");
    private final JMenuItem file_saveas = createMenuItem("File_SaveAs");
    private final JMenuItem file_savecopy = createMenuItem("File_SaveCopy");
    private final JMenuItem file_revert = createMenuItem("File_Revert");
    private final JMenuItem file_pagesetup = createMenuItem("File_PageSetup");
    private final JMenuItem file_print = createMenuItem("File_Print");
    private final JMenuItem file_printselect = createMenuItem(
            "File_PrintSelect");
    private final JMenuItem file_preferences = createMenuItem(
            "File_Preferences");
    private final JMenuItem file_quit = createMenuItem("File_Quit");

    private final JMenu edit = new JMenu(bundle.getString("Edit"));
    private final JMenuItem edit_undo = createMenuItem("Edit_Undo");
    private final JMenuItem edit_redo = createMenuItem("Edit_Redo");
    private final JMenuItem edit_cut = createMenuItem("Edit_Cut");
    private final JMenuItem edit_copy = createMenuItem("Edit_Copy");
    private final JMenuItem edit_paste = createMenuItem("Edit_Paste");
    private final JMenuItem edit_selectall = createMenuItem("Edit_SelectAll");
    private final JMenuItem edit_balance = createMenuItem("Edit_Balance");
    private final JMenuItem edit_shiftleft = createMenuItem("Edit_ShiftLeft");
    private final JMenuItem edit_shiftright = createMenuItem("Edit_ShiftRight");
    private final JMenuItem edit_zapgremlins = createMenuItem(
            "Edit_ZapGremlins");
    private final JCheckBoxMenuItem edit_showhidden = createCheckBoxMenuItem(
            "Edit_ShowHidden", false);

    private final JRadioButtonMenuItem editLineEndings_LF =
        createRadioButtonMenuItem("EditLineEndings_LF");
    private final JRadioButtonMenuItem editLineEndings_CR =
        createRadioButtonMenuItem("EditLineEndings_CR");
    private final JRadioButtonMenuItem editLineEndings_CRLF =
        createRadioButtonMenuItem("EditLineEndings_CRLF");

    private final JRadioButtonMenuItem editFileEncode_UTF16 =
        createRadioButtonMenuItem("EditFileEncode_UTF16");
    private final JRadioButtonMenuItem editFileEncode_UTF8 =
        createRadioButtonMenuItem("EditFileEncode_UTF8");
    private final JRadioButtonMenuItem editFileEncode_UTF7 =
        createRadioButtonMenuItem("EditFileEncode_UTF7");
    private final JRadioButtonMenuItem editFileEncode_ISOLatin1 =
        createRadioButtonMenuItem("EditFileEncode_ISOLatin1");
    private final JRadioButtonMenuItem editFileEncode_ISOLatin2 =
        createRadioButtonMenuItem("EditFileEncode_ISOLatin2");
    private final JRadioButtonMenuItem editFileEncode_ISOLatin3 =
        createRadioButtonMenuItem("EditFileEncode_ISOLatin3");
    private final JRadioButtonMenuItem editFileEncode_ISOLatin4 =
        createRadioButtonMenuItem("EditFileEncode_ISOLatin4");
    private final JRadioButtonMenuItem editFileEncode_ISOLatin5 =
        createRadioButtonMenuItem("EditFileEncode_ISOLatin5");
    private final JRadioButtonMenuItem editFileEncode_ISO8859_5 =
        createRadioButtonMenuItem("EditFileEncode_ISO8859_5");
    private final JRadioButtonMenuItem editFileEncode_ISO8859_7 =
        createRadioButtonMenuItem("EditFileEncode_ISO8859_7");
    private final JRadioButtonMenuItem editFileEncode_ISO2022_JP =
        createRadioButtonMenuItem("EditFileEncode_ISO2022_JP");

    private final JMenu find = new JMenu(bundle.getString("Find"));
    private final JMenuItem find_find = createMenuItem("Find_Find");
    private final JMenuItem find_next = createMenuItem("Find_Next");
    private final JMenuItem find_prev = createMenuItem("Find_Prev");
    private final JMenuItem find_replacenext = createMenuItem(
            "Find_ReplaceNext");
    private final JMenuItem find_replaceprev = createMenuItem(
            "Find_ReplacePrev");
    private final JMenuItem find_replace = createMenuItem("Find_Replace");
    private final JMenuItem find_replaceall = createMenuItem("Find_ReplaceAll");
    private final JMenuItem find_setfindstring = createMenuItem(
            "Find_SetFindString");
    private final JMenuItem find_setreplacestring = createMenuItem(
            "Find_SetReplaceString");

    private final JMenu window = new JMenu(bundle.getString("Window"));
    private final JMenuItem window_zoom = createMenuItem("Window_Zoom");
    private final JMenuItem window_zoomsize = createMenuItem("Window_ZoomSize");
    private final JMenuItem window_tile = createMenuItem("Window_Tile");
    private final JMenuItem window_tilevert = createMenuItem("Window_TileVert");
    private final JMenuItem window_stack = createMenuItem("Window_Stack");

    private final JMenu help = new JMenu(bundle.getString("Help"));
    private final JMenuItem help_contents = createMenuItem("Help_Contents");
    private final JMenuItem help_tutorial = createMenuItem("Help_Tutorial");
    private final JMenuItem help_index = createMenuItem("Help_Index");
    private final JMenuItem help_search = createMenuItem("Help_Search");
    private final JMenuItem help_about = createMenuItem("Help_About");

    public EditorMenuBar(EditorWindow ewin)
    {
        this.ewin = ewin;

        bar.add(file);
        bar.add(edit);
        bar.add(find);
        bar.add(window);
        bar.add(help);

        file.addMouseListener(this);
        edit.addMouseListener(this);
        find.addMouseListener(this);
        window.addMouseListener(this);
        help.addMouseListener(this);

        file.add(file_new);
        file.add(file_newstationary);
        file.add(file_open);
        file.add(file_openrecent);
        file.add(file_openselect);
        file.add(file_openrelated);
        file.add(file_insert);
        file.addSeparator();
        file.add(file_close);
        file.add(file_save);
        file.add(file_saveas);
        file.add(file_savecopy);
        file.add(file_revert);
        file.addSeparator();
        file.add(file_pagesetup);
        file.add(file_print);
        file.add(file_printselect);
        file.addSeparator();
        file.add(file_preferences);
        file.addSeparator();
        file.add(file_quit);

        edit.add(edit_undo);
        edit.add(edit_redo);
        edit.addSeparator();
        edit.add(edit_cut);
        edit.add(edit_copy);
        edit.add(edit_paste);
        edit.add(edit_selectall);
        edit.addSeparator();
        edit.add(edit_balance);
        edit.add(edit_shiftleft);
        edit.add(edit_shiftright);
        edit.add(edit_zapgremlins);
        edit.addSeparator();
        edit.add(edit_showhidden);

        JMenu editLineEndings = new JMenu(bundle.getString("Edit_LineEndings"));
        editLineEndings.add(editLineEndings_LF);
        editLineEndings.add(editLineEndings_CR);
        editLineEndings.add(editLineEndings_CRLF);
        edit.add(editLineEndings);
        ButtonGroup editLineEndingsGroup = new ButtonGroup();
        editLineEndingsGroup.add(editLineEndings_LF);
        editLineEndingsGroup.add(editLineEndings_CR);
        editLineEndingsGroup.add(editLineEndings_CRLF);
        editLineEndings_LF.setSelected(true);

        JMenu editFileEncode = new JMenu(bundle.getString("Edit_FileEncode"));
        editFileEncode.add(editFileEncode_UTF16);
        editFileEncode.add(editFileEncode_UTF8);
        editFileEncode.add(editFileEncode_UTF7);
        editFileEncode.add(editFileEncode_ISOLatin1);
        editFileEncode.add(editFileEncode_ISOLatin2);
        editFileEncode.add(editFileEncode_ISOLatin3);
        editFileEncode.add(editFileEncode_ISOLatin4);
        editFileEncode.add(editFileEncode_ISOLatin5);
        editFileEncode.add(editFileEncode_ISO8859_5);
        editFileEncode.add(editFileEncode_ISO8859_7);
        editFileEncode.add(editFileEncode_ISO2022_JP);
        edit.add(editFileEncode);
        ButtonGroup editFileEncodeGroup = new ButtonGroup();
        editFileEncodeGroup.add(editFileEncode_UTF16);
        editFileEncodeGroup.add(editFileEncode_UTF8);
        editFileEncodeGroup.add(editFileEncode_UTF7);
        editFileEncodeGroup.add(editFileEncode_ISOLatin1);
        editFileEncodeGroup.add(editFileEncode_ISOLatin2);
        editFileEncodeGroup.add(editFileEncode_ISOLatin3);
        editFileEncodeGroup.add(editFileEncode_ISOLatin4);
        editFileEncodeGroup.add(editFileEncode_ISOLatin5);
        editFileEncodeGroup.add(editFileEncode_ISO8859_5);
        editFileEncodeGroup.add(editFileEncode_ISO8859_7);
        editFileEncodeGroup.add(editFileEncode_ISO2022_JP);
        editFileEncode_UTF8.setSelected(true);

        find.add(find_find);
        find.addSeparator();
        find.add(find_next);
        find.add(find_prev);
        find.add(find_replacenext);
        find.add(find_replaceprev);
        find.add(find_replace);
        find.add(find_replaceall);
        find.addSeparator();
        find.add(find_setfindstring);
        find.add(find_setreplacestring);

        window.add(window_zoom);
        window.add(window_tile);
        window.add(window_tilevert);
        window.add(window_stack);

        help.add(help_contents);
        help.add(help_tutorial);
        help.add(help_index);
        help.add(help_search);
        help.addSeparator();
        help.add(help_about);
    }

    /**
     */
    public JMenuBar getMenuBar()
    {
        return bar;
    }

    /**
     */
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getActionCommand() == UPDATE_STATUS)
        {
            updateMenuStatus();
        }
        else
        {
            try
            {
                Class[] parameters = {};
                Object[] arguments = {};
                String name = "handleAction__" + evt.getActionCommand();
                Method meth = getClass().getDeclaredMethod(name, parameters);
                meth.invoke(this, arguments);
            }
            catch (NoSuchMethodException err)
            {
                String cmd = evt.getActionCommand();
                if (cmd == "")
                    cmd = "????";
                System.err.println("No method for action "
                    + cmd + ".");
            }
            catch (InvocationTargetException err)
            {
                //the action handler method threw an exception
            }
            catch (IllegalAccessException err)
            {
                //This should not happen
            }
            catch (Throwable err)
            {
                err.printStackTrace(System.err);
            }
            updateMenuStatus();
        }
    }

    /**
     */
    public void focusGained(FocusEvent evt)
    {
        if (evt.getComponent() instanceof JTextComponent)
        {
            text = (JTextComponent) evt.getComponent();
        }
        updateMenuStatus();
    }

    /**
     */
    public void focusLost(FocusEvent evt)
    {
        //no op
    }

    /**
     */
    public void mouseEntered(MouseEvent evt)
    {
        //no op
    }

    /**
     */
    public void mouseExited(MouseEvent evt)
    {
        //no op
    }

    /**
     */
    public void mousePressed(MouseEvent evt)
    {
        
        updateMenuStatus();
    }

    /**
     */
    public void mouseReleased(MouseEvent evt)
    {
        //no op
    }

    /**
     */
    public void mouseClicked(MouseEvent evt)
    {
        //no op
    }

    /**
     */
    private void updateMenuStatus()
    {
        Document doc = ewin.getEditor().getDocument();
        boolean select = (text.getSelectionStart() != text.getSelectionEnd());

        file_new.setEnabled(true);
        file_newstationary.setEnabled(false);
        file_open.setEnabled(true);
        file_openrecent.setEnabled(false);
        file_openselect.setEnabled(false);
        file_openrelated.setEnabled(false);
        file_insert.setEnabled(true);
        file_close.setEnabled(true);
        file_save.setEnabled(ewin.getEditor().isDirty());
        file_saveas.setEnabled(true);
        file_savecopy.setEnabled(true);
        file_revert.setEnabled(true);
        file_pagesetup.setEnabled(false);
        file_print.setEnabled(true);
        file_printselect.setEnabled(false);
        file_preferences.setEnabled(true);
        file_quit.setEnabled(true);

        edit_undo.setEnabled(ewin.getEditor().getUndo().canUndo());
        edit_redo.setEnabled(ewin.getEditor().getUndo().canRedo());
        edit_cut.setEnabled(select);
        edit_copy.setEnabled(select);
        edit_paste.setEnabled(true);
        edit_selectall.setEnabled(doc.getLength() != 0);
        edit_balance.setEnabled(select);
        edit_shiftleft.setEnabled(select);
        edit_shiftright.setEnabled(select);
        edit_zapgremlins.setEnabled(false);
        edit_showhidden.setEnabled(true);

        //editLineEndings_LF.setEnabled(true);
        //editLineEndings_CR.setEnabled(true);
        //editLineEndings_CRLF.setEnabled(true);

        //editFileEncode_UTF16.setEnabled(true);
        //editFileEncode_UTF8.setEnabled(true);
        //editFileEncode_UTF7.setEnabled(true);
        //editFileEncode_ISOLatin1.setEnabled(true);
        //editFileEncode_ISOLatin2.setEnabled(true);
        //editFileEncode_ISOLatin3.setEnabled(true);
        //editFileEncode_ISOLatin4.setEnabled(true);
        //editFileEncode_ISOLatin5.setEnabled(true);
        //editFileEncode_ISO8859_5.setEnabled(true);
        //editFileEncode_ISO8859_7.setEnabled(true);
        //editFileEncode_ISO2022_JP.setEnabled(true);

        org.ttt.salt.editor.find.Dialog find =
                org.ttt.salt.editor.find.Dialog.getInstance();
        String sstr = find.getFindString();

        find_find.setEnabled(true);
        find_next.setEnabled(sstr.length() != 0);
        find_prev.setEnabled(sstr.length() != 0);
        find_replacenext.setEnabled(select & sstr.length() != 0);
        find_replaceprev.setEnabled(select & sstr.length() != 0);
        find_replace.setEnabled(select & sstr.length() != 0);
        find_replaceall.setEnabled(sstr.length() != 0);
        find_setfindstring.setEnabled(true);
        find_setreplacestring.setEnabled(true);

        window_zoom.setEnabled(false);
        window_tile.setEnabled(false);
        window_tilevert.setEnabled(false);
        window_stack.setEnabled(false);

        help_contents.setEnabled(false);
        help_tutorial.setEnabled(false);
        help_index.setEnabled(false);
        help_search.setEnabled(false);
        help_about.setEnabled(true);
    }

    /**
     */
    private void handleAction__File_New()
    {
        try
        {
            Editor editor = new Editor();
            EditorWindow editwin = new EditorWindow(editor);
        }
        catch (IOException err)
        {
            System.err.println("NOT DONE: IOException occured.");
        }
    }

    /**
     */
    private void handleAction__File_NewS()
    {
    }

    /**
     */
    private void handleAction__File_Open()
    {
        ewin.getEditor().open();
        text.getCaret().setDot(0);
    }

    /**
     */
    private void handleAction__File_OpenSelect()
    {
    }

    /**
     */
    private void handleAction__File_OpenRelated()
    {
    }

    /**
     */
    private void handleAction__File_Insert()
    {
        ewin.getEditor().insert(text.getCaret(), ewin.getPrimaryComponent());
    }

    /**
     */
    private void handleAction__File_Close()
    {
        ewin.close();
    }

    /**
     */
    private void handleAction__File_Save()
    {
        ewin.getEditor().save(ewin);
    }

    /**
     */
    private void handleAction__File_SaveAs()
    {
        ewin.getEditor().saveAs(ewin);
    }

    /**
     */
    private void handleAction__File_SaveCopy()
    {
        ewin.getEditor().saveCopy(ewin);
    }

    /**
     */
    private void handleAction__File_Revert()
    {
        ewin.getEditor().revert(ewin);
    }

    /**
     */
    private void handleAction__File_PageSetup()
    {
    }

    /**
     */
    private void handleAction__File_Print()
    {
        try
        {
            PrinterJob printjob = PrinterJob.getPrinterJob();
            ewin.getEditor().print(printjob);
        }
        catch (Throwable err)
        {
            err.printStackTrace(System.err);
        }
    }

    /**
     */
    private void handleAction__File_PrintSelect()
    {
    }

    /**
     */
    private void handleAction__File_Preferences()
    {
        org.ttt.salt.editor.preferences.Preferences.getInstance().showDialog();
    }

    /**
     */
    private void handleAction__File_Quit()
    {
        org.ttt.salt.editor.Main.getInstance().signalQuit();
    }

    /**
     */
    private void handleAction__Edit_Undo()
    {
        ewin.getEditor().getUndo().undo();
    }

    /**
     */
    private void handleAction__Edit_Redo()
    {
        ewin.getEditor().getUndo().redo();
    }

    /**
     */
    private void handleAction__Edit_Cut()
    {
        //text.cut();
    }

    /**
     */
    private void handleAction__Edit_Copy()
    {
        //text.copy();
    }

    /**
     */
    private void handleAction__Edit_Paste()
    {
        //text.paste();
    }

    /**
     */
    private void handleAction__Edit_SelectAll()
    {
        text.selectAll();
    }

    /**
     */
    private void handleAction__Edit_Balance()
    {
    }

    /**
     */
    private void handleAction__Edit_ShiftLeft()
    {
    }

    /**
     */
    private void handleAction__Edit_ShiftRight()
    {
    }

    /**
     */
    private void handleAction__Edit_ZapGremlins()
    {
    }

    /**
     */
    private void handleAction__Edit_ShowHidden()
    {
        if (edit_showhidden.isSelected())
        {
        }
        else
        {
        }
    }

    /**
     */
    private void handleAction__Find_Find()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().setVisible(true);
    }

    /**
     */
    private void handleAction__Find_Next()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().findNext();
    }

    /**
     */
    private void handleAction__Find_Prev()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().findPrev();
    }

    /**
     */
    private void handleAction__Find_ReplaceNext()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().replaceFindNext();
    }

    /**
     */
    private void handleAction__Find_ReplacePrev()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().replaceFindPrev();
    }

    /**
     */
    private void handleAction__Find_Replace()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().replace();
    }

    /**
     */
    private void handleAction__Find_ReplaceAll()
    {
        org.ttt.salt.editor.find.Dialog.getInstance().replaceAll();
    }

    /**
     */
    private void handleAction__Find_SetFindString()
    {
        String str = text.getSelectedText();
        org.ttt.salt.editor.find.Dialog.getInstance().setFindString(str);
    }

    /**
     */
    private void handleAction__Find_SetReplaceString()
    {
        String str = text.getSelectedText();
        org.ttt.salt.editor.find.Dialog.getInstance().setReplaceString(str);
    }

    /**
     */
    private void handleAction__Window_Zoom()
    {
    }

    /**
     */
    private void handleAction__Window_Tile()
    {
    }

    /**
     */
    private void handleAction__Window_TileVert()
    {
    }

    /**
     */
    private void handleAction__Window_Stack()
    {
    }

    /**
     */
    private void handleAction__Help_Contents()
    {
    }

    /**
     */
    private void handleAction__Help_Tutorial()
    {
    }

    /**
     */
    private void handleAction__Help_Index()
    {
    }

    /**
     */
    private void handleAction__Help_Search()
    {
    }

    /**
     */
    private void handleAction__Help_About()
    {
        org.ttt.salt.editor.about.Dialog.display(ewin.getPrimaryComponent());
    }

    /**
     */
    private JMenuItem createMenuItem(String key)
    {
        JMenuItem ret = new JMenuItem(bundle.getString(key));
        addAccelAndMnemonic(key, ret);
        bar.registerKeyboardAction(this, "",
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                                       java.awt.Event.CTRL_MASK, false),
                                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return ret;
    }

    /**
     */
    private JCheckBoxMenuItem createCheckBoxMenuItem(String key,
        boolean select)
    {
        JCheckBoxMenuItem ret = new JCheckBoxMenuItem(bundle.getString(key),
            select);
        addAccelAndMnemonic(key, ret);
        return ret;
    }

    /**
     */
    private JRadioButtonMenuItem createRadioButtonMenuItem(String key)
    {
        String title = bundle.getString(key);
        JRadioButtonMenuItem ret = new JRadioButtonMenuItem(title);
        addAccelAndMnemonic(key, ret);
        return ret;
    }

    /**
     */
    private void addAccelAndMnemonic(String key, JMenuItem item)
    {
        try
        {
            String mnemonic = bundle.getString(key + "_Mnemonic");
            if (mnemonic.length() == 0)
                throw new MissingResourceException("", "", "");
            item.setMnemonic(mnemonic.charAt(0));
        }
        catch (MissingResourceException err)
        {
        }
        try
        {
            String accel = bundle.getString(key + "_Accel");
            KeyStroke press = KeyStroke.getKeyStroke(accel);
            int keycode = press.getKeyCode();
            int mod = press.getModifiers();
            KeyStroke release = KeyStroke.getKeyStroke(keycode, mod, true);
            bar.registerKeyboardAction(this, UPDATE_STATUS,
                   press, JComponent.WHEN_IN_FOCUSED_WINDOW);
            item.setAccelerator(release);
        }
        catch (MissingResourceException err)
        {
            //noop
        }
        catch (NoSuchElementException err)
        {
            //noop
        }
        item.setActionCommand(key);
        item.addActionListener(this);
    }

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle("org.ttt.salt.editor.xmleditor.EditorMenuBar",
                                          Locale.getDefault());
    }
}

