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

import java.io.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Caret;
import javax.swing.text.ViewFactory;
import javax.swing.text.EditorKit;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
class Kit_TextJava extends EditorKit
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    public Object clone()
    {
        throw new UnsupportedOperationException();
    }
    
    public Caret createCaret()
    {
        throw new UnsupportedOperationException();
    }
    
    public Document createDefaultDocument()
    {
        throw new UnsupportedOperationException();
    }
    
    public void deinstall(JEditorPane c)
    {
        throw new UnsupportedOperationException();
    }
    
    public Action[] getActions()
    {
        throw new UnsupportedOperationException();
    }
    
    public String getContentType()
    {
        return "text/x-java";
    }
    
    public ViewFactory getViewFactory()
    {
        throw new UnsupportedOperationException();
    }
    
    public void install(JEditorPane c)
    {
        throw new UnsupportedOperationException();
    }
    
    public void read(InputStream in, Document doc, int pos)
    {
        throw new UnsupportedOperationException();
    }
    
    public void read(Reader in, Document doc, int pos)
    {
        throw new UnsupportedOperationException();
    }
    
    public void write(OutputStream out, Document doc, int pos, int len)
    {
        throw new UnsupportedOperationException();
    }
    
    public void write(Writer out, Document doc, int pos, int len)
    {
        throw new UnsupportedOperationException();
    }
}

