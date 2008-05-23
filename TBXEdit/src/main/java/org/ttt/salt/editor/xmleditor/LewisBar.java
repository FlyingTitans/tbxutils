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

import java.awt.*;
import java.util.*;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.*;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class LewisBar implements Highlighter.HighlightPainter
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private static final LewisBar instance = new LewisBar();
    
    /**
     */
    private LewisBar()
    {
        super();
    }
    
    /**
     */
    public static LewisBar getInstance()
    {
        return instance;
    }
        
    /**
     */
    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
    {
        //TODO -- check user pref
        Dimension dim = c.getSize();
        Font font = c.getFont();
        FontMetrics metrics = c.getFontMetrics(font);
        Insets inset = (Insets) UIManager.get("EditorPane.margin");
        int columns = 80;    //TODO--user pref
        int offset = metrics.charWidth('m') * columns + inset.left;
        g.setColor(Color.lightGray);
        g.drawLine(offset, 0, offset, dim.height);
    }
}

