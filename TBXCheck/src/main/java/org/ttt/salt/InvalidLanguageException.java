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
package org.ttt.salt;

import org.w3c.dom.Element;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class InvalidLanguageException extends XCSValidationException
{
    /*
     */
    
    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /**
     * @param elem The XML element this exception occured on.
     */
    public InvalidLanguageException(Element elem)
    {
        super(elem);
    }
    
    /** {@inheritDoc} */
    public String getMessage()
    {
        String lang = null;
        if (element().hasAttribute("xml:lang"))
            lang = element().getAttribute("xml:lang");
        else if (element().hasAttribute("lang"))
            lang = element().getAttribute("lang");
        return "Invalid language for tag: " + lang + " at " + buildContext();
    }

    /** {@inheritDoc} */
    public String getLocalizedMessage()
    {
        //TODO--localize the message
        return getMessage();
    }
}
