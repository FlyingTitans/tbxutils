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
package org.ttt.salt.editor.find;

import java.lang.reflect.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
class FilesPanel extends JPanel implements ActionListener
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
    private final Dialog dialog;

    /** */
    private boolean multiFile = false;

    /** */
    private boolean batchSearch = false;

    /** */
    private boolean searchNested = false;

    /** */
    private final JTextArea fileNameText = createTextArea("FileNameText");

    /** */
    private final JLabel directoryLabel = createLabel("DirectoryLabel");

    /**
     */
    public FilesPanel(Dialog dialog)
    {
        this.dialog = dialog;
        setLayout(new BorderLayout());

        //setup switches
        AbstractButton multiFileCheckBox= (JCheckBox) createButton(
                BTN_CHECK, "MultiFileCheckBox");
        AbstractButton batchSearchCheckBox= (JCheckBox) createButton(
                BTN_CHECK, "BatchSearchCheckBox");
        AbstractButton searchNestedCheckBox= (JCheckBox) createButton(
                BTN_CHECK, "SearchNestedCheckBox");
        JPanel switches = new JPanel();
        this.add(switches, BorderLayout.WEST);
        switches.setLayout(new BoxLayout(switches, BoxLayout.Y_AXIS));
        switches.add(multiFileCheckBox);
        switches.add(batchSearchCheckBox);
        switches.add(searchNestedCheckBox);

/*
        //setup replace string panel
        JPanel replacepanel = new JPanel();
        center.add(replacepanel);
        replacepanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        replacepanel.add(createLabel("ReplaceLabel"));
        replacepanel.add(new JScrollPane(replaceText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        //setup switches panel
        JPanel switches = new JPanel();
        center.add(switches);
        switches.setLayout(new GridLayout());
     */
    }

    /**
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
            System.err.println("No method for action "
                + evt.getActionCommand());
        }
        catch (InvocationTargetException err)
        {
            //the action handler method threw an exception
        }
        catch (IllegalAccessException err)
        {
            //This should not happen
        }
        updateButtonStatus();
    }

    /**
     */
    private void updateButtonStatus()
    {
    }

    /**
     */
    private JLabel createLabel(String key)
    {
        JLabel ret = new JLabel(Dialog.getBundle().getString(key));
        ret.setHorizontalAlignment(SwingConstants.RIGHT);
        return ret;
    }

    /**
     */
    private AbstractButton createButton(int type, String key)
    {
        AbstractButton ret;
        switch (type)
        {
            case BTN_PUSH:
                ret = new JButton(Dialog.getBundle().getString(key));
                break;
            case BTN_CHECK:
                ret = new JCheckBox(Dialog.getBundle().getString(key));
                break;
            case BTN_RADIO:
                ret = new JRadioButton(Dialog.getBundle().getString(key));
                break;
            default:
                throw new IllegalArgumentException();
        }
        ret.setActionCommand(key);
        ret.addActionListener(this);
        return ret;
    }

    /**
     */
    private JTextArea createTextArea(String key)
    {
        JTextArea ret = new JTextArea();
        ret.setColumns(40);
        ret.setRows(4);
        ret.setLineWrap(true);
        //ret.getDocument().addDocumentListener(this);
        return ret;
    }
}

