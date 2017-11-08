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

import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.w3c.dom.Element;
import org.ttt.salt.editor.termentry.TermEntry;
import org.flyingtitans.xml.ObservableElement;
import org.flyingtitans.xml.TreeView;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Element_termEntry extends XMLElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /**
     */
    private static Map<Element, TermEntry> elemToWin  = new java.util.HashMap<Element, TermEntry>();
    
    /** {@inheritDoc} */
    private static WindowListener telisten = new WindowListener()
    {
        public void windowOpened(WindowEvent evt)
        {
        }
    
        public void windowClosed(WindowEvent evt)
        {
        }
    
        public void windowClosing(WindowEvent evt)
        {
            if (evt.getWindow() instanceof TermEntry)
            {
                TermEntry te = (TermEntry) evt.getWindow();
                Element elem = te.getBaseElement();
                elemToWin.remove(elem);
            }
        }
    
        public void windowActivated(WindowEvent evt)
        {
        }
    
        public void windowDeactivated(WindowEvent evt)
        {
        }
    
        public void windowDeiconified(WindowEvent evt)
        {
        }
    
        public void windowIconified(WindowEvent evt)
        {
        }
    };
    
    /**
     */
    private TermEntry tewin;
    
    /**
     * @param elem The DOM element that has the data.
     * @param window The view this element displays in.
     * @param title The title to use in the view.
     */
    public Element_termEntry(Element elem, TreeView window, String title)
    {
        super(elem, window, title, false);
        addAttributeTextField(BUNDLE, "id");
        tewin = elemToWin.get(elem);
        if (tewin == null)
        {
            tewin = new TermEntry((ObservableElement) elem, (TBXWindow) window);
            tewin.addWindowListener(telisten);
            elemToWin.put(elem, tewin);
        }
        tewin.setVisible(true);
        tewin.toFront();
    }
    
    /** {@inheritDoc} */
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getActionCommand().equals("id"))
        {
            JTextField id = (JTextField) attributes.get("id");
            String val = id.getText();
            if (!val.equals(elem.getAttribute("id")))
            {
                int ans = JOptionPane.YES_OPTION;
                if (getTBXDocument().getTBXFile().getTermEntryMap().containsKey(val))
                {
                    ans = JOptionPane.showConfirmDialog(this,
                            BUNDLE.getString("termEntry_dup_msg"), "",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                }
                if (ans == JOptionPane.YES_OPTION)
                {
                    elem.setAttribute("id", val);
                    tewin.setTitle(val);
                }
                else if (ans == JOptionPane.NO_OPTION)
                {
                    id.setText("");
                }
            }
        }
    }
}
