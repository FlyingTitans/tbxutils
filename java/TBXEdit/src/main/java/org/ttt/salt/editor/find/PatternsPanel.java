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
class PatternsPanel extends JPanel implements ActionListener
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private static final int BTN_PUSH = 0;

    /** */
    private static final int BTN_CHECK = 1;

    /** */
    private static final int BTN_RADIO = 2;

    /**
     */
    public PatternsPanel()
    {
        setLayout(new BorderLayout());
    /*
        Box center = Box.createVerticalBox();
        add(center, BorderLayout.CENTER);

        //setup search string panel
        JPanel findpanel = new JPanel();
        center.add(findpanel);
        findpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        findpanel.add(createLabel("FindLabel"));
        findpanel.add(new JScrollPane(findText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

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

        //setup the limits panel with radio buttons
        JRadioButton stopBottomButton = (JRadioButton) createButton(
                BTN_RADIO, "StopAtBottomRadioButton");
        JRadioButton wrapButton = (JRadioButton) createButton(
                BTN_RADIO, "WrapAroundRadioButton");
        JRadioButton selectionButton = (JRadioButton) createButton(
                BTN_RADIO, "SearchSelectionRadioButton");
        stopBottomButton.setSelected(true);
        JPanel limits = new JPanel();
        limits.setLayout(new BoxLayout(limits, BoxLayout.Y_AXIS));
        limits.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        limits.add(stopBottomButton);
        limits.add(wrapButton);
        limits.add(selectionButton);
        switches.add(limits);
        ButtonGroup limitsGroup = new ButtonGroup();
        limitsGroup.add(stopBottomButton);
        limitsGroup.add(wrapButton);
        limitsGroup.add(selectionButton);

        //setup the checkbox switches panel
        AbstractButton wholeWordCheckBox= (JCheckBox) createButton(
                BTN_CHECK, "WholeWordCheckBox");
        AbstractButton ignoreCaseCheckBox = (JCheckBox) createButton(
                BTN_CHECK, "IgnoreCaseCheckBox");
        AbstractButton reCheckBox = (JCheckBox) createButton(
                BTN_CHECK, "RegularExpressionCheckBox");
        ignoreCaseCheckBox.setSelected(true);
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        options.add(wholeWordCheckBox);
        options.add(ignoreCaseCheckBox);
        options.add(reCheckBox);
        switches.add(options);

        //setup the command buttons panel
        findBtn.setDefaultCapable(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(findBtn, null);
        buttonPanel.add(replaceBtn, null);
        buttonPanel.add(replaceFindBtn, null);
        buttonPanel.add(replaceAllBtn, null);
        updateButtonStatus();
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

