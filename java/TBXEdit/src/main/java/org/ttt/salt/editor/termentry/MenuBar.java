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
package org.ttt.salt.editor.termentry;

import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;
import org.w3c.dom.Element;
import org.ttt.salt.editor.TBXEditor;
import org.ttt.salt.editor.tbxedit.TBXWindow;
import org.flyingtitans.xml.DOMTreeModel;

/**
 * @version $Id$
 */
public class MenuBar extends org.flyingtitans.xml.TreeViewMenuBar
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Default resource bundle for all MenuBar objects. */
    private static ResourceBundle bundle;
    
    /** Default resource bundle for accelerator and mnemonics. */
    private static ResourceBundle bundleAccel;
    
    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.termentry.MenuBar", Locale.getDefault());
        bundleAccel = bundle;
        //if (System.getProperty("os.name").equals("Mac OS X"))
        //{
        //    defaultBundleAccel = ResourceBundle.getBundle(
        //            "org.flyingtitans.swing.MenuBarMac",
        //            Locale.getDefault());
        //}
    }

    /** The editing window this is associated with. */
    private final TermEntry ewin;
    
    /** The parent editing window. */
    private final TBXWindow parent;
    
    /* Element menu items */
    //CHECKSTYLE: JavadocVariable OFF
    private JMenuItem element_New_admin;
    private JMenuItem element_New_adminGrp;
    private JMenuItem element_New_adminNote;
    private JMenuItem element_New_date;
    private JMenuItem element_New_descrip;
    private JMenuItem element_New_descripGrp;
    private JMenuItem element_New_descripNote;
    private JMenuItem element_New_langSet;
    private JMenuItem element_New_note;
    private JMenuItem element_New_ntig;
    private JMenuItem element_New_ref;
    private JMenuItem element_New_term;
    private JMenuItem element_New_termComp;
    private JMenuItem element_New_termCompGrp;
    private JMenuItem element_New_termCompList;
    private JMenuItem element_New_termGrp;
    private JMenuItem element_New_termNote;
    private JMenuItem element_New_termNoteGrp;
    private JMenuItem element_New_tig;
    private JMenuItem element_New_transac;
    private JMenuItem element_New_transacGrp;
    private JMenuItem element_New_transacNote;
    private JMenuItem element_New_xref;
    //CHECKSTYLE: JavadocVariable ON
    
    /**
     * @param e The term entry to be displayed.
     * @param p The window this menu bar is placed in.
     */
    public MenuBar(TermEntry e, TBXWindow p)
    {
        super(e, null);
        ewin = e;
        parent = p;
    }

