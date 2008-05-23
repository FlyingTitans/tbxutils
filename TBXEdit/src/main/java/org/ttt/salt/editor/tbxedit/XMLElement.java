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

import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;
import org.ttt.salt.editor.TBXEditor;
import org.ttt.salt.editor.TBXDocument;
import org.flyingtitans.xml.TreeView;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class XMLElement extends org.flyingtitans.xml.TreeViewElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
        
    /** */
    protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org.ttt.salt.editor.tbxedit.XMLElement", Locale.getDefault());
    
    /** */
    private final TBXWindow window;
    
    /** */
    private final TBXEditor editor;
    
    /** */
    private final TBXDocument file;
        
    /**
     * This will do the standard init of necessary for all element
     * handlers.
     * <p>
     * <code>setLayout</code> must be called by sub-classes to set the
     * layout for the panel.</p>
     *
     * @param elem The backing element that contains the data to be displayed
     *  and edited.
     * @param win The view that will display the element data.
     * @param title The title to be displayed for the element.
     * @param buffered Flag to turn off-screen drawing on or off (see
     *  {@link javax.swing.JPanel#JPanel(boolean)}.
     */
    protected XMLElement(Element elem, TreeView win, String title,
            boolean buffered)
    {
        super(elem, win, title, buffered);
        window = (TBXWindow) win;
        editor = window.getTBXEditor();
        file = editor.getTBXDocument();
    }
    
    /**
     * Get the TBXDocument that holds the element this is displaying.
     *
     * @return TBXDocument this element is in.
     */
    public TBXDocument getTBXDocument()
    {
        return file;
    }
    
//CHECKSTYLE: MethodName OFF
    /**
     * Validator and save method for attribute id.
     *
     * @param evt The {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void validateAndSaveAttribute_id(ActionEvent evt)
    {
        String tagname = evt.getActionCommand();
        JTextField text = (JTextField) attributes.get(tagname);
        String newval = text.getText();
        String oldval = elem.getAttribute("id");
        if (!newval.equals(oldval))
        {
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
     * Validator and save method for attribute type.
     *
     * @param evt The {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void validateAndSaveAttribute_type(ActionEvent evt)
    {
        JTextField text = (JTextField) attributes.get("type");
        String value = text.getText();
        elem.setAttribute("type", value);
    }
//CHECKSTYLE: MethodName ON
}

