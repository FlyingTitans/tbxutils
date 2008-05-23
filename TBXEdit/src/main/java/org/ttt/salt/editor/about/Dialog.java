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
package org.ttt.salt.editor.about;

import java.text.MessageFormat;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.flyingtitans.util.RcsId;

/**
 * Editor to open a single file for editing in a single pane.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public final class Dialog
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /**
     * @param primary The window this about dialog should use as its root.
     */
    public static void display(JComponent primary)
    {
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.editor.about.Dialog");
        RcsId rcs = new RcsId(org.ttt.salt.editor.Main.RCSID);
        Object[] parms = {rcs.getVersion(), rcs.getState(), rcs.getDate()};
        String msg = bundle.getString("Message");
        String title = bundle.getString("Title");
        JOptionPane.showMessageDialog(primary, MessageFormat.format(msg, parms), title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     */
    private Dialog()
    {
    }
}

