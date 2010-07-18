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

import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This will look at an input stream and determine the Unicode Transformation
 * Format and build itself as a {@link java.io.Reader} that will properly
 * transform the stream.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class UTFStreamReader extends Reader
{
    /** The character set that represents the streams tranform format.*/
    private Charset format;
    
    /** The input stream reader that backs this reader. */
    private InputStreamReader insr;
    
    /**
     * Create a new UTFStreamReader from the input stream.
     *
     * @param in The input stream.
     */
    public UTFStreamReader(InputStream in) throws IOException
    {
        super();
        if (!in.markSupported())
            in = new BufferedInputStream(in);
        String fmt = getTransformationFormat(in);
        format = Charset.forName(fmt);
        insr = new InputStreamReader(in, format);
    }
    
    /** {@inheritDoc} */
    public void close() throws IOException
    {
        insr.close();
    }

    /** {@inheritDoc} */
    public void mark(int readAheadLimit) throws IOException
    {
        insr.mark(readAheadLimit);
    }
    
    /** {@inheritDoc} */
    public boolean markSupported()
    {
        return insr.markSupported();
    }
    
    /** {@inheritDoc} */
    public int read() throws IOException
    {
        return insr.read();
    }
    
    /** {@inheritDoc} */
    public int read(char[] cbuf) throws IOException
    {
        return insr.read(cbuf);
    }
    
    /** {@inheritDoc} */
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        return insr.read(cbuf, off, len);
    }
    
    /** {@inheritDoc} */
    public int read(CharBuffer target) throws IOException
    {
        return insr.read(target);
    }
    
    /** {@inheritDoc} */
    public boolean ready() throws IOException
    {
        return insr.ready();
    }
    
    /** {@inheritDoc} */
    public void reset() throws IOException
    {
        insr.reset();
    }
    
    /** {@inheritDoc} */
    public long skip(long len) throws IOException
    {
        return insr.skip(len);
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
    private String getTransformationFormat(InputStream input)
            throws UnsupportedEncodingException, IOException
    {
        //CHECKSTYLE: MagicNumber OFF
        input.mark(16);
        int b0 = input.read();
        int b1 = input.read();
        int b2 = input.read();
        int b3 = input.read();
        input.reset();
                
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

