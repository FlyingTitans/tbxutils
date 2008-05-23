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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
class EditPanel extends JPanel implements ActionListener
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private static final int BTN_PUSH = 0;

    /** */
    private static final int BTN_CHECK = 1;

    /** */
    private static final int BTN_RADIO = 2;
    
    /** */
    private static final int MARGIN = 5;

    /** */
    private JComboBox fontFamily;

    /** */
    private JComboBox fontSize;

    /** */
    private JComboBox fontStyle;

    /**
     */
    public EditPanel()
    {
        setLayout(new BorderLayout());

        //setup the checkbox switches panel
        JCheckBox autoIndentCheckBox = new JCheckBox(Dialog.getBundle().getString("AutoIndentCheckBox"));
        autoIndentCheckBox.setActionCommand("AutoIndentCheckBox");
        autoIndentCheckBox.addActionListener(this);

        JCheckBox replaceTabsCheckBox = new JCheckBox(Dialog.getBundle().getString("ReplaceTabsCheckBox"));
        autoIndentCheckBox.setActionCommand("ReplaceTabsCheckBox");
        autoIndentCheckBox.addActionListener(this);
        
        JCheckBox removeTrailSpaceCheckBox = new JCheckBox(Dialog.getBundle().getString("RemoveTrailingSpaceCheckBox"));
        autoIndentCheckBox.setActionCommand("RemoveTrailingSpaceCheckBox");
        autoIndentCheckBox.addActionListener(this);
        
        JCheckBox autoBracesCheckBox = new JCheckBox(Dialog.getBundle().getString("AutoBracesCheckBox"));
        autoIndentCheckBox.setActionCommand("AutoBracesCheckBox");
        autoIndentCheckBox.addActionListener(this);
        
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)),
                Dialog.getBundle().getString("GeneralPaneTitle")));
        options.add(autoIndentCheckBox);
        options.add(replaceTabsCheckBox);
        options.add(removeTrailSpaceCheckBox);
        options.add(autoBracesCheckBox);

        //setup the font panel
        JPanel font = new JPanel();
        font.setLayout(new BoxLayout(font, BoxLayout.Y_AXIS));
        font.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            Dialog.getBundle().getString("FontPaneTitle")));
        fontFamily = createAndAddMenu("FontFamily", font);
        fontSize = createAndAddMenu("FontSize", font);
        fontStyle = createAndAddMenu("FontStyle", font);
        setupFontMenus();

        add(options, BorderLayout.CENTER);
        add(font, BorderLayout.EAST);
    }

    /**
     */
    public void setPreferences()
    {
    }

    /**
     * @param evt {@link java.awt.ActionEvent} that triggered this action.
     */
    public void actionPerformed(ActionEvent evt)
    {
        try
        {
            Class[] parameters = {ActionEvent.class};
            Object[] arguments = {evt};
            String name = "handleAction__" + evt.getActionCommand();
            Method meth = getClass().getDeclaredMethod(name, parameters);
            meth.invoke(this, arguments);
        }
        catch (NoSuchMethodException err)
        {
            System.err.println("No method for action " + evt.getActionCommand());
        }
        catch (InvocationTargetException err)
        {   //the action handler method threw an exception
            throw new UnsupportedOperationException("Exception in handling.", err);
        }
        catch (IllegalAccessException err)
        {   //This should not happen
            throw new UnsupportedOperationException("Invalid method protection.", err);
        }
    }

    /**
     * @param key The localization key for the button.
     * @param owner The panel the combo box lives in.
     * @return The newly create combo box.
     */
    private JComboBox createAndAddMenu(String key, JPanel owner)
    {
        JPanel panel = new JPanel();
        owner.add(panel);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel label = new JLabel(
                Dialog.getBundle().getString(key));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(label);
        JComboBox menu = new JComboBox();
        panel.add(menu);
        return menu;
    }

    /**
     * @param key The localization key for the button.
     * @return The newly created label.
     */
    private JLabel createLabel(String key)
    {
        JLabel ret = new JLabel(Dialog.getBundle().getString(key));
        ret.setHorizontalAlignment(SwingConstants.RIGHT);
        return ret;
    }

    /**
     */
    private void setupFontMenus()
    {
        Locale locale = Locale.getDefault();
        Collection<String> names = new HashSet<String>();
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAllFonts();
        for (int i = 0; i < fonts.length; i++)
        {
            String name = fonts[i].getFamily(locale);
            if (!names.contains(name))
            {
                names.add(name);
                fontFamily.addItem(new Pair(name, fonts[i]));
            }
        }
        fontStyle.addItem(new Pair("Plain", new Integer(Font.PLAIN)));
        fontStyle.addItem(new Pair("Bold", new Integer(Font.BOLD)));
        fontStyle.addItem(new Pair("Italic", new Integer(Font.ITALIC)));
    }

    /**
     */
    private static class Pair
    {
        /** The name of this pair. */
        private final String name;
        
        /** The object stored in the pair. */
        private final Object obj;
        
        /**
         * Create a new pair object.
         *
         * @param n The name for the pair.
         * @param o The object to be stored in the pair.
         */
        public Pair(String n, Object o)
        {
            name = n;
            obj = o;
        }
        
        /** {@inheritDoc} */
        public String toString()
        {
            return name;
        }
    }
}