//CHECKSTYLE: MethodName OFF
        
    /**
     */
    public void handleMenuItem__File_New()
    {
        try
        {
            TBXEditor.open(null);
        }
        catch (IOException err)
        {
            System.out.println(err);
        }
    }

    /**
     */
    public void handleMenuItem__File_Open()
    {
        parent.getTBXEditor().open();
    }

    /**
     */
    public void handleMenuItem__File_Close()
    {
        parent.close();
    }

    /**
     */
    public void handleMenuItem__File_Save()
    {
        parent.getTBXEditor().save(parent);
    }

    /**
     */
    public void handleMenuItem__File_SaveAs()
    {
        parent.getTBXEditor().saveAs(parent);
    }

    /**
     */
    public void handleMenuItem__File_PageSetup()
    {
        System.err.println("NOT IMPLEMENTED File_PageSetup");
    }

    /**
     */
    public void handleMenuItem__File_Print()
    {
        //CHECKSTYLE: IllegalCatch OFF
        System.err.println("NOT IMPLEMENTED File_Print");
        try
        {
            PrinterJob printjob = PrinterJob.getPrinterJob();
            //ewin.getTBXEditor().print(printjob);
        }
        catch (Throwable err)
        {
            err.printStackTrace(System.err);
        }
        //CHECKSTYLE: IllegalCatch ON
    }

    /**
     */
    public void handleMenuItem__File_Quit()
    {
        org.ttt.salt.editor.Main.getInstance().signalQuit();
    }

    /**
     */
    public void handleMenuItem__Window_Tile()
    {
        System.err.println("NOT IMPLEMENTED Window_Tile");
    }

    /**
     */
    public void handleMenuItem__Window_TileVert()
    {
        System.err.println("NOT IMPLEMENTED Window_TileVert");
    }

    /**
     */
    public void handleMenuItem__Window_Stack()
    {
        System.err.println("NOT IMPLEMENTED Window_Stack");
    }

    /**
     */
    public void handleMenuItem__Help_Contents()
    {
        System.err.println("NOT IMPLEMENTED Help_Contents");
    }

    /**
     */
    public void handleMenuItem__Help_Tutorial()
    {
        System.err.println("NOT IMPLEMENTED Help_Tutorial");
    }

    /**
     */
    public void handleMenuItem__Help_Index()
    {
        System.err.println("NOT IMPLEMENTED Help_Index");
    }

    /**
     */
    public void handleMenuItem__Help_Search()
    {
        System.err.println("NOT IMPLEMENTED Help_Search");
    }

    /**
     */
    public void handleMenuItem__Help_About()
    {
        org.ttt.salt.editor.about.Dialog.display(ewin.getRootPane());
    }

    /**
     */
    public void handleMenuItem__Help_GPL()
    {
        System.err.println("NOT IMPLEMENTED Help_GPL");
    }

