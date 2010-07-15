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

import org.xml.sax.Locator;

/**
 * This is the base class for all TBX exceptions that can occur when parsing,
 * validating, and checking conformance of a TBX file.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXParseException extends TBXException
{
    /** The line where the parse exception occurred. */
    private int line;
    
    /** The column where the parse exception occurred. */
    private int column;

    /**
     */
    public TBXParseException(Locator locator, String message)
    {
        super(message);
        line = locator.getLineNumber();
        column = locator.getColumnNumber();        
    }
    
    /**
     */
    public TBXParseException(Locator locator, String message, Throwable cause)
    {
        super(message, cause);
        line = locator.getLineNumber();
        column = locator.getColumnNumber();        
    }
    
    /**
     */
    public TBXParseException(Locator locator, Throwable cause)
    {
        super(cause);
        line = locator.getLineNumber();
        column = locator.getColumnNumber();        
    }

    /**
     * Get the line number the exception occurred.
     *
     * @return The line number in the XML file.
     */
    public int getLineNumber()
    {
        return line;
    }
    
    /**
     * Get the column number the exception occurred.
     *
     * @return The column number in the XML file.
     */
    public int getColumnNumber()
    {
        return column;
    }
}

