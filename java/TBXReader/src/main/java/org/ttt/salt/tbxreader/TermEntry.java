/*
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
package org.ttt.salt.tbxreader;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

/**
 * This represents a single term entry from the TBX file. If the term entry
 * could not be built because of an XML well-formed error, XML validation
 * error, or XCS conformance error then there will be an associated list of
 * TBX exceptions that will give the errors in order.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TermEntry
{
    /** The DOM element that produced this term entry. */
    private Element element;
    
    /** The line where the term entry starts. */
    private int line;
    
    /** The column where the term entry starts. */
    private int column;
        
    /** The list of exceptions that have occurred in building this term entry. */
    private List<TBXException> exceptions = new java.util.ArrayList<TBXException>();
    
    /**
     * Create a new term entry.
     *
     * @param elem The DOM element that contains all of the validated
     *  information for the term entry.
     * @param loc The Locator in the XML this term entry is starting at.
     */
    public TermEntry(Element elem, Locator loc)
    {
        element = elem;
        line = loc.getLineNumber();
        column = loc.getColumnNumber();        
    }
    
    /**
     * The term entry is complete and can be built from DOM element.
     */
    public void init() throws TBXException
    {
        element.getParentNode().removeChild(element);
    }
    
    /**
     * Get the line number this entry starts at.
     *
     * @return The line number in the XML file.
     */
    public int getLineNumber()
    {
        return line;
    }
    
    /**
     * Get the column number this entry starts at.
     *
     * @return The column number in the XML file.
     */
    public int getColumnNumber()
    {
        return column;
    }
        
    /**
     * Get the list of exceptions that occurred while creating the term entry.
     *
     * @return A non-modifiable list of exceptions that occurred in the order
     *  each occurred. If no exceptions occurred then this list will be
     *  empty.
     */
    public List<TBXException> getExceptions()
    {
        return exceptions;
    }
}

