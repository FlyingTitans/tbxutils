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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xerces.dom.DocumentImpl;
import org.ttt.salt.TBXException;


/**
 * This holds a DOM style TBX document structure.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXDocument extends DocumentImpl implements Document
{
    /*
     */
    
    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.tbx");
    
    /** The class prefix for element editors. */
    public static final String PREFIX = "org.ttt.salt.dom.tbx.Element_";
    
    /** Timestampt when document was created. */
    private final Date created = new Date();
    
    /** Top level elements are stored as hard or soft references. */
    private final boolean hardRef;
    
    /** TBX document element. */
    private TBXElementDocument tbxDocElem;

    /** Error accumulation list. */
    private SortedSet<TBXException> exceptions = new java.util.TreeSet<TBXException>();
    
    /**
     * Create an empty TBX document.
     *
     * @param hardref If this is true then the normal hard reference system is
     *  used for document top level elements. If this is false then these top
     *  level elements are stored in a soft/weak reference to allow them to
     *  to be garbage collected when working with large TBX documents.
     */
    public TBXDocument(boolean hardref)
    {
        hardRef = hardref;
    }
    
    /** {@inheritDoc} */
    public Element getDocumentElement()
    {
        return hardRef ? super.getDocumentElement() : tbxDocElem;
    }
    
    /**
     * Get a list of all exceptions that occured while this document was being
     * parsed and validated.
     *
     * @return The sorted set of all exceptions that occured.
     */
    public SortedSet<TBXException> getParseExceptions()
    {
        return exceptions;
    }
    
    /**
     * Add an exception to the list of exceptions on this document.
     *
     * @param err The TBXException to add to the set of exceptions.
     */
    public void addParseException(TBXException err)
    {
        exceptions.add(err);
    }
    
    /**
     * Create a new TBX element for this document. This should be called
     * instead of {@link org.w3c.dom.Document#createElement} when building a
     * new element because it allow for the location of the element to be
     * recorded.
     *
     * @param tagName The name of the element type to instantiate.
     * @param loc The {@link org.xml.sax.Locator} when this element tag is
     *  encountered.
     * @throws SAXParseException Any exceptions that occur will be wrapped in
     *  this exception.
     * @return The newly created TBX element.
     * @see #createElement
     */
    @SuppressWarnings("unchecked")
    public TBXElement createTBXElement(String tagName, Locator loc) throws SAXParseException
    {
        if (errorChecking && !isXMLName(tagName, true))
            super.createElement(tagName);   //let the superclass throw the error
        TBXElement ret = null;
        try
        {
            try
            {
                Class<TBXElement> clazz = (Class<TBXElement>) Class.forName(PREFIX + tagName,
                                            true, getClass().getClassLoader());
                Constructor<TBXElement> cstrct = clazz.getConstructor(TBXDocument.class,
                    String.class, Locator.class);
                ret = cstrct.newInstance(this, tagName, loc);
            }
            catch (ClassNotFoundException err)
            {
                if (!TBXParser.KNOWN_MISSING.contains(tagName))
                    Logger.getLogger("org.ttt.salt.dom.tbx").log(Level.INFO, "Unknown TBX Element: {0}", tagName);
                ret = new TBXElement(this, tagName, loc);
            }

            if (tagName.equals("martif"))
            {
                docElement = ret;
                if (!hardRef)
                    tbxDocElem = new TBXElementDocument(this, tagName, loc, docElement);
            }
        }
        catch (NoSuchMethodException err)
        {
            Logger.getLogger("org.ttt.salt.dom.tbx").log(Level.SEVERE, "Invalid TBX Class: {0}", tagName);
            throw new SAXParseException("Invalid TBX Class", loc, err);
        }
        catch (InstantiationException err)
        {
            Logger.getLogger("org.ttt.salt.dom.tbx").log(Level.SEVERE, "TBX class not concrete: {0}", tagName);
            throw new SAXParseException("TBX class not concrete", loc, err);
        }
        catch (IllegalAccessException err)
        {
            Logger.getLogger("org.ttt.salt.dom.tbx").log(Level.SEVERE, "TBX constructor not accessible on {0}", tagName);
            throw new SAXParseException("TBX constructor not accessible", loc, err);
        }
        catch (InvocationTargetException err)
        {
            Logger.getLogger("org.ttt.salt.dom.tbx").log(Level.SEVERE, "TBX element {0} creation error", tagName);
            throw new SAXParseException("TBX element creation error", loc, err);
        }
        return ret;
    }
    
    /** {@inheritDoc} */
    public Element createElement(String tagName)
    {
        try
        {
            return createTBXElement(tagName, null);
        }
        catch (SAXParseException err)
        {   //It is not appropriate to place this into a DOMException
            throw new IllegalStateException(String.format("Error creating TBXElement: %s", tagName), err);
        }
    }
}


