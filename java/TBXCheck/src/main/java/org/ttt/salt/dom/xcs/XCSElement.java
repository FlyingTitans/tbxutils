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
package org.ttt.salt.dom.xcs;

import org.xml.sax.Locator;
import org.w3c.dom.Element;
import org.apache.xerces.dom.ElementImpl;
import org.ttt.salt.XCSDocument;


/**
 * This is the base class for all elements in an TBX XCS XML Document (how's
 * that for a triple TLA).
 * <p>
 * This is designed to add a few extra methods to the {@link org.w3c.dom.Element}
 * interface to allow validation and output of an XCS element in well-formed
 * valid XML format.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class XCSElement extends ElementImpl implements Element
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
        
    /** Line in the XML stream this element starts. */
    private int lineStart;
    
    /** Column in the XML stream this element starts. */
    private int columnStart;

    /** Line in the XML stream this element ends. */
    private int lineEnd;
    
    /** Column in the XML stream this element ends. */
    private int columnEnd;
    
    /**
     * Create an XCSElement and start building its contents.
     *
     * @param ownerDoc The TBXDocument this element is created in.
     * @param name The element tag id.
     * @param loc The SAX locator that describes the start tag location.
     */
    public XCSElement(XCSDocument ownerDoc, String name, Locator loc)
    {
        super(ownerDoc, name);
        if (loc != null)
        {
            lineStart = loc.getLineNumber();
            columnStart = loc.getColumnNumber();
            lineEnd = loc.getLineNumber();
            columnEnd = loc.getColumnNumber();
        }
    }
    
    /**
     * Signal that the element has completed construction and the end tag
     * has been reached.
     *
     * @param loc The SAX locator that describes the end tag location.
     */
    void endElement(Locator loc)
    {
        lineEnd = loc.getLineNumber();
        columnEnd = loc.getColumnNumber();
    }
    
    /**
     * Get a string that defines the location in the document this element
     * is located at.
     *
     * @return The location identification string.
     */
    public String getLocationString()
    {
        return String.format("Start %d:%d, End %d:%d", lineStart, columnStart,
            lineEnd, columnEnd);
    }
}

