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

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.w3c.dom.Element;
import org.ttt.salt.dom.tbx.TBXElement;

/**
 * Gives details about XCS validation problems.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class XCSValidationException extends Exception
{
    /*
     */
    
    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private final Element elem;
    
    /**
     * @param e The XML element this exception occured on.
     */
    public XCSValidationException(Element e)
    {
        super();
        elem = e;
    }
    
    /**
     * Get the element that caused this exception.
     * @return The XML element this exception occured on.
     */
    protected Element element()
    {
        return elem;
    }

    /** {@inheritDoc} */
    public String getMessage()
    {
        return "XCSValidationException " + buildContext();
    }

    /** {@inheritDoc} */
    public String getLocalizedMessage()
    {
        //TODO--localize the message
        return getMessage();
    }
    
    /**
     * Build an error context string for use in producing a message.
     * This will give information so users can find the location of
     * the error in large files.
     *
     * @return A human readable context for this error.
     */
    protected String buildContext()
    {
        //CHECKSTYLE: MagicNumber OFF
        String ret;
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.XCSValidationException");
        Element term = elem;
        while (!term.getTagName().equals("termEntry"))
            term = (Element) term.getParentNode();
        Object[] args = new Object[4];
        args[2] = elem;
        args[3] = (elem instanceof TBXElement) ? ((TBXElement) elem).getLocationString() : "Line: ??";
                
        String id = term.getAttribute("id");
        if (id.equals("") || id.startsWith(TBXFile.AUTO_TERMENTRY_ID_PREFIX))
        {
            args[0] = "TODO"; //NOTDONE
            args[1] = "TODO"; //NOTDONE
            args[2] = elem;
            ret = MessageFormat.format(bundle.getString("NoTermEntry"), args);
        }
        else
        {
            args[0] = id;
            ret = MessageFormat.format(bundle.getString("TermEntry"), args);
        }
        return ret;
        //CHECKSTYLE: MagicNumber ON
    }
}
