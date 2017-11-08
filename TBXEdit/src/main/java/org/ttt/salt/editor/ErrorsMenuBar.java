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
package org.ttt.salt.editor;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.awt.print.PrinterJob;

/**
 * @version $Id$
 */
public class ErrorsMenuBar extends org.flyingtitans.swing.MenuBar
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Default resource bundle for all MenuBar objects. */
    private static ResourceBundle bundle;
    
    /** Default resource bundle for accelerator and mnemonics. */
    private static ResourceBundle bundleAccel;

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle("org.ttt.salt.editor.ErrorsMenuBar", Locale.getDefault());
        bundleAccel = bundle;
    }

    /** The editing window this is associated with. */
    private final ErrorsWindow ewin;
    
    /**
     * Create a new error menu bar for the errors window.
     *
     * @param w The error display window.
     */
    public ErrorsMenuBar(ErrorsWindow w)
    {
        super(false);
        ewin = w;
    }


//CHECKSTYLE: MethodName OFF
    
    /**
     */
    public void handleMenuItem__File_New()
    {
        try
        {
            TBXEditor.open(null);
        }
        catch (IOException err)
        {
            System.out.println(err);
        }
    }

    /**
     */
    public void handleMenuItem__File_Open()
    {
        System.err.println("NOT IMPLEMENTED File_Open");
    }

    /**
     */
    public void handleMenuItem__File_Close()
    {
        ewin.close();
    }

    /**
     */
    public void handleMenuItem__File_Save()
    {
        System.err.println("NOT IMPLEMENTED File_Save");
    }

    /**
     */
    public void handleMenuItem__File_SaveAs()
    {
        System.err.println("NOT IMPLEMENTED File_SaveAs");
    }

    /**
     */
    public void handleMenuItem__File_PageSetup()
    {
        System.err.println("NOT IMPLEMENTED File_PageSetup");
    }

    /**
     */
    public void handleMenuItem__File_Print()
    {
        //CHECKSTYLE: IllegalCatch OFF
        System.err.println("NOT IMPLEMENTED File_Print");
        try
        {
            PrinterJob printjob = PrinterJob.getPrinterJob();
            //ewin.getTBXEditor().print(printjob);
        }
        catch (Throwable err)
        {
            err.printStackTrace(System.err);
        }
        //CHECKSTYLE: IllegalCatch ON
    }

    /**
     */
    public void handleMenuItem__File_Quit()
    {
        org.ttt.salt.editor.Main.getInstance().signalQuit();
    }

    /**
     */
    public void handleMenuItem__Window_Tile()
    {
        System.err.println("NOT IMPLEMENTED Window_Tile");
    }

    /**
     */
    public void handleMenuItem__Window_TileVert()
    {
        System.err.println("NOT IMPLEMENTED Window_TileVert");
    }

    /**
     */
    public void handleMenuItem__Window_Stack()
    {
        System.err.println("NOT IMPLEMENTED Window_Stack");
    }

    /**
     */
    public void handleMenuItem__Help_Contents()
    {
        System.err.println("NOT IMPLEMENTED Help_Contents");
    }

    /**
     */
    public void handleMenuItem__Help_Tutorial()
    {
        System.err.println("NOT IMPLEMENTED Help_Tutorial");
    }

    /**
     */
    public void handleMenuItem__Help_Index()
    {
        System.err.println("NOT IMPLEMENTED Help_Index");
    }

    /**
     */
    public void handleMenuItem__Help_Search()
    {
        System.err.println("NOT IMPLEMENTED Help_Search");
    }

    /**
     */
    public void handleMenuItem__Help_About()
    {
        org.ttt.salt.editor.about.Dialog.display(ewin.getRootPane());
    }

    /**
     */
    public void handleMenuItem__Help_GPL()
    {
        System.err.println("NOT IMPLEMENTED Help_GPL");
    }
//CHECKSTYLE: MethodName ON

    
    /** {@inheritDoc} */
    protected String getString(String key)
    {
        String ret;
        try
        {
            ret = bundle.getString(key);
        }
        catch (MissingResourceException err)
        {
            ret = super.getString(key);
        }
        return ret;
    }

    /**
     */
    protected void updateMenuStatus()
    {
        super.updateMenuStatus();
        
        file_save.setEnabled(true);
        file_pagesetup.setEnabled(false);

        window_tile.setEnabled(false);
        window_tilevert.setEnabled(false);
        window_stack.setEnabled(false);

        help_contents.setEnabled(false);
        help_tutorial.setEnabled(false);
        help_index.setEnabled(false);
        help_search.setEnabled(false);
        help_about.setEnabled(true);
    }
}

