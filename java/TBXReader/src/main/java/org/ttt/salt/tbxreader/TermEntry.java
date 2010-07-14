/*
 * Copyright 2010 Lance Finn Helsten (helsten@acm.org)
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
    /** The reader that generated this object. */
    private TBXReader reader;
    
    /** The location in the XML file where this term entry starts. */
    private Locator locator;
        
    /** The list of exceptions that have occurred in building this term entry. */
    private List<TBXException> exceptions = new java.util.ArrayList<TBXException>();
    
    /**
     * Create a new term entry.
     *
     * @param loc The Locator in the XML this term entry is starting at.
     */
    public TermEntry(Locator loc)
    {
        locator = loc;
    }
    
    /**
     * Get the XML location where this term entry started.
     *
     * @return The XML location.
     */
    public Locator getLocation()
    {
        return locator;
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
        return java.util.Collections.unmodifiableList(exceptions);
    }
}

