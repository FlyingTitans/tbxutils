/*
 * TermBase eXchange conformance checker library.
 * Copyright (C) 2010 Lance Finn Helsten (helsten@acm.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

