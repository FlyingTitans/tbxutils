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

import java.io.Reader;
import java.io.FilterReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.ttt.salt.dom.tbx.TBXParser;
import org.ttt.salt.dom.tbx.TBXDocument;
import org.ttt.salt.dom.tbx.TBXElement;
import org.flyingtitans.xml.ElementalParser;

/**
 * This will perform XCS validation on an XML file.
 * <p>
 * The default behavior is to build the entire TBX document in memory while
 * validating each termEntry as it is completed. The exceptions for XCS
 * validation are only reported if the TBX file passes a basic pre-parse
 * check, and then a full XML parse and validation phase.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXFile
{
    /*
     */
    
    /**
     * The basic type of TBX file that is given to {@link TBXFile}. This may
     * be used to determine if the XML file is corrupt, if it is based on
     * the TBX DTD, or based on the TBX Schema.
     */
    public enum Type
    {
        /** */
        UNKNOWN,
        
        /** */
        CORRUPT,
        
        /** */
        DTD,
        
        /** */
        SCHEMA;
    }
        
    /** SCM information. */
    private static final String RCSID = "$Id$";
    
    /** */
    public static final String AUTO_TERMENTRY_ID_PREFIX = "AUTO_termEntry_ID_";
    
    /** */
    private static final DocumentBuilderFactory FACTORY;
    
    /** */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt");

    /**
     */
    static
    {
        
        FACTORY = DocumentBuilderFactory.newInstance();
        FACTORY.setNamespaceAware(true);
    }

    /** */
    private Type fileType = Type.UNKNOWN;
    
    /** Validation and compliance configuration. */
    private Configuration config;
    
    /** URI for the input file. */
    private URL url;
        
    /** */
    private Reader reader;
    
    /** */
    private XCSDocument xcsDocument;

    /** The entity resolver that I use. */
    private TBXResolver resolver;
        
    /** TBXParser that will build the TBXDocument. */
    private TBXParser tbxParser;
    
    /** TBXDocument that holds the DOM information for this file. */
    private TBXDocument tbxDocument;
    
    /** Indicates that the document has been parsed. */
    private boolean parsed;
    
    /** Indicates that the document is valid. */
    private boolean valid;
    
    /** Auto term entry id sequence number. */
    private int termEntrySeq;
    
    /** Map of all termEntry IDs to the termEntry element. */
    private Map<String, Element> termEntries = new java.util.HashMap<String, Element>();
        
    /** The exceptions that invalidated the file. */
    private SortedSet<TBXException> exceptions = new java.util.TreeSet<TBXException>();
    
    /** The exceptions that are warnings and do not invaidate the file. */
    private SortedSet<TBXException> warnings = new java.util.TreeSet<TBXException>();
    
    /**
     *
     * @param u The URL to the TBX file to process.
     * @param c The configuration for validation and compliance.
     * @throws IOException Any unhandled I/O exceptions.
     * @throws SAXExcetion Any SAX issues in creating the file.
     */
    public TBXFile(URL u, Configuration c) throws IOException, SAXException
    {
        if (c == null)
            throw new IllegalArgumentException("Configuration cannot be null");
        config = c;
        
        if (u == null)
            throw new IllegalArgumentException("URL argument cannot be null");
        url = u;
        
        resolver = new TBXResolver(url);
        tbxParser = new TBXParser(resolver, c);
        InputStream input = url.openStream();
        if (!input.markSupported())
            input = new BufferedInputStream(input);
        InputStreamReader inread = new InputStreamReader(input, TBXResolver.getEncoding(input));
        reader = new BufferedReader(inread);
    }
    
    /**
     * Parse and validate the document. This is a separate step from
     * construction to allow for observers to be added to this object.
     *
     * @throws IOException Only report problems with reading the document from
     *  the input source. All other exceptions should be taken from
     *  {@link #getInvalidatingExceptions}.
     */
    public void parseAndValidate() throws IOException
    {
        if (!parsed)
        {
            boolean ok = preParseCheck();
            if (ok)
                ok = parseDocument();
            if (ok)
                valid = validate();
            if (valid)
                buildTermEntriesMap();
        }
    }
        
    /**
     * Get the TBXDocument that was built.
     *
     * @return {@link org.ttt.salt.dom.tbx.TBXDocument} built by the parser.
     */
    public TBXDocument getTBXDocument()
    {
        return tbxDocument;
    }
    
    /**
     * Find out if the document is valid.
     *
     * @return true => The document is valid.
     */
    public boolean isValid()
    {
        return valid;
    }
    
    /**
     *
     * @param file The file to write this document to.
     * @throws IOException Any unhandled I/O exceptions.
     */
    public void write(File file) throws IOException
    {
        ResourceBundle bundle = getResourceBundle();
        PrintWriter out = new PrintWriter(new java.io.FileWriter(file));
        Element elem = getTBXDocument().getDocumentElement();
        out.println(bundle.getString("Header"));
        out.println(elem);
        out.flush();
        out.close();
    }
    
    /**
     * Return the type of file given to the object.
     *
     * @return Type of file being worked with.
     */
    public Type getType()
    {
        return fileType;
    }
    
    /**
     * This will return an map of all termEntry elements in the TBX file. This
     * is meant to be a quick index into the termEntry elements in the file.
     * <p>
     * <strong>WARNING:</strong> Changing this map without a corresponding
     * change made to the contained TBXDocument results in undefined
     * behavior.</p>
     *
     * @return Map of term entry id to termEntry elements in the file.
     */
    public Map<String, Element> getTermEntryMap()
    {
        return termEntries;
    }
        
    /**
     * Get the XCS Document that this is validated against.
     *
     * @return The XCS document being validated.
     */
    public XCSDocument getXCSDocument()
    {
        return xcsDocument;
    }
    
    /**
     * Utility function to get the body element.
     *
     * @return The element that is the main body element.
     */
    public Element getBodyElement()
    {
        Element root = getTBXDocument().getDocumentElement();
        Element text = (Element) root.getElementsByTagName("text").item(0);
        Element body = (Element) text.getElementsByTagName("body").item(0);
        return body;
    }
    
    /**
     * Return the exceptions that caused the file to be invalid.
     *
     * @return The list of exceptions that describe why the document is invalid.
     */
    public List<TBXException> getInvalidatingExceptions()
    {
        //Throwable[] ret = (Throwable[]) exceptions.toArray();
        //Arrays.sort(ret);
        //return Arrays.asList(ret);
        return new java.util.ArrayList<TBXException>(exceptions);
    }
    
    /**
     * Return the exceptions that are warnings on the file.
     *
     * @return The list of exceptions that describe why the document is invalid.
     */
    public List<TBXException> getWarningExceptions()
    {
        //Throwable[] ret = (Throwable[]) exceptions.toArray();
        //Arrays.sort(ret);
        //return Arrays.asList(ret);
        return new java.util.ArrayList<TBXException>(warnings);
    }
    
    /**
     * Get an appropriate localized resource bundle for this particular
     * TBX file.
     *
     * @return The localized {@link java.util.ResourceBundle}.
     */
    private ResourceBundle getResourceBundle()
    {
        return ResourceBundle.getBundle("org.ttt.salt.TBXFile");
    }
        
    /**
     * This will do some pre-wellformedness checks and determine what
     * type of file this is.
     *
     * @return true => The file passed the tests.
     * @throw IllegalStateException The document has already been built. This
     *  must be executed on a raw file or stream before parsing.
     * @throws IOException Any unhandled I/O exceptions.
     */
    private boolean preParseCheck() throws IOException
    {
        final int stageXMLDECL = 0;
        final int stageDOCTYPE = 1;
        final int stageMARTIF = 2;
        final int stageDONE = 3;
        final int minMarkSize = 1024;

        ResourceBundle bundle = getResourceBundle();
        ElementalParser parse = new ElementalParser(reader);
        reader.mark(minMarkSize);
        int stage = stageXMLDECL;
        ElementalParser.Token type = parse.next();
        while (stage != stageDONE)
        {
            if (type == ElementalParser.Token.EOF)
            {
                Exception cause = new StreamCorruptedException(bundle.getString("PreParse_EOF"));
                TBXException err = new TBXException(TBXException.Priority.PRE_PARSE, cause);
                exceptions.add(err);
                fileType = Type.CORRUPT;
                stage = stageDONE;
            }
            else if (type == ElementalParser.Token.ILLFORMED)
            {
                exceptions.add(new TBXException(TBXException.Priority.PRE_PARSE,
                    new StreamCorruptedException(bundle.getString("PreParse_Illformed")
                        + parse.sval)));
                fileType = Type.CORRUPT;
                stage = stageDONE;
            }
            else if (stage == stageXMLDECL
                    && type != ElementalParser.Token.XMLDECL)
            {
                exceptions.add(new TBXException(TBXException.Priority.PRE_PARSE,
                    new StreamCorruptedException(bundle.getString("PreParse_NoXMLDecl")
                        + parse.sval)));
                fileType = Type.CORRUPT;
            }
            else if (stage == stageXMLDECL
                    && type == ElementalParser.Token.XMLDECL)
            {
                stage = stageDOCTYPE;
            }
            else if (stage == stageDOCTYPE
                    && type == ElementalParser.Token.DOCTYPEDECL)
            {
                fileType = Type.DTD;
                //TODO--check the DOCTYPE name and URL
                stage = stageMARTIF;
            }
            else if (stage == stageMARTIF
                    && type == ElementalParser.Token.START)
            {
                if (fileType == Type.UNKNOWN)
                    fileType = Type.SCHEMA;
                stage = stageDONE;
            }
            type = parse.next();
        }
        try
        {
            reader.reset();
        }
        catch (IOException err)
        {
            if (err.getMessage().equals("Mark invalid"))
            {
                exceptions.add(new TBXException(TBXException.Priority.PRE_PARSE,
                    new StreamCorruptedException(bundle.getString("PreParse_Overflow"))));
            }
            else
            {
                throw err;
            }
        }
        return exceptions.isEmpty();
    }
    
    /**
     * This will do the XML parsing, well-formedness checks, and validation
     * if there is a DOCTYPE tag.
     *
     * @return true => The document passed well-formedness and validation
     *  checks.
     * @throw IllegalStateException The document has already been built. This
     *  must be executed on a raw file or stream before parsing.
     * @throws IOException Any unhandled I/O exceptions.
     */
    private boolean parseDocument() throws IOException
    {
        boolean ret = true;
        try
        {
            switch (fileType)
            {
                case CORRUPT:
                    //no op
                    break;
                case DTD:
                    buildDocumentWithDTD();
                    break;
                case SCHEMA:
                    buildDocumentWithSchema();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown file type: " + fileType);
            }
            parsed = true;
        }
        catch (IOException err)
        {
            exceptions.add(new TBXException(TBXException.Priority.XMLVALID_MAJOR, err));
            ret = false;
        }
        catch (SAXException err)
        {
            exceptions.add(new TBXException(TBXException.Priority.WELLFORMED, err));
            ret = false;
        }
        catch (ParserConfigurationException err)
        {
            exceptions.add(new TBXException(TBXException.Priority.WELLFORMED, err));
            ret = false;
        }
        return ret;
    }
    
    /**
     * Build the tbx document by reference to the TBX DTD.
     *
     * @throws IOException Any unhandled I/O exceptions.
     * @throws ParserConfigurationException The XML parser is invalid for XCS.
     * @throws SAXException Parsing exceptions occured.
     */
    private void buildDocumentWithDTD() throws IOException,
        ParserConfigurationException, SAXException
    {
        InputSource insource = new InputSource(reader);
        insource.setSystemId(url.toString());
        tbxDocument = tbxParser.parse(insource);
        exceptions.addAll(tbxDocument.getParseExceptions());
    }
    
    /**
     * Build the tbx document by reference to the TBX Schema.
     *
     * @throws IOException Any unhandled I/O exceptions.
     * @throws ParserConfigurationException The XML parser is invalid for XCS.
     * @throws SAXException Parsing exceptions occured.
     */
    private void buildDocumentWithSchema() throws IOException,
        ParserConfigurationException, SAXException
    {
        InputSource insource = new InputSource(reader);
        insource.setSystemId(url.toString());
        tbxDocument = tbxParser.parse(insource);
        exceptions.addAll(tbxDocument.getParseExceptions());
    }

    /**
     * Check to see if the document is valid.
     * <p>
     * If the XML source has not been parsed then an
     * {@link java.lang.IllegalStateException} is thrown.
     *
     * @return true => the document is valid against the defined XCS.
     * @throws IOException Any unhandled I/O exceptions.
     */
    private boolean validate() throws IOException
    {
        ResourceBundle bundle = getResourceBundle();
        if (!parsed)
            throw new IllegalArgumentException(bundle.getString("Validate_NoXMLParse"));
        try
        {
            if (tbxParser != null)
            {
                exceptions.addAll(tbxDocument.getParseExceptions());
            }
            else
            {
                buildXCSDocument();
                validateAgainstXCS((TBXElement) getTBXDocument().getDocumentElement());
            }
        }
        catch (SAXException err)
        {
            exceptions.add(new TBXException(TBXException.Priority.XMLVALID_MAJOR, err));
        }
        catch (ParserConfigurationException err)
        {
            System.err.println(err);
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
                reader = null;
            }
        }
        return exceptions.isEmpty();
    }
    
    /**
     * This will build the XCS document for the special case where a built
     * TBXDocument is given to this object on construction for pure
     * validation.
     *
     * @throws IOException Any unhandled I/O exceptions.
     * @throws ParserConfigurationException The XML parser is invalid for XCS.
     * @throws SAXException Parsing exceptions occured.
     */
    private void buildXCSDocument() throws IOException,
        ParserConfigurationException, SAXException
    {
        Element root = getTBXDocument().getDocumentElement();
        Element header = (Element) root.getElementsByTagName("martifHeader").
                item(0);
        if (header.getElementsByTagName("encodingDesc").getLength() == 1)
        {
            String xcsUriStr = null;
            Element encoding = (Element) header.getElementsByTagName("encodingDesc").item(0);
            NodeList plist = encoding.getElementsByTagName("p");
        GOT_XCS:
            for (int i = 0; i < plist.getLength(); i++)
            {
                Element p = (Element) plist.item(i);
                if (p.hasAttribute("type"))
                {
                    if (p.getAttribute("type").equals("DCSName")
                        || p.getAttribute("type").equals("XCSName"))
                    {
                        xcsUriStr = p.getTextContent().trim();
                        LOGGER.info("<p type='DCSName'> is deprecated: use <p type='XCSURI'>.");
                        break GOT_XCS;
                    }
                    else if (p.getAttribute("type").equals("XCSURI"))
                    {
                        xcsUriStr = p.getTextContent().trim();
                        break GOT_XCS;
                    }
                    else if (p.getAttribute("type").equals("XCSContent"))
                    {
                        throw new UnsupportedOperationException("XCSContent location type unsupported.");
                    }
                }
            }
            
            if (xcsUriStr == null)
                throw new FileNotFoundException(
                    String.format("XCS unspecified for TBX file: %s.", url));

            try
            {
                LOGGER.info("Using XCS file: " + xcsUriStr);
                xcsDocument = new XCSDocument(xcsUriStr, resolver, config);
            }
            catch (FileNotFoundException err)
            {
                String msg = String.format("On TBX file '%s' XCS file '%s' not found.", url, xcsUriStr);
                LOGGER.info(msg);
            }
            catch (IOException err)
            {
                LOGGER.log(Level.WARNING, "Exception building XCS", err);
            }
        }
    }
    
    /**
     * @param elem The element to be validated against the XCS.
     */
    private void validateAgainstXCS(TBXElement elem)
    {
        String name = elem.getTagName();
        if (name.equals("termEntry"))
        {
            try
            {
                xcsDocument.validateTermEntry(elem);
            }
            catch (XCSValidationException err)
            {
                exceptions.add(new TBXException(TBXException.Priority.XCS, err));
            }
        }
        else
        {
            Node node = elem.getFirstChild();
            while (node != null)
            {
                if (node instanceof TBXElement)
                    validateAgainstXCS((TBXElement) node);
                node = node.getNextSibling();
            }
        }
    }
    
    /**
     * Provide a new unique ID for a term entry in this file.
     *
     * @return A string that will give a unique ID for a tag.
     */
    public String getTermEntryAutoId()
    {
        String ret = AUTO_TERMENTRY_ID_PREFIX + termEntrySeq;
        termEntrySeq++;
        return ret;
    }
    
    /**
     */
    private void buildTermEntriesMap()
    {
        Element body = getBodyElement();
        NodeList terms = body.getElementsByTagName("termEntry");
        for (int i = 0; i < terms.getLength(); i++)
        {
            Element entry = (Element) terms.item(i);
            if (entry.getAttribute("id").startsWith(AUTO_TERMENTRY_ID_PREFIX))
                entry.removeAttribute("id");
            String id = entry.getAttribute("id");
            if (id.equals(""))
            {
                id = getTermEntryAutoId();
                entry.setAttribute("id", id);
            }
            termEntries.put(id, entry);
        }
    }
    
    /**
     * This reader will allow all characters through unless the mark
     * limit will be exceeded. If there is no mark limit then all
     * characters are allowed.
     * <p>
     * This is a pre-screen reader. When it closes it will not close
     * the reader that was given to the constructor.</p>
     */
    private static class MarkLimitReader extends FilterReader
    {
        /** */
        private int maxChars;
        
        /** */
        private int count;
        
        /**
         * @param in The reader that this will read from.
         */
        public MarkLimitReader(Reader in)
        {
            super(in);
            if (!in.markSupported())
                throw new IllegalArgumentException("Reader shall support marking");
        }
        
        /** {@inheritDoc} */
        public int read() throws IOException
        {
            checkReader(1);
            int ret = in.read();
            count++;
            return ret;
        }
    
        /** {@inheritDoc} */
        public int read(char[] cbuf, int off, int len) throws IOException
        {
            int allow = (int) checkReader(len);
            int ret = in.read(cbuf, off, allow);
            count += ret;
            return ret;
        }
    
        /** {@inheritDoc} */
        public long skip(long n) throws IOException
        {
            long allow = checkReader(n);
            long ret = in.skip(allow);
            count += ret;
            return ret;
        }
    
        /** {@inheritDoc} */
        public void mark(int readAheadLimit) throws IOException
        {
            if (in == null)
                throw new IOException("Reader closed");
            maxChars = readAheadLimit;
            in.mark(readAheadLimit);
        }
    
        /** {@inheritDoc} */
        public void reset() throws IOException
        {
            if (in == null)
                throw new IOException("Reader closed");
            count = 0;
            in.reset();
        }
        
        /**
         * This will clear the mark to allow reading beyond the mark
         * limit. This is necessary since reset will always put the
         * read point back to the last mark, but the mark is never
         * removed.
         *
         * @throws IOException Any unhandled I/O exceptions.
         */
        public void clearMark() throws IOException
        {
            maxChars = -1;
        }

        /**
         * CAUTION: This will not close the reader given to the
         * constructor. This is meant to be a temporary reader.
         *
         * @throws IOException Any unhandled I/O exceptions.
         */
        public void close() throws IOException
        {
            in = null;
        }
        
        /**
         * This will check the reader that everything is OK and return
         * the maximum number of characters allowed to be read or the
         * number of characters desired whichever is smaller.
         *
         * @param n Number of characters a reader should be able to mark.
         * @return Number of characters that may be marked.
         * @throws IOException Any unhandled I/O exceptions.
         */
        private long checkReader(long n) throws IOException
        {
            if (in == null)
                throw new IOException("Reader closed");
            long ret = 0;
            if (maxChars >= 0)
            {
                if (count >= maxChars)
                    throw new IOException("Maximum marked characters read");
                if (count + n > maxChars)
                    ret = maxChars - count;
            }
            return ret;
        }
    }
}


