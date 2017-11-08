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

import java.io.*;
import java.util.*;
import javax.swing.ListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * This will display each XML validation error in order of importance.
 * <p>
 * When an error is selected then the area in the XML text that has the
 * problem will be selected.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class XMLErrorPane extends JScrollPane
{
    /*
     */
     
    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private final XMLEditPane editor;
    
    /** */
    private final ListModel errors;
    
    /**
     * @param e The XML editing pane to display the error into.
     * @param err The list of errors to be displayed.
     */
    public XMLErrorPane(XMLEditPane e, ListModel err)
    {
        super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        editor = e;
        errors = err;
        getViewport().setView(new JList(errors));
    }
}