//CHECKSTYLE: MethodName ON
    
    /** {@inheritDoc} */
    protected String getString(String key)
    {
        String ret;
        try
        {
            ret = bundle.getString(key);
        }
        catch (MissingResourceException err)
        {
            ret = super.getString(key);
        }
        return ret;
    }

    /**
     */
    protected void updateMenuStatus()
    {
        super.updateMenuStatus();
        
        file_save.setEnabled(true);
        file_pagesetup.setEnabled(false);

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
    protected void buildElementMenu()
    {
        super.buildElementMenu();
        element_New_admin = createMenuItem("Element_New_admin");
        element_New_adminGrp = createMenuItem("Element_New_adminGrp");
        element_New_adminNote = createMenuItem("Element_New_adminNote");
        element_New_date = createMenuItem("Element_New_date");
        element_New_descrip = createMenuItem("Element_New_descrip");
        element_New_descripGrp = createMenuItem("Element_New_descripGrp");
        element_New_descripNote = createMenuItem("Element_New_descripNote");
        element_New_langSet = createMenuItem("Element_New_langSet");
        element_New_note = createMenuItem("Element_New_note");
        element_New_ntig = createMenuItem("Element_New_ntig");
        element_New_ref     = createMenuItem("Element_New_ref");
        element_New_term = createMenuItem("Element_New_term");
        element_New_termComp = createMenuItem("Element_New_termComp");
        element_New_termCompGrp = createMenuItem("Element_New_termCompGrp");
        element_New_termCompList = createMenuItem("Element_New_termCompList");
        element_New_termGrp = createMenuItem("Element_New_termGrp");
        element_New_termNote = createMenuItem("Element_New_termNote");
        element_New_termNoteGrp = createMenuItem("Element_New_termNoteGrp");
        element_New_tig     = createMenuItem("Element_New_tig");
        element_New_transac = createMenuItem("Element_New_transac");
        element_New_transacGrp = createMenuItem("Element_New_transacGrp");
        element_New_transacNote = createMenuItem("Element_New_transacNote");
        element_New_xref = createMenuItem("Element_New_xref");

        element.addSeparator();
        element.add(element_New_admin);
        element.add(element_New_adminGrp);
        element.add(element_New_adminNote);
        element.addSeparator();
        element.add(element_New_descrip);
        element.add(element_New_descripGrp);
        element.add(element_New_descripNote);
        element.addSeparator();
        element.add(element_New_term);
        element.add(element_New_termComp);
        element.add(element_New_termCompGrp);
        element.add(element_New_termCompList);
        element.add(element_New_termGrp);
        element.add(element_New_termNote);
        element.add(element_New_termNoteGrp);
        element.addSeparator();
        element.add(element_New_transac);
        element.add(element_New_transacGrp);
        element.add(element_New_transacNote);
        element.addSeparator();
        element.add(element_New_date);
        element.add(element_New_langSet);
        element.add(element_New_note);
        element.add(element_New_tig);
        element.add(element_New_ntig);
        element.add(element_New_ref);
        element.add(element_New_xref);
    }
    
    /**
     */
    protected void updateFileMenuStatus()
    {
        //TODO--when preferences are built then let menu item be enabled.
        super.updateFileMenuStatus();
        file_print.setEnabled(false);
        file_preferences.setEnabled(false);
    }
    
    /**
     * @param path The {@link javax.swing.tree.TreePath} of the selected element
     *  in the tree view.
     */
    protected void updateElementMenuStatus(TreePath path)
    {
        elementMenuDisable();
        if (path != null)
        {
            DOMTreeModel.DOMTreeNode node =
                (DOMTreeModel.DOMTreeNode) path.getLastPathComponent();
            Element elem = node.getElement();
            String name = elem.getTagName();
            if (name.equals("termEntry"))
            {
                element_Delete.setEnabled(false);
                auxInfoEnable();
                element_New_langSet.setEnabled(true);
            }
            else if (name.equals("admin"))
            {
                if (getParentTagName(path).equals("adminGrp"))
                    element_Delete.setEnabled(false);
            }
            else if (name.equals("adminGrp"))
            {
                element_New_adminNote.setEnabled(true);
                element_New_note.setEnabled(true);
                element_New_ref.setEnabled(true);
                element_New_xref.setEnabled(true);
            }
            else if (name.equals("descrip"))
            {
                if (getParentTagName(path).equals("descripGrp"))
                    element_Delete.setEnabled(false);
            }
            else if (name.equals("descripGrp"))
            {
                element_New_descripNote.setEnabled(true);
                element_New_admin.setEnabled(true);
                element_New_adminGrp.setEnabled(true);
                element_New_note.setEnabled(true);
                element_New_ref.setEnabled(true);
                element_New_xref.setEnabled(true);
            }
            else if (name.equals("langSet"))
            {
                if (getParentTagName(path).equals("termEntry"))
                {
                    String[] list = {"langSet"};
                    if (getCountOfTags(path.getParentPath(), list) == 1)
                        element_Delete.setEnabled(false);
                }
                auxInfoEnable();
                element_New_tig.setEnabled(true);
                element_New_ntig.setEnabled(true);
            }
            else if (name.equals("ntig"))
            {
                if (getParentTagName(path).equals("langSet"))
                {
                    String[] list = {"tig", "ntig"};
                    if (getCountOfTags(path.getParentPath(), list) == 1)
                        element_Delete.setEnabled(false);
                }
                auxInfoEnable();
            }
            else if (name.equals("term"))
            {
                if (getParentTagName(path).equals("tig")
                    || getParentTagName(path).equals("termGrp"))
                    element_Delete.setEnabled(false);
            }
            else if (name.equals("termComp"))
            {
                if (getParentTagName(path).equals("termCompGrp"))
                {
                    element_Delete.setEnabled(false);
                }
                if (getParentTagName(path).equals("termCompList"))
                {
                    String[] list = {"termComp", "termCompGrp"};
                    if (getCountOfTags(path.getParentPath(), list) == 1)
                        element_Delete.setEnabled(false);
                }
            }
            else if (name.equals("termCompGrp"))
            {
                if (getParentTagName(path).equals("termCompList"))
                {
                    String[] list = {"termComp", "termCompGrp"};
                    if (getCountOfTags(path.getParentPath(), list) == 1)
                        element_Delete.setEnabled(false);
                }
                element_New_termNote.setEnabled(true);
                element_New_termNoteGrp.setEnabled(true);
                noteLinkInfoEnable();
            }
            else if (name.equals("termCompList"))
            {
                auxInfoEnable();
                element_New_termComp.setEnabled(true);
                element_New_termCompGrp.setEnabled(true);
            }
            else if (name.equals("termGrp"))
            {
                if (getParentTagName(path).equals("ntig"))
                {
                    element_Delete.setEnabled(false);
                }
                element_New_termNote.setEnabled(true);
                element_New_termNoteGrp.setEnabled(true);
                noteLinkInfoEnable();
            }
            else if (name.equals("termNote"))
            {
                if (getParentTagName(path).equals("termNoteGrp"))
                {
                    element_Delete.setEnabled(false);
                }
            }
            else if (name.equals("termNoteGrp"))
            {
                noteLinkInfoEnable();
            }
            else if (name.equals("tig"))
            {
                if (getParentTagName(path).equals("langSet"))
                {
                    String[] list = {"tig", "ntig"};
                    if (getCountOfTags(path.getParentPath(), list) == 1)
                        element_Delete.setEnabled(false);
                }
                element_New_termNote.setEnabled(true);
                auxInfoEnable();
            }
            else if (name.equals("transac"))
            {
                if (getParentTagName(path).equals("transacGrp"))
                {
                    element_Delete.setEnabled(false);
                }
            }
            else if (name.equals("transacGrp"))
            {
                element_New_transacNote.setEnabled(true);
                element_New_date.setEnabled(true);
                element_New_note.setEnabled(true);
                element_New_ref.setEnabled(true);
                element_New_xref.setEnabled(true);
            }
        }
        else
        {
            element_Delete.setEnabled(false);
        }
    }
    
    /**
     */
    protected void elementMenuDisable()
    {
        element_Delete.setEnabled(true);
        element_New_admin.setEnabled(false);
        element_New_adminGrp.setEnabled(false);
        element_New_adminNote.setEnabled(false);
        element_New_date.setEnabled(false);
        element_New_descrip.setEnabled(false);
        element_New_descripGrp.setEnabled(false);
        element_New_descripNote.setEnabled(false);
        element_New_langSet.setEnabled(false);
        element_New_note.setEnabled(false);
        element_New_ntig.setEnabled(false);
        element_New_ref.setEnabled(false);
        element_New_term.setEnabled(false);
        element_New_termComp.setEnabled(false);
        element_New_termCompGrp.setEnabled(false);
        element_New_termCompList.setEnabled(false);
        element_New_termGrp.setEnabled(false);
        element_New_termNote.setEnabled(false);
        element_New_termNoteGrp.setEnabled(false);
        element_New_tig.setEnabled(false);
        element_New_transac.setEnabled(false);
        element_New_transacGrp.setEnabled(false);
        element_New_transacNote.setEnabled(false);
        element_New_xref.setEnabled(false);
    }
    
    /**
     */
    private void auxInfoEnable()
    {
        element_New_descrip.setEnabled(true);
        element_New_descripGrp.setEnabled(true);
        element_New_admin.setEnabled(true);
        element_New_adminGrp.setEnabled(true);
        element_New_transacGrp.setEnabled(true);
        element_New_note.setEnabled(true);
        element_New_ref.setEnabled(true);
        element_New_xref.setEnabled(true);
    }
    
    /**
     */
    private void noteLinkInfoEnable()
    {
        element_New_admin.setEnabled(true);
        element_New_adminGrp.setEnabled(true);
        element_New_transacGrp.setEnabled(true);
        element_New_note.setEnabled(true);
        element_New_ref.setEnabled(true);
        element_New_xref.setEnabled(true);
    }
}

