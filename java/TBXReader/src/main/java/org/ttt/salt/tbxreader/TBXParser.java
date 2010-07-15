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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.ext.DefaultHandler2;
import org.w3c.dom.*;

/**
 * This is used by TBXReader to do the parsing of the TBX XML stream.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
class TBXParser extends DefaultHandler implements Runnable
{
    /*
     * Much of the encasing an exception in a SAXException is to allow that
     * exception through the XML parser where it can be handled directly.
     *
     * This uses the DOM object system to build objects, but it does not
     * use the DOM document builder because that attempts to build the
     * entire document at once, and this needs to handle very large files
     * with event driven term entry construction.
     */

    /** Logger for all classes in this package. */
    static final Logger PARSE_LOG = Logger.getLogger("org.ttt.salt.tbxreader.parser");
    
    /** Maximum queue size. */
    static final int QUEUE_SIZE = 32;

    /** Map of PUBLIC names to entities. */
    private static Map<String, String> names2rsrc = new java.util.HashMap<String, String>();
    
    /** Set of known element names that can be ignored by the parser. */
    private static Set<String> knownTagNames = new java.util.TreeSet<String>();
    
    static
    {
        PARSE_LOG.setLevel(Level.WARNING);
    
        names2rsrc.put("ISO 30042:2008A//DTD TBX core//EN", "/xml/TBXcoreStructV02.dtd");
        names2rsrc.put("ISO 30042:2008A//DTD TBX XCS//EN", "/xml/tbxxcsdtd.dtd");

        knownTagNames.add("martif");        
        knownTagNames.add("sourceDesc");        
        knownTagNames.add("fileDesc");        
        knownTagNames.add("encodingDesc");        
        knownTagNames.add("p");
        knownTagNames.add("hi");
        knownTagNames.add("tig");
        knownTagNames.add("langSet");
        knownTagNames.add("term");
        knownTagNames.add("text");
        knownTagNames.add("descrip");
        knownTagNames.add("descripGrp");
        knownTagNames.add("descripNote");
    }
    
    /** Document builder to generate new documents. */
    private DocumentBuilder documentbuilder;
        
    /** XML data source to parse. */
    private InputStream source;
    
    /** SAX parser that will handle XML parsing and validation. */
    private SAXParser parser;
    
    /** SAX locator for finding where errors occur. */
    private Locator locator;
    
    /** DOM document for all new nodes. */
    private Document document;

    /** Default language for document. */
    private String defaultlang = "en";
        
    /** Current element node. */
    private Element element;
    
    /** Element stack. */
    private Stack<Element> elements = new Stack<Element>();
    
    /** Thread that will handle the parsing of the XML file. */
    private Thread thread;
    
    /** Indicator that the thread should be stopped. */
    private boolean stop;
    
    /** An exception occurred in parsing that is not recoverable. */
    private TBXException fatalerror;
        
    /** The martif header element. */
    private MartifHeader martifheader;
    
    /** Number of term entries that have been processed. */
    private int termentrycount;
    
    /** Queue of term entry elements that have been parsed. */
    private BlockingQueue<TermEntry> termentries
            = new java.util.concurrent.ArrayBlockingQueue<TermEntry>(QUEUE_SIZE);
    
    /**
     * Current term entry being built: this will be the last term entry
     * built until a new {@link startElement} is called for a new termEntry.
     */
    private TermEntry currentTermEntry;
            
    /**
     * Create a new TBX parser.
     *
     * @param saxparser The configured XML SAX parser to use in parsing the
     *  TBX file.
     * @param data The XML data source to be parsed.
     */
    public TBXParser(SAXParser saxparser, InputStream data)
    {
        source = data;
        parser = saxparser;
                
        thread = new Thread(this);
        thread.setName("TBXParser");
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Get the {@link MartifHeader} from the XML after it has been parsed.
     * This may block the first time until the header has been parsed. If this
     * is called a second time it will not block and will either return the
     * same object or throw the same exception as the first call.
     *
     * @return The martif header object for the TBX file.
     * @throws IOException An I/O exception occurred while parsing the file.
     * @throws TBXException Indicates a well-formed or validation error in
     *  the XML has occurred.
     * @throws InterruptedException While waiting for the martif header to
     *  become available this thread was interrupted, or the parsing has
     *  been stopped without the header being parsed.
     */
    public synchronized MartifHeader getMartifHeader()
        throws IOException, TBXException, InterruptedException
    {
        while (martifheader == null && fatalerror == null)
        {
            if (stop)
                throw new InterruptedException();
            wait();
        }
        if (fatalerror != null)
        {
            if (fatalerror.getCause() instanceof IOException)
                throw (IOException) fatalerror.getCause();
            else
                throw fatalerror;
        }
        return martifheader;
    }
    
    /**
     * Get the next {@link TermEntry} from the XML after it has been parsed,
     * validated, and checked.
     *
     * @return The term entry object from the TBX file or <code>null</code>
     *  if there are no more entries.
     * @throws InterruptedException While waiting for a term entry to
     *  become available this thread was interrupted.
     */
    public TermEntry getNextTermEntry() throws InterruptedException
    {
        return termentries.poll(100, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get the current queue size.
     */
    boolean isTermEntryQueueFull()
    {
        return termentries.size() == QUEUE_SIZE;
    }
    
    /**
     * Get the number of term entries processed.
     */
    int getTermEntriesProcessed()
    {
        return termentrycount;
    }
    
    /**
     * Put the current term entry to the queue.
     *
     * @throws SAXException The thread was interrupted while putting
     *  the current term entry.
     */
    private void offerTermEntry() throws SAXException
    {
        if (currentTermEntry != null)
        {
            try
            {
                currentTermEntry.init();
                termentries.put(currentTermEntry);
                termentrycount++;
                currentTermEntry = null;
            }
            catch (TBXException err)
            {
                throw new SAXException(err);
            }
            catch (InterruptedException err)
            {
                throw new SAXException(err);
            }
        }
    }
    
    /**
     * Stop all parsing of the XML file and interrupt any threads that are
     * waiting for something to become available (e.g. {@link getMartifHeader}.
     */
    public synchronized void stop()
    {
        stop = true;
        thread.interrupt();
    }

    /** {@inheritDoc} */
    public void run()
    {
        try
        {
            parser.parse(source, this);
        }
        catch (SAXException err)
        {
            if (err.getCause() instanceof TBXException)
                fatalerror = (TBXException) err.getCause();
            else if (err.getCause() instanceof SAXParseException)
                fatalerror = new TBXException(err.getCause());
            else if (err.getCause() instanceof InterruptedException)
                TBXReader.LOGGER.info("TBXParser thread interrupted.");
            else
                TBXReader.LOGGER.log(Level.SEVERE, "TBXParser had unhandled SAXException.", err);
        }
        catch (IOException err)
        {
            fatalerror = new TBXException("I/O Exception", err);
        }
        catch (Throwable err)
        {
            TBXReader.LOGGER.log(Level.SEVERE, "Exception in TBXParser thread.", err);
        }
        finally
        {
            synchronized (this)
            {
                stop = true;
                notifyAll();
            }
        }
    }
    
    /**
     * Get the thread that is parsing this XML document.
     *
     * @return Thread of control.
     */
    Thread getThread()
    {
        return thread;
    }
        
    /**
     * Check to see if I need to stop.
     *
     * @throws SAXException The exception that can be thrown through the
     *  parser to the top of the run loop to indicate that I am stopped.
     */
    private void checkStop() throws SAXException
    {
        if (stop)
        {
            Exception ie = new InterruptedException();
            throw new SAXException("", ie);
        }
    }
    
    
    //===========================================
    // org.xml.sax.EntityResolver
    //===========================================
    
    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId)
        throws IOException, SAXException
    {
        PARSE_LOG.entering("TBXParser", "resolveEntity", 
            String.format("PublicID='%s' SystemId='%s'", publicId, systemId));
        TBXReader.LOGGER.info(
            String.format("Resolve Entity for PublicID='%s' SystemId='%s'", publicId, systemId));
        checkStop();
        java.io.InputStreamReader reader = null;
        try
        {
            InputStream input = null;
            if (names2rsrc.containsKey(publicId))
            {   //Check to see if it is a named resource
                TBXReader.LOGGER.info("Entity is a known publicId: " + publicId);
                systemId = names2rsrc.get(publicId);
                input = getClass().getResourceAsStream(names2rsrc.get(publicId));
            }
            else if (systemId.matches("\\w+:.+"))
            {   //Do the full URI parsing
                TBXReader.LOGGER.info("Entity is a URI: " + systemId);
                URI uri = new URI(systemId);
                input = uri.toURL().openStream();
            }
            else
            {   //Check the relative URI location
                TBXReader.LOGGER.fine("Unknown schema systemId: " + systemId);
                if (systemId.startsWith("/"))
                {
                    TBXReader.LOGGER.info("Entity is an absolute path: " + systemId);
                    File file = new File(systemId);
                    if (file.exists())
                        input = new java.io.FileInputStream(file);
                    else
                        input = getClass().getResourceAsStream(systemId);
                }                
                if (input == null)
                    throw new java.io.FileNotFoundException();
            }
            if (!input.markSupported())
                input = new java.io.BufferedInputStream(input);
            String enc = Utility.getEncoding(input);
            reader = new java.io.InputStreamReader(input, enc);
        }
        catch (java.io.FileNotFoundException err)
        {
            String msg = String.format("Entity could not be resolved:\n  PUBLIC: '%s'\n  SYSTEM: '%s'\n  BUILT IN: %s",
                publicId, systemId,
                names2rsrc.containsKey(publicId) ? names2rsrc.get(publicId) : "NONE");
            throw new java.io.FileNotFoundException(msg);
        }
        catch (java.io.UnsupportedEncodingException err)
        {
            throw new java.io.UnsupportedEncodingException(
                    String.format("PUBLIC %s SYSTEM %s", publicId, systemId));
        }
        catch (java.net.URISyntaxException err)
        {
            throw new SAXException("Invalid System ID format", err);
        }
        InputSource ret = new InputSource(reader);
        ret.setPublicId(publicId);
        ret.setSystemId(systemId);
        PARSE_LOG.exiting("TBXParser", "resolveEntity", ret);
        return ret;
    }
    
    //===========================================
    // org.xml.sax.DTDHandler
    //===========================================
    
    /** {@inheritDoc} */
    public void notationDecl(String name, String publicId, String systemId)
        throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "notationDecl");
        checkStop();
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void unparsedEntityDecl(String name, String publicId,
        String systemId, String notationName) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "unparsedEntityDecl");
        checkStop();
        throw new UnsupportedOperationException();
    }
    
    //===========================================
    // org.xml.sax.ContentHandler
    //===========================================

    /** {@inheritDoc} */
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }
    
    /** {@inheritDoc} */
    public void startDocument() throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "startDocument");
        checkStop();
        fatalerror = null;
        martifheader = null;
        termentries.clear();

        if (documentbuilder == null)
        {
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                //factory.setSchema
                //factory.setValidating(true);
                documentbuilder = factory.newDocumentBuilder();
            }
            catch (ParserConfigurationException err)
            {
                throw new SAXException("Unable to initialize DOM document.", err);
            }
        }
        document = documentbuilder.newDocument();
    }
    
    /** {@inheritDoc} */
    public void endDocument() throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "endDocument");
        checkStop();
    }
    
    /** {@inheritDoc} */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "startPrefixMapping");
        checkStop();
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void endPrefixMapping(String prefix) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "endPrefixMapping");
        checkStop();
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "startElement",
                String.format("ns='%s' QName='%s' LocalName='%s'", uri, qName, localName));
        checkStop();
        elements.push(element);
        
        if (uri.isEmpty())
            element = document.createElement(qName);
        else
            element = document.createElementNS(uri, qName);

        for (int i = 0; i < atts.getLength(); i++)
        {
            String ns = atts.getURI(i);
            String qname = atts.getQName(i);
            String value = atts.getValue(i);
            
            Attr attr;
            if (ns.isEmpty())
                attr = document.createAttribute(qname);
            else
                attr = document.createAttributeNS(ns, qname);
            attr.setValue(value);
            PARSE_LOG.finer("Attribute: " + attr);
            element.setAttributeNode(attr);
        }
        
        if (qName.equals("martif"))
        {
            String type = element.getAttribute("type");
            if (!type.equals("TBX"))
                throw new SAXException(new InvalidFileException(locator, "NotTBXFile"));
            String defaultlang = element.getAttributeNS("http://www.w3.org/XML/1998/namespace", "lang");
            if (defaultlang.isEmpty())
                throw new SAXException(new InvalidFileException(locator, "NoDefaultLang"));
        }
        else if (qName.equals("termEntry"))
        {
            offerTermEntry();
            currentTermEntry = new TermEntry(element, locator);
        }
    }
    
    /** {@inheritDoc} */
    public void endElement(String uri, String localName, String qName)
         throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "endElement",
                String.format("ns='%s' QName='%s' LocalName='%s'", uri, qName, localName));
        checkStop();
        Element child = element;
        element = elements.pop();
        
        assert element != null || qName.equals("martif");
        if (element != null)
            element.appendChild(child);

        if (qName.equals("martifHeader"))
        {
            synchronized (this)
            {
                martifheader = new MartifHeader(child);
                notifyAll();
            }
        }
        else if (qName.equals("body"))
        {
            offerTermEntry();
        }
        else if (qName.equals("termEntry"))
        {
        }
        else if (!knownTagNames.contains(qName))
        {
            PARSE_LOG.warning("Unknown element: " + qName);
        }
    }
    
    /** {@inheritDoc} */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "characters", new String(ch, start, length));
        checkStop();
        Text text = document.createTextNode(new String(ch, start, length));
        element.appendChild(text);
    }

    /** {@inheritDoc} */
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "ignorableWhitespace", String.format("Width: %d", length));
        checkStop();
    }
    
    /** {@inheritDoc} */
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "processingInstruction", new Object[]{target, data});
        checkStop();
        ProcessingInstruction pi = document.createProcessingInstruction(target, data);
        element.appendChild(pi);
    }
    
    /** {@inheritDoc} */
    public void skippedEntity(String name) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "skippedEntity", new Object[]{name});
        checkStop();
        throw new UnsupportedOperationException();
    }

    
    //===========================================
    // org.xml.sax.ErrorHandler
    //===========================================
    
    /** {@inheritDoc} */
    public void warning(SAXParseException exception) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "warning", exception);
        throw new UnsupportedOperationException(
            String.format("TBXParser#warning %s: %s", exception.getClass(), exception.getMessage()));
    }
    
    /** {@inheritDoc} */
    public void error(SAXParseException exception) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "error", exception);
        if (currentTermEntry != null)
        {
            InvalidTermEntryException err = new InvalidTermEntryException(locator, exception.getLocalizedMessage(), exception);
            currentTermEntry.getExceptions().add(err);
        }
        else
        {
            throw new SAXException("Unhandled SAX error.", exception);
        }
    }
    
    /** {@inheritDoc} */
    public void fatalError(SAXParseException exception) throws SAXException
    {
        PARSE_LOG.entering("TBXParser", "fatalError", exception);
        throw new SAXException("FatalError", exception);
    }
}

