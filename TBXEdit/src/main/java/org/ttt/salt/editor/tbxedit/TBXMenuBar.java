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

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.awt.print.PrinterJob;
import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;
import org.w3c.dom.Element;
import org.ttt.salt.editor.TBXEditor;
import org.ttt.salt.editor.TBXDocument;
import org.flyingtitans.xml.DOMTreeModel;

/**
 * @version $Id$
 */
public class TBXMenuBar extends org.flyingtitans.xml.TreeViewMenuBar
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Default resource bundle for all MenuBar objects. */
    private static ResourceBundle bundle;
    
    /** Default resource bundle for accelerator and mnemonics. */
    private static ResourceBundle bundleAccel;
    
    /** */
    static final Tag MARTIF;

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.tbxedit.TBXMenuBar", Locale.getDefault());
        bundleAccel = bundle;
        //if (System.getProperty("os.name").equals("Mac OS X"))
        //{
        //    defaultBundleAccel = ResourceBundle.getBundle(
        //            "org.flyingtitans.swing.MenuBarMac",
        //            Locale.getDefault());
        //}
        
        //Build the structure
        Tag martif = new Tag("martif");
        Tag martifHeader = new Tag("martifHeader");
        Tag p = new Tag("p");
        Tag fileDesc = new Tag("fileDesc");
        Tag titleStmt = new Tag("titleStmt");
        Tag title = new Tag("title");
        Tag publicationStmt = new Tag("publicationStmt");
        Tag sourceDesc = new Tag("sourceDesc");
        Tag encodingDesc = new Tag("encodingDesc");
        Tag ude = new Tag("ude");
        Tag map = new Tag("map");
        Tag revisionDesc = new Tag("revisionDesc");
        Tag change = new Tag("change");
        Tag text = new Tag("text");
        Tag front = new Tag("front");
        Tag body = new Tag("body");
        Tag back = new Tag("back");
        Tag refObjectList = new Tag("refObjectList");
        Tag refObject = new Tag("refObject");
        Tag item = new Tag("item");
        Tag itemGrp = new Tag("itemGrp");
        Tag itemSet = new Tag("itemSet");
        Tag termEntry = new Tag("termEntry");
        
        martif.addChild(1, 1, martifHeader);
        martif.addChild(1, 1, text);
        
        martifHeader.addChild(1, 1, fileDesc);
        martifHeader.addChild(0, 1, encodingDesc);
        martifHeader.addChild(0, 1, revisionDesc);
        
        fileDesc.addChild(0, 1, titleStmt);
        fileDesc.addChild(0, 1, publicationStmt);
        fileDesc.addChild(1, Tag.UNLIMITED, sourceDesc);
        
        titleStmt.addChild(1, 1, title);
        //titleStmt.addChild(0, Tag.UNLIMITED, note);
        
        publicationStmt.addChild(1, Tag.UNLIMITED, p);

        sourceDesc.addChild(1, Tag.UNLIMITED, p);

        encodingDesc.addChild(0, 1, ude);
        encodingDesc.addChild(1, Tag.UNLIMITED, p);
        
        ude.addChild(1, Tag.UNLIMITED, map);
        
        revisionDesc.addChild(1, Tag.UNLIMITED, change);
        
        change.addChild(1, Tag.UNLIMITED, p);
        
        text.addChild(0, 1, front);
        text.addChild(1, 1, body);
        text.addChild(0, 1, back);
        
        body.addChild(1, Tag.UNLIMITED, termEntry);
        
        back.addChild(0, Tag.UNLIMITED, refObjectList);
        
        refObjectList.addChild(1, Tag.UNLIMITED, refObject);
        
        Tag[] refobj = {itemSet, itemGrp, item};
        refObject.addGroup(1, Tag.UNLIMITED, refobj);
        
        //Tag[] noteLinkInfo = {admin, adminGrp, transacGrp, note, ref, xref};
        itemGrp.addChild(1, 1, item);
        //itemGrp.addGroup(0, Tag.UNLIMITED, noteLinkInfo);
        
        Tag[] itemset = {itemGrp, item};
        itemSet.addGroup(1, Tag.UNLIMITED, itemset);
        
        MARTIF = martif;
    }

    /** The editing window this is associated with. */
    private final TBXWindow ewin;
    
    /* Element menu items */
    //CHECKSTYLE: JavadocVariable OFF
    private JMenuItem element_New_termEntry;
    
    private JMenuItem element_New_back;
    private JMenuItem element_New_body;
    private JMenuItem element_New_change;
    private JMenuItem element_New_encodingDesc;
    private JMenuItem element_New_fileDesc;
    private JMenuItem element_New_front;
    private JMenuItem element_New_item;
    private JMenuItem element_New_itemGrp;
    private JMenuItem element_New_itemSet;
    private JMenuItem element_New_map;
    private JMenuItem element_New_martif;
    private JMenuItem element_New_martifHeader;
    private JMenuItem element_New_p;
    private JMenuItem element_New_publicationStmt;
    private JMenuItem element_New_refObject;
    private JMenuItem element_New_refObjectList;
    private JMenuItem element_New_revisionDesc;
    private JMenuItem element_New_sourceDesc;
    private JMenuItem element_New_text;
    private JMenuItem element_New_title;
    private JMenuItem element_New_titleStmt;
    private JMenuItem element_New_ude;
    //CHECKSTYLE: JavadocVariable ON

    /**
     * @param w The window this menu bar is placed in.
     */
    public TBXMenuBar(TBXWindow w)
    {
        super(w, MARTIF);
        ewin = w;
    }

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
        ewin.getTBXEditor().open();
    }

    /**
     */
    public void handleMenuItem__File_Close()
    {
        ewin.close();
    }

    /**
     */
    public void handleMenuItem__File_Save()
    {
        ewin.getTBXEditor().save(ewin);
    }

    /**
     */
    public void handleMenuItem__File_SaveAs()
    {
        ewin.getTBXEditor().saveAs(ewin);
    }

    /**
     */
    public void handleMenuItem__File_SaveCopy()
    {
        ewin.getTBXEditor().saveCopy(ewin);
    }

    /**
     */
    public void handleMenuItem__File_Revert()
    {
        ewin.getTBXEditor().revert(ewin);
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
    public void handleMenuItem__File_Preferences()
    {
        org.ttt.salt.editor.preferences.Preferences.getInstance().showDialog();
    }

    /**
     */
    public void handleMenuItem__File_Quit()
    {
        org.ttt.salt.editor.Main.getInstance().signalQuit();
    }

    /**
     */
    public void handleMenuItem__Edit_CreateTermEntry()
    {
        //CHECKSTYLE: IllegalCatch OFF
        try
        {
            TBXDocument file = ewin.getTBXEditor().getTBXDocument();
            Element entry = file.createElement("termEntry");
            
            Element langSet = file.createElement("langSet");
            langSet.setAttribute("xml:lang", "en");
            entry.appendChild(langSet);

            Element ntig = file.createElement("ntig");
            langSet.appendChild(ntig);

            Element termGrp = file.createElement("termGrp");
            ntig.appendChild(termGrp);

            Element term = file.createElement("term");
            termGrp.appendChild(term);
            
            file.getBodyElement().appendChild(entry);
        }
        catch (Throwable err)
        {
            err.printStackTrace(System.err);
        }
        //CHECKSTYLE: IllegalCatch ON
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
        org.ttt.salt.editor.about.Dialog.display(ewin.getPrimaryComponent());
    }

    /**
     */
    public void handleMenuItem__Help_GPL()
    {
        System.err.println("NOT IMPLEMENTED Help_GPL");
    }
    
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
    protected void buildElementMenu()
    {
        super.buildElementMenu();
        
        element_New_termEntry = createMenuItem("Element_New_termEntry");
        element_New_back = createMenuItem("Element_New_back");
        element_New_body = createMenuItem("Element_New_body");
        element_New_change = createMenuItem("Element_New_change");
        element_New_encodingDesc = createMenuItem("Element_New_encodingDesc");
        element_New_fileDesc = createMenuItem("Element_New_fileDesc");
        element_New_front = createMenuItem("Element_New_front");
        element_New_item = createMenuItem("Element_New_item");
        element_New_itemGrp = createMenuItem("Element_New_itemGrp");
        element_New_itemSet = createMenuItem("Element_New_itemSet");
        element_New_map = createMenuItem("Element_New_map");
        element_New_martif = createMenuItem("Element_New_martif");
        element_New_martifHeader = createMenuItem("Element_New_martifHeader");
        element_New_p = createMenuItem("Element_New_p");
        element_New_publicationStmt = createMenuItem(
                "Element_New_publicationStmt");
        element_New_refObject = createMenuItem("Element_New_refObject");
        element_New_refObjectList = createMenuItem("Element_New_refObjectList");
        element_New_revisionDesc = createMenuItem("Element_New_revisionDesc");
        element_New_sourceDesc = createMenuItem("Element_New_sourceDesc");
        element_New_text = createMenuItem("Element_New_text");
        element_New_title = createMenuItem("Element_New_title");
        element_New_titleStmt = createMenuItem("Element_New_titleStmt");
        element_New_ude = createMenuItem("Element_New_ude");

        element.addSeparator();
        element.add(element_New_termEntry);
        element.addSeparator();
        element.add(element_New_back);
        element.add(element_New_body);
        element.add(element_New_change);
        element.add(element_New_encodingDesc);
        element.add(element_New_fileDesc);
        element.add(element_New_front);
        element.add(element_New_item);
        element.add(element_New_itemGrp);
        element.add(element_New_itemSet);
        element.add(element_New_map);
        element.add(element_New_martif);
        element.add(element_New_martifHeader);
        element.add(element_New_p);
        element.add(element_New_publicationStmt);
        element.add(element_New_refObject);
        element.add(element_New_refObjectList);
        element.add(element_New_revisionDesc);
        element.add(element_New_sourceDesc);
        element.add(element_New_text);
        element.add(element_New_title);
        element.add(element_New_titleStmt);
        element.add(element_New_ude);
    }

    /**
     */
    protected void updateMenuStatus()
    {
        super.updateMenuStatus();
        
        file_save.setEnabled(ewin.getTBXEditor().isDirty());
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
     * @return true => The term entry is selected in the tree.
     */
    private boolean isTermEntrySelected()
    {
        Object obj = ewin.getJTree().getLastSelectedPathComponent();
        return (obj != null)
                && (obj.toString().startsWith("termEntry"));
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
            if (name.equals("martifHeader"))
            {
                element_New_encodingDesc.setEnabled(isAddable(path, "encodingDesc"));
                element_New_revisionDesc.setEnabled(isAddable(path, "revisionDesc"));
            }
            else if (name.equals("fileDesc"))
            {
                element_New_titleStmt.setEnabled(isAddable(path, "titleStmt"));
                element_New_publicationStmt.setEnabled(isAddable(path,
                        "publicationStmt"));
                element_New_sourceDesc.setEnabled(isAddable(path, "sourceDesc"));
            }
            //else if (name.equals("titleStmt"))
            //{
            //    element_New_note.setEnabled(isAddable(path, "note"));
            //}
            else if (name.equals("publicationStmt"))
            {
                element_New_p.setEnabled(isAddable(path, "p"));
            }
            else if (name.equals("sourceDesc"))
            {
                element_New_p.setEnabled(isAddable(path, "p"));
            }
            else if (name.equals("encodingDesc"))
            {
                element_New_ude.setEnabled(isAddable(path, "ude"));
                element_New_p.setEnabled(isAddable(path, "p"));
            }
            else if (name.equals("ude"))
            {
                element_New_map.setEnabled(isAddable(path, "map"));
            }
            else if (name.equals("revisionDesc"))
            {
                element_New_change.setEnabled(isAddable(path, "change"));
            }
            else if (name.equals("change"))
            {
                element_New_p.setEnabled(isAddable(path, "p"));
            }
            else if (name.equals("text"))
            {
                element_New_front.setEnabled(isAddable(path, "front"));
                element_New_back.setEnabled(isAddable(path, "back"));
            }
            else if (name.equals("body"))
            {
                element_New_termEntry.setEnabled(isAddable(path, "termEntry"));
            }
            else if (name.equals("back"))
            {
                element_New_refObjectList.setEnabled(isAddable(path, "refObjectList"));
            }
            else if (name.equals("refObjectList"))
            {
                element_New_refObject.setEnabled(isAddable(path, "refObject"));
            }
            else if (name.equals("refObject"))
            {
                element_New_itemSet.setEnabled(isAddable(path, "itemSet"));
                element_New_itemGrp.setEnabled(isAddable(path, "itemGrp"));
                element_New_item.setEnabled(isAddable(path, "item"));
            }
            else if (name.equals("itemSet"))
            {
                element_New_itemGrp.setEnabled(isAddable(path, "itemGrp"));
                element_New_item.setEnabled(isAddable(path, "item"));
            }
            element_Delete.setEnabled(isDeleteable(path));
        }
    }
}

