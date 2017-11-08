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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.ttt.salt.XCSDocument;
import org.flyingtitans.xml.TreeView;
import org.flyingtitans.swing.SetListModel;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Element_transac extends TermEntryElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /**
     * @param elem Element to represent.
     * @param window Window to display the element.
     * @param title Name to display in window.
     */
    public Element_transac(Element elem, TreeView window, String title)
    {
        super(elem, window, title, false);
        addTypeAttribute("type", elem);
        addXmlLangAttribute();
        addAttributeTextField(BUNDLE, "target");
        addAttributeTextField(BUNDLE, "datatype");
        setSpecContentEditor(elem);
    }
    
    /**
     * This is used instead of the superclass version since the "type"
     * attribute has become #IMPLIED instead of #REQUIRED. When the SALT
     * committee changes its mind later then just delete this method.
     *
     * {@inheritDoc}
     * @see TermEntryElement#buildPickListSelector
     */
    protected void addTypeAttribute(String name, Element elem)
    {
    }
    
    /**
     * This is used instead of the superclass version since the "type"
     * attribute has become #IMPLIED instead of #REQUIRED. When the SALT
     * committee changes its mind later then just delete this method.
     *
     * @param elem The element to create a selector on.
     * @see TermEntryElement#buildPickListSelector
     */
    protected void buildPickListSelector(Element elem)
    {
        //XCSDocument.Key key = new XCSDocument.Key(elem.getTagName(),
        //        elem.getAttribute("type"));
        XCSDocument.Key key = new XCSDocument.Key("transac", "transactionType");
        
        String datatype = termEntry().getXCSDocument().getDataType(key);
        if (!datatype.equals("picklist"))
            throw new IllegalArgumentException("Element not picklist");
        contentPanel.add(new JLabel(BUNDLE.getString("content_pickList")));
        Set<String> picklist = termEntry().getXCSDocument().getPicklist(key);
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
}
