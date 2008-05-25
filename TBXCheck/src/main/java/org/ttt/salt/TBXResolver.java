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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * This will handle resolution of PUBLIC names and SYSTEM names.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXResolver implements EntityResolver
{
    
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt");
    
    /** Localized resource bundle to be used for this object. */
    private final Map<String, String> uri2url = new java.util.HashMap<String, String>();
    
    /** Current working directory to use when resolving relative paths. */
    private String currentDir;

    /**
     */
    public TBXResolver()
    {
        this(System.getProperty("user.dir"));
    }
    
    /**
     * @param cwd The current working directory for relative path resolution.
     */
    public TBXResolver(String cwd)
    {
        currentDir = cwd;
        addPublicId("ISO 12200:1999A//DTD MARTIF core (DXFcdV04)//EN", "/xml/XLTCDV04.DTD");
        addPublicId("Demo XCS", "/xml/TBXDCSv05.xml");
    }
    
    /**
     * Add a PUBLIC name to the resolver built in mapping.
     *
     * @param publicId The PUBLIC ID of the entity.
     * @param systemId The SYSTEM ID of the entity: uri or url.
     */
    public void addPublicId(String publicId, String systemId)
    {
        uri2url.put(publicId, systemId);
    }

    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
        InputStreamReader reader = null;
        try
        {
            InputStream input;
            if (uri2url.containsKey(publicId))
            {   //Check to see if it is a named resource
                LOGGER.info("Mapped URI to URL publicId: " + publicId);
                input = getClass().getResourceAsStream(uri2url.get(publicId));
            }
            else if (systemId.matches("\\w+:.+"))
            {   //Do the full URI parsing
                LOGGER.info("Valid URI systemId: " + systemId);
                URI uri = new URI(systemId);
                input = uri.toURL().openStream();
            }
            else
            {   //Check the relative URI location
                LOGGER.info("Unknown schema systemId: " + systemId);
                if (systemId.startsWith("/"))
                {
                    LOGGER.info("Absolute path systemId: " + systemId);
                    File file = new File(systemId);
                    if (file.exists())
                        input = new FileInputStream(file);
                    else
                        input = getClass().getResourceAsStream(systemId);
                }
                else
                {
                    LOGGER.info("Relative path systemId: " + systemId);
                    File file = new File(currentDir, systemId);
                    LOGGER.info("Resolved path: " + file.toString());
                    input = new FileInputStream(file);
                }
                
                if (input == null)
                    throw new FileNotFoundException();
            }
            if (!input.markSupported())
                input = new java.io.BufferedInputStream(input);
            String enc = TBXResolver.getEncoding(input);
            reader = new InputStreamReader(input, enc);
        }
        catch (FileNotFoundException err)
        {
            String msg = String.format("Entity could not be resolved:\n  PUBLIC: '%s'\n  SYSTEM: '%s'\n  BUILT IN: %s",
                publicId, systemId,
                uri2url.containsKey(publicId) ? uri2url.get(publicId) : "NONE");
            throw new FileNotFoundException(msg);
        }
        catch (UnsupportedEncodingException err)
        {
            throw new UnsupportedEncodingException(String.format("PUBLIC %s SYSTEM %s", publicId, systemId));
        }
        catch (URISyntaxException err)
        {
            throw new SAXException("Invalid System ID format", err);
        }
        Logger.getLogger("org.ttt.salt").log(Level.INFO, "EntityResolved", publicId);
        return new InputSource(reader);
    }


    /**
     * Find out if the file is UTF-16 or UTF-8 encoded. If there is no
     * Byte Order Mark (BOM) at the start of the file, but the "<?xml"
     * processing instruction exists then it is assumed the file is a
     * UTF-8 encoded file.
     *
     * @param input Stream to read the file encoding from.
     * @return The file encoding: UTF-16 or UTF-8.
     * @throws UnsupportedEncodingException The file encoding could not be
     *  determined. If the file is UTF-32 encoded this will recognize the
     *  encoding, but still throw this exception.
     * @throws IOException Any unhandled I/O exceptions.
     */
    //CHECKSTYLE: RedundantThrows unchecked OFF
    public static String getEncoding(InputStream input)
            throws UnsupportedEncodingException, IOException
    {
        //CHECKSTYLE: MagicNumber OFF
        input.mark(16);
        int b0 = input.read();
        int b1 = input.read();
        int b2 = input.read();
        int b3 = input.read();
        int b4 = input.read();
        input.reset();
        
        int b1utf = b1 & 0xC0;
        int b2utf = b2 & 0xC0;
        int b3utf = b3 & 0xC0;
        
        String ret = null;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF)
        {   //UTF-8 BOM eliminate the first three bytes
            b0 = input.read();
            b1 = input.read();
            b2 = input.read();
            ret = "UTF-8";
        }
        else if (
               ((b0 & 0x80) == 0x00)                                //UTF-8 single byte
            || ((b0 & 0xE0) == 0xC0 && (b1 & 0xC0) == 0x80)         //UTF-8 two bytes
            || ((b0 & 0xF0) == 0xE0 && (b1 & 0xC0) == 0x80
                    && (b2 & 0xC0) == 0x80)                         //UTF-8 three bytes
            || ((b0 & 0xF8) == 0xF0 && (b1 & 0xC0) == 0x80
                    && (b2 & 0xC0) == 0x80 && (b2 & 0xC0) == 0x80)  //UTF-8 four bytes
            )
        {   //Check that first character is valid UTF-8 encoded then assume UTF-8
            ret = "UTF-8";
        }
        else if (b0 == 0xfe && b1 == 0xff)
        {
            b0 = input.read();
            b1 = input.read();
            ret = "UTF-16BE";
        }
        else if (b0 == 0xff && b1 == 0xfe && b2 != 0x00)
        {
            b0 = input.read();
            b1 = input.read();
            ret = "UTF-16LE";
        }
        else if (b0 == 0x00 && b1 == 0x00 && b2 == 0xfe && b3 == 0xff)
        {   //BUGBUG: This will probably be interpreted as UTF-8
            ret = "UTF-32BE";
            if (!java.nio.charset.Charset.isSupported(ret))
                throw new UnsupportedEncodingException("UTF-32BE encoding is unsupported.");
        }
        else if (b0 == 0xff && b2 == 0xfe && b2 == 0x00 && b3 == 0x00)
        {
            ret = "UTF-32LE";
            if (!java.nio.charset.Charset.isSupported(ret))
                throw new UnsupportedEncodingException("UTF-32LE encoding is unsupported.");
        }
        else
        {
            throw new UnsupportedEncodingException("Unknown file encoding");
        }
        return ret;
        //CHECKSTYLE: MagicNumber ON
    }
    //CHECKSTYLE: RedundantThrows unchecked ON
}

