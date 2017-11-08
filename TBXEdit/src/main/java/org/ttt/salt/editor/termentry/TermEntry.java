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

import java.util.Collection;
import java.util.SortedSet;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.ComboBoxModel;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.ttt.salt.XCSDocument;
import org.ttt.salt.editor.TBXEditor;
import org.ttt.salt.editor.TBXDocument;
import org.ttt.salt.editor.tbxedit.TBXWindow;
import org.flyingtitans.swing.MapComboBoxModel;
import org.flyingtitans.swing.SetComboBoxModel;
import org.flyingtitans.xml.ObservableElement;
import org.flyingtitans.xml.TreeView;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class TermEntry extends TreeView
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private static final Map<String, String> TAGID
            = new java.util.HashMap<String, String>();
    
    /** */
    private static final String[] NOEXPAND =
    {
        "admin", "adminNote",
        "date",
        "descrip", "descripNote",
        "note",
        "term", "termComp", "termNote",
        "transac", "transacNote",
        "ref", "xref"
    };
    
    /** */
    private static final Map<String, String[]> INSERT_MAP
            = new java.util.HashMap<String, String[]>();
    
    /**
     */
    static
    {
        TAGID.put("admin", "id");
        TAGID.put("descrip", "type");
        TAGID.put("term", "id");
        TAGID.put("langSet", "xml:lang");
        TAGID.put("tig", "id");
        TAGID.put("ntig", "id");
        
        /*
         * Build the insert position table.
         * The commented empty inserts are for consistency with DTD
         */
        String[] empty = {};
        String[] auxInfo = {"descrip", "descripGrp", "admin", "adminGrp",
                "transac", "transacGrp", "note", "ref", "xref"};
        String[] noteInfo = {"admin", "adminGrp", "transac", "transacGrp",
                "note", "ref", "xref"};
        String[] tigntig = {"tig", "ntig"};
        String[] langset = {"langSet"};
        String[] compgrp = {"termComp", "termCompGrp"};
        String[] notegrp = {"termNote", "termNoteGrp"};
        
        putAuxInfo(INSERT_MAP, "langSet", tigntig);
        //INSERT_MAP.put(makeInsertKey("langSet", "tig"), empty);
        //INSERT_MAP.put(makeInsertKey("langSet", "ntig"), empty);
        
        INSERT_MAP.put(makeInsertKey("termCompGrp", "termNote"), noteInfo);
        INSERT_MAP.put(makeInsertKey("termCompGrp", "termNoteGrp"), noteInfo);
        //putNoteLinkInfo(INSERT_MAP, "termCompGrp", empty);
        
        putAuxInfo(INSERT_MAP, "termCompList", compgrp);
        //INSERT_MAP.put(makeInsertKey("termCompList", "termComp"), empty);
        //INSERT_MAP.put(makeInsertKey("termCompList", "termCompGrp"), empty);
       
        putAuxInfo(INSERT_MAP, "termEntry", langset);
        //INSERT_MAP.put(makeInsertKey("termEntry", "langSet"), empty);

        INSERT_MAP.put(makeInsertKey("termGrp", "termNote"), notegrp);
        INSERT_MAP.put(makeInsertKey("termGrp", "termNoteGrp"), notegrp);
        //INSERT_MAP.put(makeInsertKey("termGrp", "termCompList"), empty);

        INSERT_MAP.put(makeInsertKey("tig", "termNote"), auxInfo);
        //putAuxInfo(INSERT_MAP, "tig", empty);
    }
    
    /**
     * @param ptag The parent element tag.
     * @param ctag The child element tag.
     * @return The created key string.
     */
    private static String makeInsertKey(String ptag, String ctag)
    {
        return ptag + "_" + ctag;
    }
    
    /**
     * @param map The map to store the link info into.
     * @param ptag The parent element tag.
     * @param group The list of linked strings.
     */
    private static void putAuxInfo(Map<String, String[]> map, String ptag,
            String[] group)
    {
        map.put(makeInsertKey(ptag, "descrip"), group);
        map.put(makeInsertKey(ptag, "descripGrp"), group);
        map.put(makeInsertKey(ptag, "admin"), group);
        map.put(makeInsertKey(ptag, "adminGrp"), group);
        map.put(makeInsertKey(ptag, "transac"), group);
        map.put(makeInsertKey(ptag, "transacGrp"), group);
        map.put(makeInsertKey(ptag, "note"), group);
        map.put(makeInsertKey(ptag, "ref"), group);
        map.put(makeInsertKey(ptag, "xref"), group);
    }
    
    /**
     * @param map The map to store the link info into.
     * @param ptag The parent element tag.
     * @param group The list of linked strings.
     */
    private static void putNoteLinkInfo(Map<String, String[]> map, String ptag,
            String[] group)
    {
        map.put(makeInsertKey(ptag, "admin"), group);
        map.put(makeInsertKey(ptag, "adminGrp"), group);
        map.put(makeInsertKey(ptag, "transac"), group);
        map.put(makeInsertKey(ptag, "transacGrp"), group);
        map.put(makeInsertKey(ptag, "note"), group);
        map.put(makeInsertKey(ptag, "ref"), group);
        map.put(makeInsertKey(ptag, "xref"), group);
    }
    
    /** */
    private final TBXWindow window;
    
    /** */
    private final TBXEditor editor;
    
    /** */
    private final TBXDocument file;
    
    /** */
    private final XCSDocument xcsdoc;
    
    /** */
    private ComboBoxModel languages;
        
    /**
     * @param elem The TBX term entry element that will back this displayable
     *  term entry.
     * @param win The window this term entry is displayed in.
     */
    public TermEntry(ObservableElement elem, TBXWindow win)
    {
        super(elem, TAGID, NOEXPAND);
        setTitle(elem.getAttribute("id"));
        MenuBar mbar = new MenuBar(this, win);
        setJMenuBar(mbar.getJMenuBar());
        window = win;
        editor = win.getTBXEditor();
        file = editor.getTBXDocument();
        xcsdoc = file.getTBXFile().getXCSDocument();
        SortedMap<String, String> langs = xcsdoc.getLanguages();
        languages = new MapComboBoxModel<String, String>(langs);
        addWindowListener(win);
        setVisible(true);
    }
    
    /**
     * @return The XCS document that will validate this term entry.
     */
    public XCSDocument getXCSDocument()
    {
        return xcsdoc;
    }
    
    /**
     * Get the acceptable languages as defined in the XCS document.
     *
     * @return The combo box that contains the languages.
     */
    public ComboBoxModel getLanguages()
    {
        return languages;
    }
    
    /**
     * Get the acceptable types for this element.
     *
     * @param tag The tag to search for.
     * @param parenttag The tag to search in.
     * @return The combo box that contains the attribute types.
     */
    public ComboBoxModel getAttributeTypes(String tag, String parenttag)
    {
        return new SetComboBoxModel<String>(xcsdoc.getSpecTypes(tag, parenttag));
    }
    
    /** {@inheritDoc} */
    public void setTitle(String id)
    {
        super.setTitle("termEntry (" + id + ")");
    }
        
    /**
     * @return The TBX window that is displaying this term entry.
     */
    TBXWindow getTBXWindow()
    {
        return window;
    }
    
    /**
     * @return The TBX document that contains this term entry.
     */
    TBXDocument getTBXDocument()
    {
        return file;
    }
    
    /** {@inheritDoc} */
    protected Element createElement(String tag, Element parent)
    {
        Document doc = window.getTBXEditor().getTBXDocument();
        Element elem = doc.createElement(tag);
        if (tag.equals("adminGrp"))
        {
            elem.appendChild(createElement("admin", elem));
        }
        else if (tag.equals("descripGrp"))
        {
            elem.appendChild(createElement("descrip", elem));
        }
        else if (tag.equals("langSet"))
        {
            elem.appendChild(createElement("ntig", elem));
            SortedMap lang = getXCSDocument().getLanguages();
            elem.setAttribute("lang", (String) lang.firstKey());
        }
        else if (tag.equals("note"))
        {   //no op
        }
        else if (tag.equals("ntig"))
        {
            elem.appendChild(createElement("termGrp", elem));
        }
        else if (tag.equals("term"))
        {   //no op
        }
        else if (tag.equals("termCompGrp"))
        {
            elem.appendChild(createElement("termComp", elem));
        }
        else if (tag.equals("termCompList"))
        {
            elem.appendChild(createElement("termComp", elem));
            SortedSet spec = getXCSDocument().getSpecTypes(elem.getTagName(),
                parent.getTagName());
            elem.setAttribute("type", (String) spec.first());
        }
        else if (tag.equals("termGrp"))
        {
            elem.appendChild(createElement("term", elem));
        }
        else if (tag.equals("termNoteGrp"))
        {
            elem.appendChild(createElement("termNote", elem));
        }
        else if (tag.equals("tig"))
        {
            elem.appendChild(createElement("term", elem));
        }
        else if (tag.equals("transacGrp"))
        {
            elem.appendChild(createElement("transac", elem));
        }
        else
        {
            SortedSet spec = getXCSDocument().getSpecTypes(elem.getTagName(),
                parent.getTagName());
            elem.setAttribute("type", (String) spec.first());
        }
        return elem;
    }
    
    /** {@inheritDoc} */
    protected Node findInsertionPoint(Element parent, Element elem)
    {
        Node ret = null;
        String ptag = parent.getTagName();
        String tag = elem.getTagName();
        String[] tags = INSERT_MAP.get(makeInsertKey(ptag, tag));
        if (tags != null)
            ret = findFirstInSet(parent, java.util.Arrays.asList(tags));
        return ret;
    }
    
    /**
     * @param parent The parent element in which to search for the tags.
     * @param tags The tags to search for.
     * @return The first element found in parent.
     */
    private Element findFirstInSet(Element parent, Collection tags)
    {
        Element ret = null;
        Node node = parent.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element elem = (Element) node;
                if (tags.contains(elem.getTagName()))
                {
                    ret = elem;
                    break;
                }
            }
            node = node.getNextSibling();
        }
        return ret;
    }
}

