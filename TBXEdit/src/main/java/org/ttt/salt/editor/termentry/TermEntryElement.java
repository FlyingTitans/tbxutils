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

import java.util.Set;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.ttt.salt.XCSDocument;
import org.ttt.salt.editor.TBXDocument;
import org.flyingtitans.swing.SetListModel;
import org.flyingtitans.xml.TextEditPane;
import org.flyingtitans.xml.TreeView;
import org.flyingtitans.xml.TreeViewElement;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class TermEntryElement extends TreeViewElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** The class prefix for element editors. */
    public static final String PREFIX = "org.ttt.salt.editor.termentry.Element_";
    
    /** The locale based localization bundle for term entry elements. */
    protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org.ttt.salt.editor.termentry.TermEntryElement",
            Locale.getDefault());
    
    /** The term entry. */
    private final TermEntry tewin;
        
    /**
     * This will do the standard init of necessary for all element
     * handlers.
     * <p>
     * <code>setLayout</code> must be called by sub-classes to set the
     * layout for the panel.</p>
     *
     * @param elem The DOM element that this object modifies.
     * @param win The TermEntry window this will be displayed in.
     * @param title The title for this panel.
     * @param buffered Will the panel be double buffered.
     */
    protected TermEntryElement(Element elem, TreeView win, String title,
            boolean buffered)
    {
        super(elem, win, title, buffered);
        this.tewin = (TermEntry) win;
    }
    
    /**
     * Get the term entry.
     *
     * @return The term entry object.
     */
    protected TermEntry termEntry()
    {
        return tewin;
    }

//CHECKSTYLE: MethodName OFF

    /**
     * Validator and save method for attributes with "id" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that caused this action.
     */
    public void validateAndSaveAttribute_id(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JTextField text = (JTextField) attributes.get(tagname);
        String newval = text.getText();
        String oldval = elem.getAttribute("id");
        if (!newval.equals(oldval))
        {
            TBXDocument file = tewin.getTBXDocument();
            Element dup = file.getElementById(newval);
            if (dup == null)
            {
                elem.setAttribute("id", newval);
            }
            else
            {
                JOptionPane.showMessageDialog(this,
                        BUNDLE.getString("element_id_dup_msg"), "",
                        JOptionPane.ERROR_MESSAGE);
                text.setText(oldval);
            }
            textChanges.remove(text.getDocument());
        }
    }
    
    /**
     * Validator and save method for attributes with "xml:lang" or
     * "lang" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that caused this action.
     */
    public void validateAndSaveAttribute_lang(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JComboBox box = (JComboBox) attributes.get(tagname);
        String value = box.getSelectedItem().toString();
        String deflang = getDefaultLang();
        if (value.equals(deflang))
            elem.removeAttribute("xml:lang");
        else if (!value.equals(elem.getAttribute("xml:lang")))
            elem.setAttribute("xml:lang", value);
        if (elem.hasAttribute("lang"))
            elem.removeAttribute("lang");
    }
    
    /**
     * Validator and save method for attributes with "type" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that caused this action.
     */
    public void validateAndSaveAttribute_type(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JComboBox box = (JComboBox) attributes.get(tagname);
        String value = box.getSelectedItem().toString();
        if (!value.equals(elem.getAttribute("type")))
        {
            elem.setAttribute("type", value);
            setSpecContentEditor(elem);
        }
    }
    
    /**
     * Validator and save method for attributes with "target" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that caused this action.
     */
    public void validateAndSaveAttribute_target(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JTextField text = (JTextField) attributes.get(tagname);
        String value = text.getText();
        if (!value.equals(elem.getAttribute("target")))
        {
            elem.setAttribute("target", value);
            textChanges.remove(text.getDocument());
        }
    }
    
    /**
     * Validator and save method for attributes with "datatype" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that caused this action.
     */
    public void validateAndSaveAttribute_datatype(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JTextField text = (JTextField) attributes.get(tagname);
        String value = text.getText();
        if (!value.equals(elem.getAttribute("datatype")))
        {
            elem.setAttribute("datatype", value);
            textChanges.remove(text.getDocument());
        }
    }

