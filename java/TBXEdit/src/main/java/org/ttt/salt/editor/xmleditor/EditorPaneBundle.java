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
package org.ttt.salt.editor.xmleditor;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class EditorPaneBundle extends ListResourceBundle
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private static Object[][] contents =
    {
        {"SplitVertIcon",    new ImageIcon("images/SplitVertIcon.gif")},
        {"SplitVertToolTip",    "Split editor vertically."},
        {"SplitHorzIcon",    new ImageIcon("images/SplitHorzIcon.gif")},
        {"SplitHorzToolTip",    "Split editor horizontally."},
        {"SplitCloseIcon",    new ImageIcon("images/SplitCloseIcon.gif")},
        {"SplitCloseToolTip",    "Close the split."},
        {"LineLabel",        "Line:"},
        {"LineToolTip",        "Enter line to goto."},
        {"ColumnLabel",        "Col:"},
        {"ColumnToolTip",    "Enter column to goto."},
    };

    /**
     */
    public EditorPaneBundle()
    {
        super();
    }
    
    /**
     */
    protected Object[][] getContents()
    {
        return contents;
    }
}

