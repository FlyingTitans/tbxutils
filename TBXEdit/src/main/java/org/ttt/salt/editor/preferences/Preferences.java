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
package org.ttt.salt.editor.preferences;

import java.awt.Font;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.util.*;
import javax.swing.UIManager;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
public final class Preferences
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** The single instance of this dialog. */
    private static Preferences instance = new Preferences();

    /**
     * @return Singleton instance of this class.
     */
    public static Preferences getInstance()
    {
        return instance;
    }

    /**
     */
    private Preferences()
    {
    }

    /**
     */
    public void showDialog()
    {
        Dialog.getInstance().setVisible(true);
    }

    /**
     * @return The font to be used in the text editor.
     */
    public Font getEditorFont()
    {
        //CHECKSTYLE: MagicNumber OFF
        Font ret = new Font("Monospaced", Font.PLAIN, 10);
        String osname = System.getProperty("os.name");
        if (osname.equals("Windoze"))
        {
            ret = new Font("Courier New", Font.PLAIN, 12);
        }
        return ret;
        //CHECKSTYLE: MagicNumber ON
    }

    /**
     * @return {@link java.awt.print.PageFormat} stored in the preferences.
     */
    public PageFormat getPageFormat()
    {
        return new PageFormat();
    }

    /**
     */
    public void setDefaults()
    {
        //CHECKSTYLE: MagicNumber OFF
        //EditorPane
        UIManager.put("EditorPane.font", getEditorFont());
        UIManager.put("EditorPane.margin", new Insets(5, 5, 5, 10));
        //"EditorPane.border"
        //"EditorPane.foreground"
        //"EditorPane.background"
        //"EditorPane.selectionForeground"
        //"EditorPane.selectionBackground"
        //"EditorPane.inactiveForeground"
        //"EditorPane.caretBlinkRate"
        //"EditorPane.caretForeground"
        //UIManager.put("EditorPane.keyBindings",
        //        Text_EditorKit.getEditorKeyBinding());

        //TextArea

        //TextField

        //TextPanel

        //Label
        //UIManager.put("Label.font", new Font("System", Font.PLAIN, 12));
        //"Label.foreground"
        //"Label.background"
        //CHECKSTYLE: MagicNumber ON
    }

    /**
     * @return The {@link java.util.ResourceBundle} to be used for localization.
     */
    private static java.util.ResourceBundle getBundle()
    {
        return java.util.ResourceBundle.getBundle(
            "org.ttt.salt.editor.preferences.Preferences");
    }
}

