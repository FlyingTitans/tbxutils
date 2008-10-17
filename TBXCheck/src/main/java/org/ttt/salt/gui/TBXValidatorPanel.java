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
package org.ttt.salt.gui;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.util.SortedSet;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.swing.SpringLayout;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXValidatorPanel extends javax.swing.JPanel
    implements java.awt.event.HierarchyBoundsListener
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Main logger for this panel. */
    private static Logger LOGGER;
    
    
    
    //These variables define the various parts of the panel. Since this will
    //change the javadoc is not required on each, but the name of the variable
    //should be highly descriptive.
    //CHECKSTYLE: JavadocVariable OFF
    private javax.swing.JComboBox boxCountry;
    private javax.swing.JComboBox boxLanguage;
    private javax.swing.JComboBox boxLogging;
    private javax.swing.JButton buttonValidate;
    private javax.swing.JLabel labelCountry;
    private javax.swing.JLabel labelLanguage;
    private javax.swing.JLabel labelLogging;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelOptions;
    //CHECKSTYLE: JavadocVariable OFF
    
    /**
     * Creates new form TBXValidatorPanel.
     *
     * @param bndl The {@link java.util.ResourceBundle} that contains all the
     *  resources necessary to build this validation panel.
     */
    public TBXValidatorPanel(ResourceBundle bndl)
    {
        boxLogging = new javax.swing.JComboBox();
        boxLanguage = new javax.swing.JComboBox();
        boxCountry = new javax.swing.JComboBox();

        setLayout(new BorderLayout());

        layoutButtons(bndl);
        add(panelButtons, BorderLayout.SOUTH);

        layoutPanelOptions(bndl);
        add(panelOptions, BorderLayout.CENTER);

        try
        {
            System.setProperty("java.util.logging.config.class", "org.ttt.salt.LogConfig");
            LogManager.getLogManager().readConfiguration();
        }
        catch (java.io.IOException err)
        {
            err.printStackTrace();
            System.exit(1);
        }
        LOGGER = Logger.getLogger("org.ttt.salt");
    }
        
    /**
     * Layout the buttons panel.
     *
     * @param bndl The {@link java.util.ResourceBundle} that contains all the
     *  resources needed to build this panel.
     */
    private void layoutButtons(ResourceBundle bndl)
    {
        ActionOpen action = (ActionOpen) TBXAbstractAction.getAction(ActionOpen.class);
        buttonValidate = new javax.swing.JButton(action);
        boxLogging.addActionListener(action);
        
        panelButtons = new javax.swing.JPanel();
        panelButtons.setLayout(new FlowLayout());
        panelButtons.add(buttonValidate);
    }
    
    /**
     * Layout the options panel.
     *
     * @param bndl The {@link java.util.ResourceBundle} that contains all the
     *  resources needed to build this panel.
     */
    private void layoutPanelOptions(ResourceBundle bndl)
    {
        panelOptions = new javax.swing.JPanel();
        labelLogging = new javax.swing.JLabel("Logging");
        labelLanguage = new javax.swing.JLabel("Language");
        labelCountry = new javax.swing.JLabel("Country");

        //Configure
        String[] logvals = {"SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"};
        boxLogging.setModel(new javax.swing.DefaultComboBoxModel(logvals));
        boxLogging.setSelectedItem("INFO"); //TODO: this needs to based on preferences
        
        SortedSet<String> iso639 = ISOReference.getInstance().get639alpha2();
        boxLanguage.setModel(new javax.swing.DefaultComboBoxModel(iso639.toArray()));
        boxLanguage.setSelectedItem("en"); //TODO: this needs to based on LOCALE and preferences
        
        SortedSet<String> iso3166 = ISOReference.getInstance().get3166alpha2();
        boxCountry.setModel(new javax.swing.DefaultComboBoxModel(iso3166.toArray()));
        boxCountry.setSelectedItem("US"); //TODO: this needs to based on LOCALE and preferences
        
        
        //Layout
        panelOptions.setLayout(new SpringLayout());
        
        labelLogging.setLabelFor(boxLogging);
        panelOptions.add(labelLogging);
        panelOptions.add(boxLogging);
        
        labelLanguage.setLabelFor(boxLanguage);
        panelOptions.add(labelLanguage);
        panelOptions.add(boxLanguage);

        labelCountry.setLabelFor(boxCountry);
        panelOptions.add(labelCountry);
        panelOptions.add(boxCountry);
        
        final int rows = 3;
        final int cols = 2;
        final int initX = 6;
        final int initY = 6;
        final int xPad = 6;
        final int yPad = 6;
        SpringUtilities.makeCompactGrid(panelOptions, rows, cols, initX, initY, xPad, yPad);
        panelOptions.setOpaque(true);  //content panes must be opaque

        /* TODO: When JDK 1.6 is available on MacOS then move to GroupLayout
        org.jdesktop.layout.GroupLayout panelOptionsLayout = new org.jdesktop.layout.GroupLayout(panelOptions);
        panelOptions.setLayout(panelOptionsLayout);
        panelOptionsLayout.setHorizontalGroup(
            panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labelLogging)
                    .add(labelLanguage)
                    .add(labelCountry))
                .add(24, 24, 24)
                .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(boxLogging, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, boxLanguage, 0,
                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, boxCountry,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(265, Short.MAX_VALUE))
        );
        panelOptionsLayout.setVerticalGroup(
            panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(boxLogging, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelLogging))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(boxLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelLanguage))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(boxCountry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelCountry))
                .addContainerGap(169, Short.MAX_VALUE))
        );
        */
    }

    /** {@inheritDoc} */
    public void ancestorMoved(java.awt.event.HierarchyEvent evt)
    {
    }
    
    /** {@inheritDoc} */
    public void ancestorResized(java.awt.event.HierarchyEvent evt)
    {
    }
    
}
