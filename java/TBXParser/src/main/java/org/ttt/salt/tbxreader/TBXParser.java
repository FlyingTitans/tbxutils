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

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

/**
 * This is used by TBXReader to do the parsing of the TBX XML stream.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
class TBXParser implements ContentHandler
{    
    /** Logger for all classes in this package. */
    static final Logger LOGGER = Logger.getLogger("org.ttt.salt.tbxparser");
    
    /** URL to the TBX file to be parsed. */
    private URL url;
    
    /** Properties for this parser. */
    private Map<String, Object> properties;
    
    /**
     * Create a new TBX parser.
     *
     * @param tbx URL to the TBX file to process.
     */
    public TBXParser(URL tbx)
    {
        url = tbx;
        properties = new java.util.HashMap<String, Object>();
    }
    
    /**
     * Get the value of a property on this parser to control the parsing,
     * validation, and conformance checking.
     *
     * @param name The name of the property to get. If the name is not known
     *  by the parser then an {@link java.lang.IllegalArgumentException} will
     *  be thrown.
     * @return The value of the named property.
     */
    public Object getProperty(String name)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Set a property on this parser to control the parsing, validation, and
     * conformance checking.
     *
     * @param name The name of the property to set. If the name is not known
     *  by the parser then an {@link java.lang.IllegalArgumentException} will
     *  be thrown.
     * @param value The new value of the property. If the value is invalid for
     *  the parser then an {@link java.lang.IllegalArgumentException} will
     *  be thrown. If the value is not an appropriate type for the property
     *  then an {@link java.lang.ClassCastException} will be thrown.
     */
    public void setProperty(String name, Object value)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get the {@link MartifHeader} for the TBX file's <code>martifHeader</code>
     * element.
     *
     * @return The martif header object.
     * @throws TBXException Contains the information on why the martif
     *  header could not be returned.
     */
    public MartifHeader getMartifHeader() throws TBXException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get the next {@link TermEntry} for the next <code>termEntry</code>
     * element in the TBX file.
     *
     * @return The next term object.
     * @throws TBXException Contains the information on why the next term
     *  entry could not be returned.
     */
    public TermEntry getNextTermEntry() throws TBXException
    {
        throw new UnsupportedOperationException();
    }
}

