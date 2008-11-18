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
package org.ttt.salt.dom.tbx;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;
import java.util.Stack;
import java.util.Observable;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.DOMException;
import org.ttt.salt.XCSDocument;
import org.ttt.salt.TBXResolver;
import org.ttt.salt.TBXException;
import org.ttt.salt.XCSValidationException;



/**
 * This handler will build a {@link org.w3c.dom.Document} from a well-formed
 * valid TBX document.
 * <p>
 * The principle purpose of using SAX2 to do this instead of the standard
 * DOM methods is this will allow for better error reporting of problems in
 * the TBX file. It will also allow the error messages to be localized here
 * and not require an external localization.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXParser
    extends Observable
    implements ContentHandler, DTDHandler, EntityResolver, ErrorHandler
{
    /*
     */

    /**
     * Defines an event that is sent to all observers as the TBX XML stream
     * is parsed and selected elements complete parsing and validation.
     *
     * @author Lance Finn Helsten
     * @version $Id$
     */
    public final class Event
    {
        /** TBXElement that just completed its build and validation. */
        private TBXElement element;
        
        /** Indicates that the TBXElement is XCS valid. */
        private boolean valid;
                
        /**
         * @param elem The element that finished building in parse.
         * @param v The element is a termEntry and it is XCS valid.
         */
        private Event(TBXElement elem, boolean v)
        {
            element = elem;
            valid = v;
        }
        
        /**
         * Get the TBXElement that this event occured on.
         *
         * @return TBXElement that was built and validated.
         */
        public TBXElement getTBXElement()
        {
            return element;
        }
        
        /**
         * Get immediate parent element of the element just built and validated.
         *
         * @return TBXElement parent of element built and validated.
         */
        public TBXElement getParentTBXElement()
        {
            return current;
        }
        
        /**
         * Get the TBXDocument that this event occured in.
         *
         * @return TBXDocument containing the element.
         */
        public TBXDocument getTBXDocument()
        {
            return document;
        }
        
        /**
         * Is the TBXElement valid against the XCS document. If this event
         * is not for a termEntry element then this will always return false.
         * If this event is for a termEntry and there were problems building
         * the XCS document then this will always return false.
         * <p>
         * If the XCS document was not built correctly then {@link #getXCSDocument}
         * will return a null.</p>
         *
         * @return The XCS validity of the TBX element in this event.
         */
        public boolean isXCSValid()
        {
            return valid;
        }
        
        /**
         * Get the locator object that shows where in the XML document this
         * element came from.
         *
         * @return Locator for the element just built and validated.
         */
        public Locator getLocator()
        {
            return locator;
        }
        
        /**
         * Get the XCS validation exceptions for this event. If this event
         * is on an element besides a termEntry then this set will be empty.
         *
         * @return Sorted set of XCSValidationException that occurred on
         *  the XCS validation step.
         */
        public SortedSet<XCSValidationException> getExceptions()
        {
            return exceptions;
        }
    }

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
                "martifHeader", "date", "note", "tig", "ntig", "ref", "xref",
                "hi", "foreign", "bpt", "ept", "it", "ph", "ut",
                "langSet", "p", "fileDesc", "titleStmt", "title",
                "publicationStmt", "sourceDesc", "encodingDesc", "ude",
                "map", "revisionDesc", "change", "text", "front", "body", "back",
                "refObjectList", "refObject", "item", "itemGrp", "itemSet",
                "termEntry", "termGrp", "termNote", "termNoteGrp",
                "term", "termComp", "termCompGrp", "termCompList",
                "transac", "transacGrp", "transacNote",
                "admin", "adminGrp", "adminNote",
                "descrip", "descripGrp", "descripNote"
            ));

    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt.dom.tbx");
        
    /** The {@link org.xml.sax.XMLReader} this parser works through. */
    private XMLReader reader;
    
    /** The entity resolver that I use. */
    private TBXResolver resolver;
    
    /** Namespace mappings. */
    private Map<String, URI> namespace = new java.util.HashMap<String, URI>();
    
    /** The XCSDocument built while parsing the TBXDocument. */
    private XCSDocument xcsDocument;
    
    /** The TBXElement that represents the entire document. */
    private TBXDocument document;
    
    /** The current TBXElement being built. */
    private TBXElement current;
    
    /** The stack of all TBXElement objects that have been created. */
    private Stack<TBXElement> stack = new Stack<TBXElement>();
    
    /** The current {@link org.xml.sax.Locator} for tracking elements. */
    private Locator locator;
    
    /** Indicates that whitespace is not significant (xml:space="default"). */
    private boolean collapseWhitespace;
    
    /** Validate each termEntry element against the XCS. */
    private boolean xcsValidate;
    
    /** Holds the current set of validation exceptions. This will be cleared
     * each time a new termEntry has completed parsing in preparation for XCS
     * validation.
     */
    private SortedSet<XCSValidationException> exceptions = new java.util.TreeSet<XCSValidationException>();
    
    /**
     * Create a new parser for TBX document parsing.
     *
     * @param r The XML entity resolver the parser should use.
     * @param xcsvalidate Validate each termEntry against the XCS when it
     *  is built.
     * @throws SAXNotRecognizedException Requried parser feature is unavailable.
     * @throws SAXNotSupportedException Could not set required parser feature.
     */
    public TBXParser(TBXResolver r, boolean xcsvalidate) throws SAXNotRecognizedException,
        SAXNotSupportedException
    {
        resolver = r;
        xcsValidate = xcsvalidate;
        try
        {
            reader = new org.apache.xerces.parsers.SAXParser();
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            reader.setEntityResolver(this);
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
     * Parse the input source into a new created TBXDocument.
     * <p>
     * If a TBXDocument has not been set when this is called then a default
     * TBXDocument will be created where all elements will be held as hard
     * references, and the full document will be built.</p>
     *
     * @param src The input source to read the TBX XML data.
     * @return The TBX document created from this parse pass.
     * @throws SAXException All exceptions with reading and parsing the TBX.
     * @throws IOException All I/O exceptions that occur.
     */
    public TBXDocument parse(InputSource src) throws SAXException, IOException
    {
        if (document == null)
        {
            TBXDocument ret = new TBXDocument(true);
            setTBXDocument(ret);
        }
        reader.parse(src);
        return getTBXDocument();
    }
    
    /**
     * Set a custom TBXDocument to use when parsing the TBX XML input source.
     * If parsing has started then {@link java.lang.IllegalStateException}
     * will be thrown.
     *
     * @param doc The TBXDocument into which the built TBX should be placed.
     */
    public void setTBXDocument(TBXDocument doc)
    {
        synchronized (this)
        {
            if (document != null)
                throw new IllegalStateException("TBXParser is already parsing a document.");
            document = doc;
        }
    }
    
    /**
     * Get the TBXDocument that was generated from the parse of the TBX XML
     * source.
     *
     * @return The generated TBXDocument.
     */
    public TBXDocument getTBXDocument()
    {
        return document;
    }
    
    /**
     * Get the XCSDocument that was successfully built while parsing the TBX
     * document.
     *
     * @return The associated {@link org.ttt.salt.XCSDocument} built by this
     *  parser. If there were failures in building then this will be null.
     */
    public XCSDocument getXCSDocument()
    {
        return xcsDocument;
    }
    
    /*********************************/
    /* org.xml.ContentHandler        */
    
    /** {@inheritDoc} */
    public void setDocumentLocator(Locator loc)
    {
        LOGGER.entering("TBXParser", "setDocumentLocator",
                String.format("Line=%d Col=%d", loc.getLineNumber(), loc.getColumnNumber()));
        locator = loc;
    }
    
    /** {@inheritDoc} */
    public void startDocument() throws SAXException
    {
        LOGGER.entering("TBXParser", "startDocument");
        assert stack.isEmpty() : "TBXParser stack is not empty.";
    }
    
    /** {@inheritDoc} */
    public void endDocument() throws SAXException
    {
        LOGGER.entering("TBXParser", "endDocument");
        assert stack.isEmpty() : "TBXParser stack is not empty.";
    }
    
    /** {@inheritDoc} */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        LOGGER.entering("TBXParser", "startPrefixMapping",
                String.format("prefix='%s', uri='%s'", prefix, uri));
        LOGGER.info(String.format("startPrefixMapping: prefix='%s' uri='%s'", prefix, uri));
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
        LOGGER.entering("TBXParser", "enPrefixMapping",
                String.format("prefix='%s'", prefix));
    }
    
    /** {@inheritDoc} */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException
    {
        LOGGER.entering("TBXParser", "startElement",
                String.format("Element %d: uri='%s' local='%s' qName='%s'",
                    locator.getLineNumber(), uri, localName, qName));
        stack.push(current);
        current = document.createTBXElement(localName, locator);
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
        LOGGER.entering("TBXParser", "endElement", String.format("Element %d: uri='%s' local='%s' qName='%s'",
                    locator.getLineNumber(), uri, localName, qName));
        current.endElement(locator);
        TBXElement child = current;
        current = stack.pop();
        if (current != null)
            current.appendChild(child);
        
        boolean valid = false;
        exceptions.clear();
        if (xcsValidate && localName.equals("encodingDesc"))
        {   //It is possible now to build an XCS document to validate against
            NodeList plist = child.getElementsByTagName("p");
        GOT_XCS:
            for (int i = 0; i < plist.getLength(); i++)
            {
                Element p = (Element) plist.item(i);
                String xcsURI = null;
                if (p.hasAttribute("type"))
                {                    
                    if (p.getAttribute("type").equals("XCSURI"))
                    {
                        xcsURI = p.getTextContent().trim();
                    }
                    else if (p.getAttribute("type").equals("XCSContent"))
                    {
                        throw new UnsupportedOperationException("XCSContent location type unsupported.");
                    }
                    else if (p.getAttribute("type").equals("DCSName"))
                    {
                        xcsURI = p.getTextContent().trim();
                    }
                    else if (p.getAttribute("type").equals("XCSName"))
                    {
                        xcsURI = p.getTextContent().trim();
                    }
                }
                
                if (xcsURI == null)
                {
                    LOGGER.warning("XCS not specified.");
                    IOException err = new FileNotFoundException("XCS not specified.");
                    TBXException tbxerr = new TBXException(TBXException.Priority.XCS, err);
                    document.addParseException(tbxerr);
                }
                else
                {
                    try
                    {
                        LOGGER.info("Using XCS: " + xcsURI);
                        xcsDocument = new XCSDocument(xcsURI, resolver);
                        break GOT_XCS;
                    }
                    catch (FileNotFoundException err)
                    {
                        String msg = String.format("XCS file '%s' not found. Because of error: %s", xcsURI, err.getMessage());
                        LOGGER.info(msg);
                        LOGGER.log(Level.FINE, msg, err);
                        TBXException tbxerr = new TBXException(TBXException.Priority.XCS, err);
                        document.addParseException(tbxerr);
                    }
                    catch (IOException err)
                    {
                        LOGGER.log(Level.WARNING, "Exception building XCS", err);
                    }
                    catch (ParserConfigurationException err)
                    {
                        LOGGER.log(Level.WARNING, "Exception building XCS", err);
                    }
                    catch (SAXException err)
                    {
                        LOGGER.log(Level.WARNING, "Exception building XCS", err.getMessage());
                        LOGGER.log(Level.FINE, "Exception building XCS", err);
                    }
                }
            }
        }
        else if (xcsValidate && localName.equals("termEntry"))
        {   //VALIDATE the term entry
            try
            {
                if (xcsDocument != null)
                {
                    xcsDocument.validateTermEntry(child);
                    valid = true;
                }
            }
            catch (XCSValidationException err)
            {
                exceptions.add(err);
                TBXException tbxerr = new TBXException(TBXException.Priority.XCS, err);
                document.addParseException(tbxerr);
            }
        }
        Event evt = new Event(child, valid);
        setChanged();
        notifyObservers(evt);
    }
    
    /** {@inheritDoc} */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        LOGGER.entering("TBXParser", "characters", String.format("'%s'", new String(ch, start, length)));
        String data = new String(ch, start, length);
        if (collapseWhitespace)
            data = data.replaceAll("\\s+", " ");
        Text text = document.createTextNode(data);
        current.appendChild(text);
    }
    
    /** {@inheritDoc} */
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
        LOGGER.entering("TBXParser", "ignorableWhitespace", String.format("'%s'", new String(ch, start, length)));
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
        LOGGER.entering("TBXParser", "processingInstruction", String.format("target='%s' data='%s'", target, data));
    }
    
    /** {@inheritDoc} */
    public void skippedEntity(String name) throws SAXException
    {
        LOGGER.entering("TBXParser", "skippedEntity", String.format("name='%s'", name));
    }
    
    
    /*********************************/
    /* org.xml.DTDHandler            */

    /** {@inheritDoc} */
    public void notationDecl(String name, String publicId, String systemId) throws SAXException
    {
        LOGGER.entering("TBXParser", "notationDecl",
                String.format("name='%s' publicId='%s' systemId='%s'",
                name, publicId, systemId));
    }
    
    /** {@inheritDoc} */
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException
    {
        LOGGER.entering("TBXParser", "unparsedEntityDecl",
                String.format("name='%s' publicId='%s' systemId='%s' notationName='%s'",
                            name, publicId, systemId, notationName));
    }
    
    /*********************************/
    /* org.xml.EntityResolver        */

    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {   //NOTDONE: need to resolve the entity
        LOGGER.entering("TBXParser", "resolvedEntity",
                String.format("publicId='%s' systemId='%s'", publicId, systemId));
        return resolver.resolveEntity(publicId, systemId);
    }
        
    /*********************************/
    /* org.xml.ErrorHandler          */

    /** {@inheritDoc} */
    public void warning(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.SEVERE, "SAX Warning", exception);
        document.addParseException(
                new TBXException(TBXException.Priority.XMLVALID_MINOR, exception));
    }
    
    /** {@inheritDoc} */
    public void error(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.SEVERE, "SAX Error", exception);
        document.addParseException(
                new TBXException(TBXException.Priority.XMLVALID_MAJOR, exception));
    }
    
    /** {@inheritDoc} */
    public void fatalError(SAXParseException exception) throws SAXException
    {
        LOGGER.log(Level.SEVERE, "SAX Fatal", exception);
        document.addParseException(
                new TBXException(TBXException.Priority.WELLFORMED, exception));
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
        TBXDocument document()
        {
            return document;
        }
        
        /** @param doc Set field of this object. */
        void document(TBXDocument doc)
        {
            document = doc;
        }
        
        /** @return Field of this object. */
        TBXElement current()
        {
            return current;
        }
        
        /** @return Field of this object. */
        Stack<TBXElement> stack()
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

