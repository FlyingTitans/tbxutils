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
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

/**
 * This provides the base for all mime types that match text/xxxx, where
 * the a sub-class of this kit will handle the sub-types.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
class Text_Document extends PlainDocument
{
    /*
     */
    
    /**
     */
    //protected class BranchElement
    //{
    //}
    
    /**
     */
    //protected class LeafElement
    //{
    //}

    /**
     */
    //protected class LineElement extends BranchElement
    //{
    //}

    /**
     */
    //protected class WordElement extends LeafElement
    //{
    //}

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /**
     */
    public Text_Document()
    {
        super();
    }

    /** {@inheritDoc} */
    //public int getLength()
    //{
    //    /* TODO */
    //    return 0;
    //}

    /** {@inheritDoc} */
    //public void addDocumentListener(DocumentListener listener)
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    //public void removeDocumentListener(DocumentListener listener)
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    //public void addUndoableEditListener(UndoableEditListener listener)
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    //public void removeUndoableEditListener(UndoableEditListener listener)
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    //public Object getProperty(Object key)
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    //public void putProperty(Object key, Object value)
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    //public void remove(int offs, int len) throws BadLocationException
    //{
    //    /* TODO */
    //}

    /** {@inheritDoc} */
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException
    {
        /* TODO */
        super.insertString(offset, str, a);
    }

    /** {@inheritDoc} */
    public String getText(int offset, int length)
        throws BadLocationException
    {
        /* TODO */
        return super.getText(offset, length);
    }

    /** {@inheritDoc} */
    public void getText(int offset, int length, Segment txt)
        throws BadLocationException
    {
        /* TODO */
        super.getText(offset, length, txt);
    }

    /** {@inheritDoc} */
    //public Position getStartPosition()
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    //public Position getEndPosition()
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    //public Position createPosition(int offs) throws BadLocationException
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    //public Element[] getRootElements()
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    //public Element getDefaultRootElement()
    //{
    //    /* TODO */
    //    return null;
    //}

    /** {@inheritDoc} */
    public void render(Runnable r)
    {
        /* TODO */
        System.err.println("render");
        super.render(r);
    }
}

