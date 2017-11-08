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

import org.xml.sax.Locator;

/**
 * This is the base class for all TBX exceptions that can occur when parsing,
 * and validating a TBX file.
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

