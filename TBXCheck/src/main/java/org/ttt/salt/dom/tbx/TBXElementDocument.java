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
package org.ttt.salt.dom.tbx;

import java.lang.ref.Reference;
import java.util.List;
import org.xml.sax.Locator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;


/**
 * This is a specialized version of the document element that is returned by
 * {@link org.w3c.dom.Document#getDocumentElement}. This will store top level
 * elements as {@link java.lang.ref.Reference} to allow large document
 * parsing and validation to remove old elements if memory requires it.
 * <p>
 * This should not be used if normal hard references are used to store child
 * documents, the default implementation will work correctly for that.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXElementDocument extends TBXElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** {@link org.w3c.dom.Element} that backs this element. */
    private Element implDocElement;
        
    /**
     * List of top level elements in this document, not used if
     * asSoftReference is false.
     */
    private List<Reference<Element>> children = new java.util.ArrayList<Reference<Element>>();
    
    /**
     * Create an TBXElement and start building its contents.
     *
     * @param ownerDoc The TBXDocument this element is created in.
     * @param name The element tag id.
     * @param loc The SAX locator that describes the start tag location.
     * @param implElem The underlying DOM implementation document element.
     */
    public TBXElementDocument(TBXDocument ownerDoc, String name, Locator loc,
        Element implElem)
    {
        super(ownerDoc, name, loc);
        if (implElem == null)
            throw new IllegalArgumentException("Element may not be null.");
        implDocElement = implElem;
    }
    
    /** {@inheritDoc} */
    public Node appendChild(Node newChild)
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node insertBefore(Node newChild, Node refChild)
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node removeChild(Node oldChild)
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node replaceChild(Node newChild, Node oldChild)
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node cloneNode(boolean deep)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getChildNodes()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getFirstChild()
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node getLastChild()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getNextSibling()
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Node getPreviousSibling()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getElementsByTagName(String name)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
    {
        throw new UnsupportedOperationException();
    }
}

