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

import java.io.PrintWriter;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXException extends Exception implements Comparable
{
    /*
     */
    
    /**
     */
    public enum Priority
    {
        /** */
        PRE_PARSE("preparse"),

        /** */
        WELLFORMED("wellformed"),

        /** */
        XMLVALID_MAJOR("invalidMajor"),

        /** */
        XMLVALID_MINOR("invalidMinor"),

        /** */
        XCS("xcs");
        
        /** */
        private final String type;
        
        /**
         * @param t Printable name for the enumeration.
         */
        private Priority(String t)
        {
            type = t;
        }

        /** {@inheritDoc} */
        public String toString()
        {
            ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.TBXException");
            return bundle.getString(type);
        }
    }

    /** SCM information. */
    private static final String RCSID = "$Id$";

    /** */
    private static int sequenceNext;
    
    /** */
    private final Priority priority;
    
    /** */
    private final int sequence = sequenceNext++;
        
    /**
     * @param p The priority of this exception.
     * @param c The cause (which is saved for later retrieval by the
     *  ({@link java.lang.Throwable#getCause} method).
     */
    public TBXException(Priority p, Exception c)
    {
        super(c);
        priority = p;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return "org.ttt.salt.TBXFile.TBXError: " + getMessage();
    }
        
    /** {@inheritDoc} */
    public String getMessage()
    {
        /*
         * This looks almost like getLocalizedMessage, but it is not.
         * getLocalizedMessage message will call getLocalizedMessage
         * on the embeded exception whereas this call getMessage. This
         * is to allow for different processing.
         *
         * I may be able to pull the common stuff out of here an place
         * it into a method, but at this time it is too complex an
         * operation to pull off without changing how the entire method
         * is built.
         */
        //CHECKSTYLE: MagicNumber OFF
        String ret = priority.toString();
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.TBXException", Locale.US);
        Object[] args = new Object[5];
        if (getCause() instanceof SAXParseException)
        {
            SAXParseException serr = (SAXParseException) getCause();
            args[0] = new Integer(serr.getLineNumber());
            args[1] = new Integer(serr.getColumnNumber());
            args[2] = serr.getMessage();
            args[3] = (serr.getException() == null) ? "NONE" : serr.getException().getMessage();
            ret += MessageFormat.format(bundle.getString("SAXParseException"), args);
        }
        else if (getCause() instanceof SAXException)
        {
            SAXException serr = (SAXException) getCause();
            args[0] = serr.getMessage();
            args[1] = (serr.getException() == null) ? "NONE" : serr.getException().getMessage();
            ret += MessageFormat.format(bundle.getString("SAXException"), args);
        }
        else if (getCause() instanceof XCSValidationException)
        {
            args[0] = getCause().getMessage();
            ret += MessageFormat.format(bundle.getString("XCSValidationException"), args);
        }
        else
        {
            args[0] = getCause();
            ret += MessageFormat.format(bundle.getString("UnknownException"), args);
            printStackTrace();
        }
        return ret.toString();
        //CHECKSTYLE: MagicNumber ON
    }
    
    /** {@inheritDoc} */
    public String getLocalizedMessage()
    {
        /*
         * See notes in getMessage about the differences between this
         * method and getMessage. They should be kept in sync, but
         * don't try to combine into a single call unless you understand
         * what is happening.
         */
        //CHECKSTYLE: MagicNumber OFF
        String ret = priority.toString();
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.TBXException");
        Object[] args = new Object[5];
        if (getCause() instanceof SAXParseException)
        {
            SAXParseException serr = (SAXParseException) getCause();
            args[0] = new Integer(serr.getLineNumber());
            args[1] = new Integer(serr.getColumnNumber());
            args[2] = serr.getLocalizedMessage();
            args[3] = (serr.getException() == null) ? "" : serr.getException().getLocalizedMessage();
            ret += MessageFormat.format(bundle.getString("SAXParseException"), args);
        }
        else if (getCause() instanceof SAXException)
        {
            SAXException serr = (SAXException) getCause();
            args[0] = serr.getLocalizedMessage();
            args[1] = (serr.getException() == null) ? "" : serr.getException().getLocalizedMessage();
            ret += MessageFormat.format(bundle.getString("SAXException"), args);
        }
        else if (getCause() instanceof XCSValidationException)
        {
            args[0] = getCause().getLocalizedMessage();
            ret += MessageFormat.format(bundle.getString("XCSValidationException"), args);
        }
        else
        {
            args[0] = getCause();
            ret += MessageFormat.format(bundle.getString("UnknownException"), args);
            printStackTrace();
        }
        return ret.toString();
        //CHECKSTYLE: MagicNumber ON
    }

    /** {@inheritDoc} */
    public void printStackTrace(PrintStream out)
    {
        getCause().printStackTrace(out);
    }
    
    /** {@inheritDoc} */
    public void printStackTrace(PrintWriter out)
    {
        getCause().printStackTrace(out);
    }

    /** {@inheritDoc} */
    public int compareTo(Object o)
    {
        TBXException p = (TBXException) o;
        int ret = priority.compareTo(p.priority);
        if (ret == 0)
            ret = sequence - p.sequence;
        return ret;
    }
}

