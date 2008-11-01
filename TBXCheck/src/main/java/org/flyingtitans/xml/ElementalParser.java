/*
 * $Id$
 *-----------------------------------------------------------------------------
 * Copyright (C) 1997-2000 Lance Finn Helsten (helsten@acm.org)
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.flyingtitans.xml;

import java.io.*;
import java.util.*;


/**
 * This will parse an XML document into elemental tokens.
 * <p>
 * The elemental tokens are XML Declaration, Comment, Processing Instruction,
 * DOCTYPE Declaration, Start Tag, End Tag, Empty Tag, and Content.</p>
 * <p>
 * There is no checking that the internal structure of the tag is well-formed,
 * and whitespace is not collapsed at this time.</p>
 * <p>
 * It is assumed that the input reader is setup correctly to handle the input
 * dataset (i.e. a UTF-16 file has been checked for '0xFEFF' marker).</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class ElementalParser
{
    /*
     */
    /** SCM information. */
    public static final String RCSID = "$Id$";

    /**
     * Defines elemental token that was processed.
     *
     * @author Lance Finn Helsten
     * @version $Id$
     */
    public enum Token
    {
        /** There is no token data. */
        NONE,
        
        /** Type of token when in the &lt;xml?...?&gt; declaration. */
        XMLDECL,
        
        /** Type of token when in a comment. */
        COMMENT,
        
        /** Type of token when in processing instruction. */
        PI,
        
        /** Type of token for the &lt;!DOCTYPE section. */
        DOCTYPEDECL,
        
        /** Type of token when in CDATA block. */
        CDATA,
        
        /** Type of token for the start of an element. */
        START,
        
        /** Type of token for the end of an element. */
        END,

        /** Type of token for an empty element. */
        EMPTY,

        /** Type of token for the contents of an element. */
        CONTENT,
    
        /** Type of token when end of file is reached. */
        EOF,
        
        /** Type of token when an malformed construct is encountered. */
        ILLFORMED;
    }

    /** Type of token that was read by last parser action. */
    public Token type = Token.NONE;

    /** The string value of the last token read. */
    public String sval = "";

    /** The reader to read the XML data for parsing. */
    private Reader reader;

    /** */
    private int c;

    /** */
    private final StringBuffer buf = new StringBuffer();

    /**
     * Create a new elemental parser.
     *
     * @param in The file stream to parse from.
     * @throws IOException Any I/O exceptions during processing.
     */
    public ElementalParser(InputStream in) throws IOException
    {
        if (!in.markSupported())
            in = new BufferedInputStream(in);
        in.mark(16);

        int b0 = in.read();
        int b1 = in.read();
        in.reset();
    
        //TODO: This needs to be more robust. The UTF-8 BOM, and UTF-32 BOM
        //needs to be handled.
        if ((b0 == 0xfe) && (b1 == 0xff))
            reader = new InputStreamReader(in, "UTF-16");
        else
            reader = new InputStreamReader(in, "UTF-8");
    }

    /**
     * Create a new elemental parser.
     *
     * @param rdr The file reader to parse from.
     * @throws IOException Any I/O exceptions during processing.
     */
    public ElementalParser(Reader rdr) throws IOException
    {
        reader = rdr;
    }

    /**
     * Get the next token from the stream.
     *
     * @return The token type of the current parse token.
     * @throws IOException Any I/O exceptions during processing.
     */
    public Token next() throws IOException
    {
        buf.setLength(0);
        read();

        if ((c == -1) && (sval.length() == 0))
            type = Token.EOF;
        else if (sval.startsWith("<?xml") && sval.endsWith("?>"))
            type = Token.XMLDECL;
        else if (sval.startsWith("<?") && sval.endsWith("?>"))
            type = Token.PI;
        else if (sval.startsWith("<?"))
            type = Token.ILLFORMED;
        else if (sval.startsWith("<!--"))
            readToEndOfComment();
        else if (sval.startsWith("<!DOCTYPE") && sval.endsWith(">"))
            type = Token.DOCTYPEDECL;
        else if (sval.startsWith("<![CDATA["))
            readToEndOfCDATA();
        else if (sval.startsWith("<!"))
            type = Token.ILLFORMED;
        else if (sval.startsWith("</") && sval.endsWith(">"))
            type = Token.END;
        else if (sval.startsWith("<") && sval.endsWith("/>"))
            type = Token.EMPTY;
        else if (sval.startsWith("<") && sval.endsWith(">"))
            type = Token.START;
        else if (sval.startsWith("<"))
            type = Token.ILLFORMED;
        else
            type = Token.CONTENT;

        return type;
    }

    /**
     * Read a single character.
     *
     * @throws IOException Any I/O exceptions during processing.
     */
    private void read() throws IOException
    {
        if (c == 0)
            c = reader.read();

        if (c != -1)
        {
            char stop = (c == '<') ? '>' : '<';
            buf.append((char) c);
            c = reader.read();
            while ((c != -1) && (c != stop))
            {
                buf.append((char) c);
                c = reader.read();
            }

            if (stop == '>')
            {
                buf.append('>');
                c = reader.read();
            }
        }
        sval = buf.toString();
    }

    /**
     * Read a comment until reaching the "-->" terminator.
     *
     * @throws IOException Any I/O exceptions during processing.
     */
    private void readToEndOfComment() throws IOException
    {
        type = Token.COMMENT;
        while ((c != -1) && !sval.endsWith("-->"))
        {
            while ((c != -1) && (c != '>'))
            {
                buf.append((char) c);
                c = reader.read();
            }
            buf.append('>');
            c = reader.read();
            sval = buf.toString();
        }
        sval = buf.toString();
    }

    /**
     * Read CDATA until reaching the "]]>" terminator.
     *
     * @throws IOException Any I/O exceptions during processing.
     */
    private void readToEndOfCDATA() throws IOException
    {
        type = Token.CDATA;
        while ((c != -1) && !sval.endsWith("]]>"))
        {
            while ((c != -1) && (c != '>'))
            {
                buf.append((char) c);
                c = reader.read();
            }
            buf.append('>');
            c = reader.read();
        }
        sval = buf.toString();
    }
}
