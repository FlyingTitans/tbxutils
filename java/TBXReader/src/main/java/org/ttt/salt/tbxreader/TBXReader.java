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

import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * TBXReader is the system used to read TBX files and check to see if it
 * is XML well-formed, XML valid against schema, and XCS conformant.
 *
 * <h4>Properties</h4>
 * <ul>
 *   <li><strong>tbx:EntityResolver</strong>: A custom entity resolver for use
 *     while parsing the TBX and XCS files. This must conform to the
 *     {@link org.xml.sax.EntityResolver} interface. If the given resolver
 *     is not set or if a resolution fails then the built in resolver will
 *     be used.</li>
 * </ul>
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXReader
{
    /** Logger for all classes in this package. */
    static final Logger LOGGER = Logger.getLogger("org.ttt.salt.tbxreader");
    
    /** The SAX parser factory that is used to generate all XML parsers. */
    private static SAXParserFactory saxfactory;
    
    static
    {
        saxfactory = SAXParserFactory.newInstance();
        saxfactory.setNamespaceAware(true);
    }
    
    /**
     * Get an XML parser that is properly configured to parse a TBX file.
     */
    static SAXParser getTBXFileSAXParser()
    {
        SAXParser ret;
        synchronized (saxfactory)
        {
            try
            {
                //TODO: set the validation state for the parser
                //TODO: set the schema for the parser
                ret = saxfactory.newSAXParser();
            }
            catch (ParserConfigurationException err)
            {
                throw new RuntimeException("Unable to initialize XML parser.", err);
            }
            catch (SAXException err)
            {
                throw new RuntimeException("Unable to initialize XML parser.", err);
            }
        }
        return ret;
    }
    
    /** Properties for this parser. */
    private Map<String, Object> properties;
    
    /**
     * 
     */
    private TBXReader()
    {
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
}

