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

import java.io.File;
import java.io.IOException;
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * Setup the application to operate correctly within the MacOS X Aqua
 * environment.
 * <p>
 * For information on setting Java configuration information, including
 * setting Java properties, refer to Apple's documentation.</p>
 *
 * @see <a href="http://developer.apple.com/techpubs/java/java.html">Apple Documentation</a>
 * @author  Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class Aqua extends ApplicationAdapter
{
    /** SCM information. */
    private static final String RCSID = "$Id$";

    /**
     */
    public Aqua()
    {
        super();
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    "TBXGui");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.showGrowBox", "true");
        //System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("dock:name", "TBXGui");
        Application.getApplication().setEnabledPreferencesMenu(true);
        Application.getApplication().addApplicationListener(this);
    }

    /** {@inheritDoc} */
    public void handleAbout(ApplicationEvent e)
    {
        System.err.println("handleAbout...");
        //AboutBox aboutBox = new AboutBox();
        //aboutBox.setResizable(false);
        //aboutBox.setVisible(true);
        //aboutBox.show();
    }

    /** {@inheritDoc} */
    public void handleOpenApplication(ApplicationEvent e)
    {
        System.err.println("handleOpenApplication...");
        //Preferences.getInstance().setDefaults();
    }

    /** {@inheritDoc} */
    public void handleOpenFile(ApplicationEvent e)
    {
        if (!e.isHandled())
        {
            try
            {
                File file = new File(e.getFilename());
                TBXValidator.validateFile(file);
            }
            catch (IOException err)
            {
                err.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    public void handlePreferences(ApplicationEvent e)
    {
        //TODO
        System.err.println("handlePreferences...");

        //Preferences prefs = new Preferences();
        //prefs.setResizable(false);
        //prefs.setVisible(true);
        //prefs.show();
    }

    /** {@inheritDoc} */
    public void handlePrintFile(ApplicationEvent e)
    {
        System.err.println("handlePrintFile...");
        //Document doc = Main.buildDocument(uri);
        //doc.print();
    }

    /** {@inheritDoc} */
    public void handleQuit(ApplicationEvent e)
    {
        Main.getInstance().signalQuit();
    }
}

