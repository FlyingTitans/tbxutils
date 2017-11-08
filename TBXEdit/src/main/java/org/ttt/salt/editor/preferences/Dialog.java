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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
class Dialog extends JDialog implements ActionListener, WindowListener
{
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** */
    private static Dialog instance;

    /**
     * @return The single preferences dialog instance.
     */
    public static Dialog getInstance()
    {
        if (instance == null)
        {
            instance = new Dialog();
        }
        return instance;
    }

    /**
     * @return The {@link java.util.ResourceBundle} to be used for localization.
     */
    static java.util.ResourceBundle getBundle()
    {
        return java.util.ResourceBundle.getBundle(
            "org.ttt.salt.editor.preferences.Dialog");
    }

    /** Main frame for the find dialog. */
    private final JFrame frame = new JFrame("Preferences");

    /** */
    private final EditPanel editPanel = new EditPanel();

    /**
     */
    public Dialog()
    {
        setTitle(getBundle().getString("DialogTitle"));
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        JTabbedPane tabpane = new JTabbedPane(JTabbedPane.LEFT);
        tabpane.add(editPanel, getBundle().getString("EditTab"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton okbtn = createButton("OKButton");
        okbtn.setDefaultCapable(true);
        buttonPanel.add(okbtn);
        buttonPanel.add(createButton("CancelButton"));
        buttonPanel.add(createButton("HelpButton"));
        getContentPane().add(tabpane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okbtn);
        pack();
        //position the dialog
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
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
        {   //the action handler method threw an exception
            throw new UnsupportedOperationException("Exception in handling.", err);
        }
        catch (IllegalAccessException err)
        {   //This should not happen
            throw new UnsupportedOperationException("Invalid method protection.", err);
        }
        updateButtonStatus();
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowOpened(WindowEvent evt)
    {
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowActivated(WindowEvent evt)
    {
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowDeactivated(WindowEvent evt)
    {
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowIconified(WindowEvent evt)
    {
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowDeiconified(WindowEvent evt)
    {
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowClosing(WindowEvent evt)
    {
        int res = JOptionPane.showConfirmDialog(this,
                getBundle().getString("SavePreferences"),
                getBundle().getString("SavePreferencesTitle"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        switch (res)
        {
            case JOptionPane.YES_OPTION:
                editPanel.setPreferences();
                setVisible(false);
                dispose();
                break;
            case JOptionPane.NO_OPTION:
                dispose();
                break;
            case JOptionPane.CANCEL_OPTION:
                break;
            default:
                throw new UnsupportedOperationException("Unknown JOptionPane value: " + res);
        }
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    public void windowClosed(WindowEvent evt)
    {
        instance = null;
    }

    /**
     * @param key The ID to use for this button.
     * @return The newly created button.
     */
    private JButton createButton(String key)
    {
        JButton ret = new JButton(Dialog.getBundle().getString(key));
        ret.setActionCommand(key);
        ret.addActionListener(this);
        return ret;
    }

    /**
     */
    private void updateButtonStatus()
    {
    }

//CHECKSTYLE: MethodName OFF
    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    private void handleAction__OKButton(ActionEvent evt)
    {
        editPanel.setPreferences();
        setVisible(false);
        dispose();
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    private void handleAction__CancelButton(ActionEvent evt)
    {
        setVisible(false);
        dispose();
    }

    /**
     * @param evt {@link java.awt.event.ActionEvent} that triggered this event.
     */
    private void handleAction__HelpButton(ActionEvent evt)
    {
        System.out.println("Preferences Help");
    }
//CHECKSTYLE: MethodName ON
}

