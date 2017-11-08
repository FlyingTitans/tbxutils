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

import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import org.w3c.dom.Element;
import org.flyingtitans.xml.TreeView;
import org.flyingtitans.xml.TextEditPane;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Element_p extends XMLElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
        
    /**
     * @param elem The DOM element that has the data.
     * @param window The view this element displays in.
     * @param title The title to use in the view.
     */
    public Element_p(Element elem, TreeView window, String title)
    {
        super(elem, window, title, false);
        addAttributeTextField(BUNDLE, "id");
        addTypeAttribute();
        addXmlLangAttribute();
        buildTextEditor();
    }
    
    /**
     * Validator and save method for attributes with "type" name.
     *
     * @param evt {@link java.awt.event.ActionEvent} that generates this event.
     */
    public void validateAndSaveAttribute_type(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JComboBox box = (JComboBox) attributes.get(tagname);
        String value = box.getSelectedItem().toString();
        if (!value.equals(elem.getAttribute("type")))
            elem.setAttribute("type", value);
    }
    
    /**
     * Add a type attribute selector for attribute that holds a type value
     * to the upper attribute panel.
     */
    protected void addTypeAttribute()
    {
        Element parent = (Element) elem.getParentNode();
        JLabel label = new JLabel(BUNDLE.getString("p_type"));
        String[] values = {"langDeclaration", "DCSName"};
        JComboBox combo = new JComboBox(values);
        combo.setSelectedItem(elem.getAttribute("type"));
        combo.setToolTipText(BUNDLE.getString("p_type_tip"));
        combo.setActionCommand("type");
        combo.addActionListener(this);
        addAttribute("type", label, combo);
    }
    
    /**
     * Build a %noteText; xml text editor and place it into the lower tag
     * contents pane.
     * <p>
     * This editor will allow all tags that the noteText entity will allow,
     * and will be configured to edit those tags easily.</p>
     */
    protected void buildTextEditor()
    {
        contentPanel.add(new JLabel(BUNDLE.getString("p_text")));
        TextEditPane pane = new TextEditPane(elem, true);
        JTextComponent text = pane.getTextComponent();
        text.getDocument().addDocumentListener(this);
        UndoManager undo = pane.getUndoManager();
        contentPanel.add(pane);
        undos.put(text, undo);
        docToController.put(text.getDocument(), pane);
    }
}
