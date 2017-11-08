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

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import org.w3c.dom.Element;
import org.flyingtitans.xml.TreeView;
import org.flyingtitans.xml.TextEditPane;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Element_title extends XMLElement
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
    public Element_title(Element elem, TreeView window, String title)
    {
        super(elem, window, title, false);
        addAttributeTextField(BUNDLE, "id");
        addXmlLangAttribute();
        buildTextEditor();
    }
    
    /**
     * Build a PCDATA xml text editor and place it into the lower tag
     * contents pane.
     * <p>
     * This editor will not allow any tags to be inserted--only #PCDATA
     * is allowed.</p>
     */
    protected void buildTextEditor()
    {
        contentPanel.add(new JLabel(BUNDLE.getString("title_text")));
        TextEditPane pane = new TextEditPane(elem, true);
        JTextComponent text = pane.getTextComponent();
        text.getDocument().addDocumentListener(this);
        UndoManager undo = pane.getUndoManager();
        contentPanel.add(pane);
        undos.put(text, undo);
        docToController.put(text.getDocument(), pane);
    }
}
