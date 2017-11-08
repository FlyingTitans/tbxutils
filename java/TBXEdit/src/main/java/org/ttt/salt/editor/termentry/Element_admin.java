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

import org.w3c.dom.Element;
import org.flyingtitans.xml.TreeView;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class Element_admin extends TermEntryElement
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
    public Element_admin(Element elem, TreeView window, String title)
    {
        super(elem, window, title, false);
        addAttributeTextField(BUNDLE, "id");
        addXmlLangAttribute();
        addTypeAttribute("type", elem);
        addAttributeTextField(BUNDLE, "target");
        addAttributeTextField(BUNDLE, "datatype");
        setSpecContentEditor(elem);
    }
}
