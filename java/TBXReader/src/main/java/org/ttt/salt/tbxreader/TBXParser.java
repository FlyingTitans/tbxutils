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

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.ext.DefaultHandler2;

/**
 * This is used by TBXReader to do the parsing of the TBX XML stream.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
class TBXParser extends DefaultHandler implements Runnable
{
    /** Map of PUBLIC names to entities. */
    private static Map<String, String> names2rsrc
            = new java.util.HashMap<String, String>();
    
    static
    {
        names2rsrc.put("ISO 30042:2008A//DTD TBX core//EN", "/xml/TBXcoreStructV02.dtd");
        names2rsrc.put("ISO 30042:2008A//DTD TBX XCS//EN", "/xml/tbxxcsdtd.dtd");                
    }
    
    /** XML data source to parse. */
    private InputStream source;
    
    /** SAX parser that will handle XML parsing and validation. */
    private SAXParser parser;
    
    /** Thread that will handle the parsing of the XML file. */
    private Thread thread;
    
    /** Indicator that the thread should be stopped. */
    private boolean stop;
    
    /** The martif header element. */
    private MartifHeader martifheader;
    
    /** The martif header exception. */
    private TBXException martifheaderexception;
    
    /** Queue of term entry elements that have been parsed. */
    private BlockingQueue<TermEntry> termentries
            = new java.util.concurrent.ArrayBlockingQueue<TermEntry>(32);
    
    /** The fatal parse exception that occurred after last TermEntry in the queue. */
    private SAXParseException fatalerror;
        
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
     * Stop all parsing of the XML file and interrupt any threads that are
     * waiting for something to become available (e.g. {@link getMartifHeader}.
     */
    public synchronized void stop()
    {
        stop = true;
        if (thread != null)
            thread.interrupt();
    }
    
    /**
     * Get the {@link MartifHeader} from the XML after it has been parsed.
     * This may block the first time until the header has been parsed. If this
     * is called a second time it will not block and will either return the
     * same object or throw the same exception as the first call.
     *
     * @return The martif header object for the TBX file.
     * @throws TBXException Indicates a well-formed or validation error in
     *  the XML has occurred.
     * @throws InterruptedException While waiting for the martif header to
     *  become available this thread was interrupted, or the parsing has
     *  been stopped without the header being parsed.
     */
    public synchronized MartifHeader getMartifHeader()
        throws TBXException, InterruptedException
    {
        while (martifheader == null && martifheaderexception == null)
        {
            if (!stop)
                wait();
            if (stop)
                throw new InterruptedException();
        }
        if (martifheaderexception != null)
            throw martifheaderexception;
        return martifheader;
    }
    
    /**
     * Get the next {@link TermEntry} from the XML after it has been parsed,
     * validated, and checked.
     *
     * @return The term entry object from the TBX file or <code>null</code>
     *  if there are no more entries.
     */
    public TermEntry getNextTermEntry()
    {
        return termentries.poll();
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
            if (err.getCause() instanceof InterruptedException)
                TBXReader.LOGGER.info("TBXParser thread interrupted.");
            else
                TBXReader.LOGGER.log(Level.SEVERE, "TBXParser had unhandled SAXException.", err);
        }
        catch (IOException err)
        {
            TBXReader.LOGGER.log(Level.SEVERE, "TBXParser had unhandled IOException.", err);
        }
        catch (Throwable err)
        {
            TBXReader.LOGGER.log(Level.SEVERE, "Exception in TBXParser thread.", err);
        }
        finally
        {
            thread = null;
            synchronized (this)
            {
                notifyAll();
            }
        }
    }
    
    //===========================================
    // org.xml.sax.EntityResolver
    //===========================================
    
    /** {@inheritDoc} */
    public InputSource resolveEntity(String publicId, String systemId)
        throws IOException, SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    //===========================================
    // org.xml.sax.DTDHandler
    //===========================================
    
    /** {@inheritDoc} */
    public void notationDecl(String name, String publicId, String systemId)
        throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void unparsedEntityDecl(String name, String publicId,
        String systemId, String notationName) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    //===========================================
    // org.xml.sax.ContentHandler
    //===========================================

    /** {@inheritDoc} */
    public void setDocumentLocator(Locator locator)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void startDocument() throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void endDocument() throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void endPrefixMapping(String prefix) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void endElement(String uri, String localName, String qName)
         throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void skippedEntity(String name) throws SAXException
    {
        throw new UnsupportedOperationException();
    }

    
    //===========================================
    // org.xml.sax.ErrorHandler
    //===========================================
    
    /** {@inheritDoc} */
    public void warning(SAXParseException exception) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void error(SAXParseException exception) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void fatalError(SAXParseException exception) throws SAXException
    {
        throw new UnsupportedOperationException();
    }
}

