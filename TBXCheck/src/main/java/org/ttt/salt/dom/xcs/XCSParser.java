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
package org.ttt.salt.dom.xcs;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.Map;
import java.util.Stack;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.w3c.dom.Text;
import org.w3c.dom.DOMException;
import org.ttt.salt.XCSDocument;
import org.ttt.salt.TBXResolver;

/**
 * This handler will build a {@link org.w3c.dom.Document} from a well-formed
 * valid TBX XCS document.
 * <p>
 * The principle purpose of using SAX2 to do this instead of the standard
 * DOM methods is this will allow for better error reporting of problems in
 * the XCS file. It will also allow the error messages to be localized here
 * and not require an external localization.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class XCSParser
    implements ContentHandler, DTDHandler, EntityResolver, ErrorHandler
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /**
     * Known missing element sub-classes of XCSElement. These elements are
     * in the XCS, but there is not special handling necessary other than
     * that done in XCSElement. They are placed here to reduce the logging
     * that these elements are missing.
     */
    public static final Set<String> KNOWN_MISSING = new java.util.TreeSet<String>(
            java.util.Arrays.asList(
                "header", "title", "languages", "langInfo", "langCode", "langName",
                "datCatSet", "datCatDoc", "datCatMap", "datCatToken", "datCatDisplay",
                "termNoteSpec", "descripSpec", "adminSpec",
                "descripNoteSpec", "xrefSpec", "refSpec", "termCompListSpec",
                "termCompListSpec", "transacNoteSpec", "adminNoteSpec",
                "transacSpec", "hiSpec",
                "refObjectDefSet", "refObjectDef", "refObjectType",
                "contents", "levels", "itemSpec", "itemSpecSet"
            ));

    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.dom.xcs");
        
    /** The {@link org.xml.sax.XMLReader} this parser works through. */
    private XMLReader reader;
    
    /** The entity resolver that I use. */
    private TBXResolver resolver;
    
    /** Namespace mappings. */
    private Map<String, URI> namespace = new java.util.HashMap<String, URI>();
    
    /** The XCSElement that represents the entire document. */
    private XCSDocument document;
    
    /** The current XCSElement being built. */
    private XCSElement current;
    
    /** The stack of all XCSElement objects that have been created. */
    private Stack<XCSElement> stack = new Stack<XCSElement>();
    
    /** The current {@link org.xml.sax.Locator} for tracking elements. */
    private Locator locator;
    
    /** Indicates that whitespace is not significant (xml:space="default"). */
    private boolean collapseWhitespace;
    
    /**
     * Create a new parser for XCS document parsing.
     *
     * @param r The XML entity resolver the parser should use.
     * @throws SAXNotRecognizedException Requried parser feature is unavailable.
     * @throws SAXNotSupportedException Could not set required parser feature.
     */
    public XCSParser(TBXResolver r) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        resolver = r;
        try
        {
            reader = new org.apache.xerces.parsers.SAXParser();
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            reader.setEntityResolver(resolver);
            reader.setErrorHandler(this);
        }
        catch (SAXNotRecognizedException err)
        {
            LOGGER.log(Level.SEVERE, "Required parser feature is unavailable.", err);
            throw err;
        }
        catch (SAXNotSupportedException err)
        {
            LOGGER.log(Level.SEVERE, "Could not set required parser feature.", err);
            throw err;
        }
    }
    
    /**
     * Parse the input source into a new created XCSDocument.
     *
     * @param src The input source to read the XCS XML data.
     * @return The XCS document created from this parse pass.
     * @throws SAXException All exceptions with reading and parsing the XCS.
     * @throws IOException All I/O exceptions that occur.
     */
    public XCSDocument parse(InputSource src) throws SAXException, IOException
    {
        XCSDocument ret = new XCSDocument();
        parse(ret, src);
        return ret;
    }
    
    /**
     * Using a pre-built XCSDocument build into in a new XCS DOM tree from the
     * given XCS XML input source.
     *
     * @param doc The XCSDocument into which the built XCS should be placed.
     * @param src The input source to read the XCS XML data.
     * @throws SAXException All exceptions with reading and parsing the XCS.
     * @throws IOException All I/O exceptions that occur.
     */
    public void parse(XCSDocument doc, InputSource src) throws SAXException, IOException
    {
        synchronized (this)
        {
            if (document != null)
                throw new IllegalStateException("XCSParser is already parsing a document.");
            document = doc;
        }
        reader.parse(src);
        document = null;
    }
    
    /*********************************/
    /* org.xml.ContentHandler        */
    
    /** {@inheritDoc} */
    public void setDocumentLocator(Locator loc)
    {
        LOGGER.entering("XCSParser", "setDocumentLocator",
                String.format("Line=%d Col=%d", loc.getLineNumber(),
                loc.getColumnNumber()));
        locator = loc;
    }
    
    /** {@inheritDoc} */
    public void startDocument() throws SAXException
    {
        LOGGER.entering("XCSParser", "startDocument");
        assert stack.isEmpty() : "XCSParser stack is not empty.";
    }
    
    /** {@inheritDoc} */
    public void endDocument() throws SAXException
    {
        LOGGER.entering("XCSParser", "endDocument");
        assert stack.isEmpty() : "XCSParser stack is not empty.";
    }
    
    /** {@inheritDoc} */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        LOGGER.entering("XCSParser", "startPrefixMapping",
                String.format("prefix='%s' uri='%s'", prefix, uri));
        try
        {
            namespace.put(prefix, new URI(uri));
        }
        catch (URISyntaxException err)
        {
            throw new SAXParseException("Invalid URI", locator, err);
        }
    }
    
    /** {@inheritDoc} */
    public void endPrefixMapping(String prefix) throws SAXException
    {
        LOGGER.entering("XCSParser", "endPrefixMapping",
                String.format("prefix='%s'", prefix));
    }
    
    /** {@inheritDoc} */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        LOGGER.entering("XCSParser", "startElement",
                String.format("%d: uri='%s' local='%s' qName='%s'",
                locator.getLineNumber(), uri, localName, qName));
        stack.push(current);
        current = document.createXCSElement(localName, locator);
        for (int i = 0; i < atts.getLength(); i++)
        {
            LOGGER.finer(String.format("Attribute: qname='%s' local='%s' type='%s' uri='%s' value='%s'",
                    atts.getQName(i), atts.getLocalName(i), atts.getType(i), atts.getURI(i), atts.getValue(i)));
            try
            {
                current.setAttribute(atts.getQName(i), atts.getValue(i));
            }
            catch (DOMException err)
            {
                LOGGER.severe(String.format("<%s %s='%s'> line %d\n\t%s",
                        localName, atts.getQName(i), atts.getValue(i),
                        locator.getLineNumber(), err.getLocalizedMessage()));
            }
        }
    }
    
    /** {@inheritDoc} */
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        LOGGER.entering("XCSParser", "endElement",
                String.format("%d: uri='%s' local='%s' qName='%s'",
                locator.getLineNumber(), uri, localName, qName));
        current.endElement(locator);
        XCSElement child = current;
        current = stack.pop();
        if (current != null)
            current.appendChild(child);
    }
    
    /** {@inheritDoc} */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        LOGGER.entering("XCSParser", "characters",
                String.format("'%s'", new String(ch, start, length)));
        String data = new String(ch, start, length);
        if (collapseWhitespace)
            data = data.replaceAll("\\s+", " ");
        Text text = document.createTextNode(data);
        current.appendChild(text);
    }
    
    /** {@inheritDoc} */
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
        LOGGER.entering("XCSParser", "ignorableWhitespace",
                String.format("'%s'", new String(ch, start, length)));
        if (!collapseWhitespace)
        {
            String data = new String(ch, start, length);
            Text text = document.createTextNode(data);
            current.appendChild(text);
        }
    }
    
    /** {@inheritDoc} */
    public void processingInstruction(String target, String data) throws SAXException
    {
        LOGGER.entering("XCSParser", "processingInstruction",
                String.format("target='%s' data='%s'", target, data));
    }
    
    /** {@inheritDoc} */
    public void skippedEntity(String name) throws SAXException
    {
        LOGGER.entering("XCSParser", "skippedEntity", name);
    }
    
    
    /*********************************/
    /* org.xml.DTDHandler            */

    /** {@inheritDoc} */
    public void notationDecl(String name, String publicId, String systemId)
        throws SAXException
    {
        LOGGER.entering("XCSParser", "notationDecl",
                String.format("name='%s' publicId='%s' systemId='%s'",
                name, publicId, systemId));
    }
    
    /** {@inheritDoc} */
    public void unparsedEntityDecl(String name, String publicId, String systemId,
        String notationName) throws SAXException
    {
        LOGGER.entering("XCSParser", "unparsedEntityDecl",
                String.format("name='%s' publicId='%s' systemId='%s' notationName='%s'",
                name, publicId, systemId, notationName));
    }
    
    /*********************************/
    /* org.xml.EntityResolver        */

    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {   //TODO: Need to check if I know the publicID
        LOGGER.entering("XCSParser", "resolvedEntity",
                String.format("publicId='%s' systemId='%s'", publicId, systemId));
        return resolver.resolveEntity(publicId, systemId);
    }
        
    /*********************************/
    /* org.xml.ErrorHandler          */

    /** {@inheritDoc} */
    public void warning(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.WARNING, "XCSParser SAX Warning", exception);
        throw exception;
    }
    
    /** {@inheritDoc} */
    public void error(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.SEVERE, "XCSParser SAX Error", exception);
        throw exception;
        /*
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.dom.xcs.XCSParser");
        String msg = MessageFormat.format(bundle.getString("SaxError"),
                exception.getMessage(),
                exception.getLineNumber(),
                exception.getColumnNumber());
        throw new SAXException(msg);
        */
    }
    
    /** {@inheritDoc} */
    public void fatalError(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.SEVERE, "XCSParser SAX Fatal", exception);
        throw exception;
        /*
        ResourceBundle bundle = ResourceBundle.getBundle("org.ttt.salt.dom.xcs.XCSParser");
        String msg = MessageFormat.format(bundle.getString("SaxFatalError"),
                exception.getMessage(),
                exception.getLineNumber(),
                exception.getColumnNumber());
        throw new SAXException(msg);
        */
    }

    /**
     * Unit Test access class.
     */
    class TestAccess
    {
        /** Only constructor. */
        TestAccess()
        {
        }
        
        /** @return Field of this object. */
        XMLReader reader()
        {
            return reader;
        }
        
        /** @return Field of this object. */
        XCSDocument document()
        {
            return document;
        }
        
        /** @param doc Set field of this object. */
        void document(XCSDocument doc)
        {
            document = doc;
        }
        
        /** @return Field of this object. */
        XCSElement current()
        {
            return current;
        }
        
        /** @return Field of this object. */
        Stack<XCSElement> stack()
        {
            return stack;
        }
        
        /** @return Field of this object. */
        Locator locator()
        {
            return locator;
        }
    }
}

