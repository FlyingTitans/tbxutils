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
package org.ttt.salt.gui;

import java.io.InputStream;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public final class ISOReference
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /**
     */
    public final class ISO639 implements Comparable<ISO639>
    {
        /** The ISO 639 alpha2 code for the language. */
        private final String alpha2;
        
        /** The ISO 639 alpha3 code for the language. */
        private final String alpha3;
        
        /** The ISO 639 english name for the language. */
        private final String name;
        
        /**
         * @param a2 The ISO 639 alpha 2 code.
         * @param a3 The ISO 639 alpha 3 code.
         * @param n The associated english name.
         */
        private ISO639(String a2, String a3, String n)
        {
            alpha2 = a2;
            alpha3 = a3;
            name = n;
        }
        
        /**
         * Get the ISO 639 alpha 2 language code.
         *
         * @return The ISO 639 alpha2 language code.
         */
        public String getAlpha2()
        {
            return alpha2;
        }
        
        /**
         * Get the ISO 639 alpha 3 language code.
         *
         * @return The ISO 639 alpha3 language code.
         */
        public String getAlpha3()
        {
            return alpha3;
        }
        
        /**
         * Get the english name for this language.
         *
         * @return The english name for this language.
         */
        public String getName()
        {
            return name;
        }
        
        /** {@inheritDoc} */
        public String toString()
        {
            return String.format("%3s | %4s | %s", alpha2, alpha3, name);
        }
        
        /** {@inheritDoc} */
        public int compareTo(ISO639 o)
        {
            return alpha2.compareTo(o.alpha2);
        }
    }

    /**
     */
    public final class ISO3166 implements Comparable<ISO3166>
    {
        /** The ISO 3166 alpha2 code for the country. */
        private final String alpha2;
        
        /** The ISO 3166 alpha3 code for the country. */
        private final String alpha3;
        
        /** The ISO 3166 english name for the language. */
        private final String name;
        
        /**
         * @param a2 The ISO 3166 alpha 2 code.
         * @param a3 The ISO 3166 alpha 3 code.
         * @param n The associated english name.
         */
        private ISO3166(String a2, String a3, String n)
        {
            alpha2 = a2;
            alpha3 = a3;
            name = n;
        }
        
        /**
         * Get the ISO 3166 alpha 2 country code.
         *
         * @return The ISO 3166 alpha2 country code.
         */
        public String getAlpha2()
        {
            return alpha2;
        }
        
        /**
         * Get the ISO 3166 alpha 3 country code.
         *
         * @return The ISO 3166 alpha3 country code.
         */
        public String getAlpha3()
        {
            return alpha3;
        }
        
        /**
         * Get the english name for this country.
         *
         * @return The english name for this country.
         */
        public String getName()
        {
            return name;
        }
        
        /** {@inheritDoc} */
        public int compareTo(ISO3166 o)
        {
            return alpha2.compareTo(o.alpha2);
        }
    }
    
    /** Single instance of this class. */
    private static final ISOReference INSTANCE = new ISOReference();
    
    /**
     * Get the single class instance.
     *
     * @return Singleton instance of this class.
     */
    public static ISOReference getInstance()
    {
        return INSTANCE;
    }
    
    /** ISO 639 information. */
    private SortedSet<ISO639> iso639;
    
    /** ISO 3166 information. */
    private SortedSet<ISO3166> iso3166;
    
    /**
     */
    private ISOReference()
    {
    }
    
    /**
     * Get the ISO 639 set of information.
     * <p>
     * <strong>CAUTION</strong> If any I/O Exceptions occur this will still
     * succeed, but will be incomplete or even empty. The details will be
     * in the org.ttt.salt.gui logger.</p>
     *
     * @return Set of ISO639 objects.
     */
    public SortedSet<ISO639> get639()
    {
        if (iso639 == null)
        {
            try
            {
                iso639 = new java.util.TreeSet<ISO639>();
                LineNumberReader rdr = getResourceReader("ISO639.txt");
                String line = rdr.readLine();
                while (line != null)
                {
                    if (!line.matches("^\\s*(#.*)?"))
                    {
                        String[] parts = line.split("\\|");
                        String alpha2 = parts[0].trim();
                        String alpha3 = parts[1].trim();
                        String name = parts[2].trim();
                        ISO639 o = new ISO639(alpha2, alpha3, name);
                        iso639.add(o);
                    }
                    line = rdr.readLine();
                }
            }
            catch (IOException err)
            {
                Logger.getLogger("org.ttt.salt.gui").log(Level.WARNING, "ISO 639 table is invalid.", err);
            }
        }
        return iso639;
    }
    
    /**
     * Get the ISO 3166 set of information.
     * <p>
     * <strong>CAUTION</strong> If any I/O Exceptions occur this will still
     * succeed, but will be incomplete or even empty. The details will be
     * in the org.ttt.salt.gui logger.</p>
     *
     * @return Set of ISO3166 objects.
     */
    public SortedSet<ISO3166> get3166()
    {
        if (iso3166 == null)
        {
            try
            {
                iso3166 = new java.util.TreeSet<ISO3166>();
                LineNumberReader rdr = getResourceReader("ISO3166.txt");
                String line = rdr.readLine();
                while (line != null)
                {
                    if (!line.matches("^\\s*(#.*)?"))
                    {
                        String[] parts = line.split("\\|");
                        String alpha2 = parts[0].trim();
                        String alpha3 = parts[1].trim();
                        String name = parts[2].trim();
                        ISO3166 o = new ISO3166(alpha2, alpha3, name);
                        iso3166.add(o);
                    }
                    line = rdr.readLine();
                }
            }
            catch (IOException err)
            {
                Logger.getLogger("org.ttt.salt.gui").log(Level.WARNING, "ISO 3166 table is invalid.", err);
            }
        }
        return iso3166;
    }
    
    /**
     * Get the resource file as a configured LineNumberReader.
     *
     * @param name The name of the resource to load.
     * @return The LineNumberReader built from the resource.
     * @throws IOException Any I/O Exception that occurs in construction.
     */
    private LineNumberReader getResourceReader(String name) throws IOException
    {
        InputStream instm = getClass().getResourceAsStream(name);
        if (instm == null)
            throw new IOException("Resource not found: " + name);
        Reader in = new java.io.InputStreamReader(instm, "UTF-8");
        LineNumberReader ret = new LineNumberReader(in);
        return ret;
    }
    
    /**
     * Get the ISO 639-1 alpha2 codes.
     * <p>
     * This will only return those languages that have an ISO 639 alpha2
     * code.</p>
     *
     * @return SortedSet of ISO 639 alpha2 codes.
     */
    public SortedSet<String> get639alpha2()
    {
        SortedSet<String> ret = new java.util.TreeSet<String>();
        SortedSet<ISO639> set = get639();
        Iterator<ISO639> iter = set.iterator();
        while (iter.hasNext())
        {
            ISO639 o = iter.next();
            if (o.alpha2.equals(""))
                ret.add(o.alpha2);
        }
        return ret;
    }
    
    /**
     * Get the ISO 3166 alpha2 codes.
     * <p>
     * This will only return those languages that have an ISO 3166 alpha2
     * code.</p>
     *
     * @return SortedSet of ISO 3166 alpha2 codes.
     */
    public SortedSet<String> get3166alpha2()
    {
        SortedSet<String> ret = new java.util.TreeSet<String>();
        SortedSet<ISO3166> set = get3166();
        Iterator<ISO3166> iter = set.iterator();
        while (iter.hasNext())
        {
            ISO3166 o = iter.next();
            if (o.alpha2.equals(""))
                ret.add(o.alpha2);
        }
        return ret;
    }
}