//CHECKSTYLE: MethodName ON
    
    /**
     * Add a combo box attribute selector for attribute that holds a
     * lang value to the upper attribute panel.
     */
    protected void addLanguageAttribute()
    {
        String lang = elem.getAttribute("lang");
        String tag = getClass().getName().substring(
                getClass().getName().lastIndexOf('_') + 1) + "_lang";
        JLabel label = new JLabel(BUNDLE.getString(tag));
        JComboBox combo = new JComboBox(tewin.getLanguages());
        combo.setSelectedItem(lang);
        combo.setToolTipText(BUNDLE.getString(tag + "_tip"));
        combo.setActionCommand("lang");
        combo.addActionListener(this);
        addAttribute("lang", label, combo);
    }
    
    /**
     * Add a type attribute selector for attribute that holds a type value
     * to the upper attribute panel.
     *
     * @param name The name of the type attribute.
     * @param elem Element to add type attribute to.
     */
    protected void addTypeAttribute(String name, Element elem)
    {
        Element parent = (Element) elem.getParentNode();
        String tag = getClass().getName().substring(
                getClass().getName().lastIndexOf('_') + 1) + "_" + name;
        JLabel label = new JLabel(BUNDLE.getString(tag));
        JComboBox combo = new JComboBox(
                tewin.getAttributeTypes(elem.getTagName(),
                                        parent.getTagName()));
        combo.setSelectedItem(elem.getAttribute(name));
        combo.setToolTipText(BUNDLE.getString(tag + "_tip"));
        combo.setActionCommand(name);
        combo.addActionListener(this);
        addAttribute(name, label, combo);
    }
    
    /**
     * This will remove any JComponents from the lower contents panel
     * and replace it with the appropriate editor for the tag and type
     * combination.
     * <p>
     * This should only be used by elements that have a tagNameSpec
     * entry in the XCSDocument.</p>
     *
     * @param elem Set the specification content editor for this element.
     */
    protected void setSpecContentEditor(Element elem)
    {
        if (!elem.hasAttribute("type"))
            throw new IllegalArgumentException("XML tag does not have spec");
        XCSDocument.Key key = new XCSDocument.Key(elem.getTagName(),
                elem.getAttribute("type"));
        String datatype = tewin.getXCSDocument().getDataType(key);
        contentPanel.removeAll();
        if (datatype.equals("basicText"))
            buildBasicTextEditor(elem);
        else if (datatype.equals("noteText"))
            buildNoteTextEditor(elem);
        else if (datatype.equals("plainText"))
            buildPlainTextEditor(elem);
        else if (datatype.equals("picklist"))
            buildPickListSelector(elem);
        else
            throw new IllegalArgumentException(
                    "Unknown data type for spec tag");
    }
    
    /**
     * Build a %basicText; xml text editor and place it into the lower tag
     * contents pane.
     * <p>
     * This editor will allow all tags that the basicText entity will allow,
     * and will be configured to edit those tags easily.</p>
     *
     * @param elem Build a new basic text editor for this element.
     */
    protected void buildBasicTextEditor(Element elem)
    {
        contentPanel.add(new JLabel(BUNDLE.getString("content_basicText")));
        TextEditPane pane = new TextEditPane(elem, true);
        JTextComponent text = pane.getTextComponent();
        text.getDocument().addDocumentListener(this);
        UndoManager undo = pane.getUndoManager();
        contentPanel.add(pane);
        undos.put(text, undo);
        docToController.put(text.getDocument(), pane);
    }
    
    /**
     * Build a %noteText; xml text editor and place it into the lower tag
     * contents pane.
     * <p>
     * This editor will allow all tags that the noteText entity will allow,
     * and will be configured to edit those tags easily.</p>
     *
     * @param elem Build a new note editor for this element.
     */
    protected void buildNoteTextEditor(Element elem)
    {
        contentPanel.add(new JLabel(BUNDLE.getString("content_noteText")));
        TextEditPane pane = new TextEditPane(elem, true);
        JTextComponent text = pane.getTextComponent();
        text.getDocument().addDocumentListener(this);
        UndoManager undo = pane.getUndoManager();
        contentPanel.add(pane);
        undos.put(text, undo);
        docToController.put(text.getDocument(), pane);
    }
    
    /**
     * Build a %noteText; xml text editor and place it into the lower tag
     * contents pane.
     * <p>
     * This editor will allow all tags that the plainText entity will allow,
     * and will be configured to edit those tags easily.</p>
     *
     * @param elem Build a text editor for this element.
     */
    protected void buildPlainTextEditor(Element elem)
    {
        contentPanel.add(new JLabel(BUNDLE.getString("content_plainText")));
        TextEditPane pane = new TextEditPane(elem, true);
        JTextComponent text = pane.getTextComponent();
        text.getDocument().addDocumentListener(this);
        UndoManager undo = pane.getUndoManager();
        contentPanel.add(pane);
        undos.put(text, undo);
        docToController.put(text.getDocument(), pane);
    }
    
    /**
     * Build a pick list and place it into the lower tag contents pane.
     * <p>
     * This will make a JList that has all the enumeration for the particular
     * tags type.</p>
     *
     * @param elem Build a new pick list selector for this element.
     */
    protected void buildPickListSelector(Element elem)
    {
        XCSDocument.Key key = new XCSDocument.Key(elem.getTagName(),
                elem.getAttribute("type"));
        String datatype = tewin.getXCSDocument().getDataType(key);
        if (!datatype.equals("picklist"))
            throw new IllegalArgumentException("Element not picklist");
        contentPanel.add(new JLabel(BUNDLE.getString("content_pickList")));
        Set<String> picklist = tewin.getXCSDocument().getPicklist(key);
        JList list = new JList(new SetListModel<String>(picklist));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Node node = elem.getFirstChild();
    NODE_ITERATOR:
        while (node != null)
        {
            if (node.getNodeType() == Node.TEXT_NODE)
            {
                java.util.StringTokenizer tok =
                    new java.util.StringTokenizer(node.getNodeValue());
                while (tok.hasMoreElements())
                {
                    list.setSelectedValue(tok.nextToken(), true);
                    break NODE_ITERATOR; //remove this on multi selection
                }
            }
            node = node.getNextSibling();
        }
        list.addListSelectionListener(this);
        JScrollPane scroll = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scroll);
    }
    
    /**
     * Get the default term entry language.
     *
     * @return The default lanaguage for the term entry.
     */
    protected String getDefaultLang()
    {
        Element elem = tewin.getTBXDocument().getDocumentElement();
        return elem.getAttribute("xml:lang");
    }
    
    /**
     * Get the undo manager for term entry elements.
     *
     * @param text The swing component the manager is associated with.
     * @return The undo manager for the text component and term entry.
     */
    protected UndoManager getUndoManager(JTextComponent text)
    {
        return (UndoManager) undos.get(text);
    }
}

