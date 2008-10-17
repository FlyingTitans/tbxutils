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

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.DocumentImpl;
import org.ttt.salt.dom.xcs.XCSParser;
import org.ttt.salt.dom.xcs.XCSElement;


/**
 * This holds all the information in a XCS document that will allow
 * checking of DOM Element objects against the XCS.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class XCSDocument extends DocumentImpl implements Document
{
    /*
     */
    
    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /**
     * This puts together a tag with a type to give a unique key for access
     * to a XCS document.
     */
    public static class Key implements Comparable
    {
        /** Element tag id. */
        private String tag;
        
        /** Element type. */
        private String type;
        
        /** Value of the tag. */
        private String value;
        
        /**
         * Create a new key for looking up specifications in the XCSDocument.
         *
         * @param tg Element tag id for this key.
         * @param tp Element tag type for this key.
         */
        public Key(String tg, String tp)
        {
            tag = tg;
            type = tp;
            value = tag + "%" + type;
        }
        
        /**
         * Get the specification name that defines the element tag of this key.
         *
         * @return The element tag specification name in XCS.
         */
        public String getSpecName()
        {
            return tag + "Spec";
        }
        
        /**
         * Get the tag name that this key points to.
         *
         * @return The element tag id.
         */
        public String getTag()
        {
            return tag;
        }
        
        /**
         * Get the type that this key points to.
         *
         * @return The element type.
         */
        public String getType()
        {
            return type;
        }
        
        /** {@inheritDoc} */
        public boolean equals(Object obj)
        {
            boolean ret = this == obj;
            if (!ret)
            {
                if (obj instanceof Key)
                {
                    Key o = (Key) obj;
                    ret = value.equals(o.value);
                }
            }
            return ret;
        }
        
        /** {@inheritDoc} */
        public int compareTo(Object obj)
        {
            throw new UnsupportedOperationException();
        }
    }
    
    /** Logger for this package. */
    private static final Logger LOGGER = Logger.getLogger("org.ttt.salt");
    
    /** The class prefix for element editors. */
    public static final String PREFIX = "org.ttt.salt.dom.xcs.Element_";
    
    /** Set of valid XCS Meta tags. */
    private static final Set<String> XCS_TAGS = new java.util.HashSet<String>();
    
    /** Set of XCS Meta tags that are parents for inheritance. */
    private static final Set<String> XCS_PARENTS = new java.util.HashSet<String>();
    
    /** */
    private static final Set<String> LEVELS_CHECK = new java.util.HashSet<String>();
    
    /**
     */
    static
    {
        //build set of valid XCS Meta tags
        XCS_TAGS.add("admin");
        XCS_TAGS.add("adminNote");
        XCS_TAGS.add("descrip");
        XCS_TAGS.add("descripNote");
        XCS_TAGS.add("ref");
        XCS_TAGS.add("termCompList");
        XCS_TAGS.add("termNote");
        XCS_TAGS.add("transac");
        XCS_TAGS.add("transacNote");
        XCS_TAGS.add("xfef");
        XCS_TAGS.add("refObjectList");
        XCS_TAGS.add("item");
        XCS_TAGS.add("itemSet");
        
        //Tags that don't have to be matched against their description
        XCS_PARENTS.add("termCompList");
        
        //Tags that should have levels checked
        //LEVELS_CHECK.add("admin");
        //LEVELS_CHECK.add("adminNote");
        LEVELS_CHECK.add("descrip");
        //LEVELS_CHECK.add("descripNote");
        //LEVELS_CHECK.add("ref");
        //LEVELS_CHECK.add("refObjectList");
        //LEVELS_CHECK.add("termCompList");
        //LEVELS_CHECK.add("termNote");
        //LEVELS_CHECK.add("transac");
        //LEVELS_CHECK.add("transacNote");
        //LEVELS_CHECK.add("xref");
    }
    
    /** Timestampt when document was created. */
    private final Date created = new Date();
        
    /** */
    private SortedMap<String, String> languages;
    
    /** */
    private final Map<Key, Element> specNodes = new java.util.HashMap<Key, Element>();
    
    /** */
    private final Map<Key, Set<String>> levels = new java.util.HashMap<Key, Set<String>>();
    
    /** */
    private final Map<String, SortedSet<String>> typeSets = new java.util.HashMap<String, SortedSet<String>>();
    
    /**
     * Create an empty XCS document.
     */
    public XCSDocument()
    {
    }
    
    /**
     * Create an XCS document for the third stage TBX XCS validation.
     *
     * @param xcsURI The XCS's URI string. This may be a URI name or a URL
     *  path. If it is a URI name it will be resolved to a URL if the name
     *  is known, otherwise it will be assumed to be a URL.
     * @throws IOException Any I/O exceptions building this document.
     * @throws ParserConfigurationException Problems with building the parser.
     * @throws SAXException Any parse exceptions building this document.
     */
    public XCSDocument(String xcsURI) throws IOException,
        ParserConfigurationException, SAXException
    {
        this(xcsURI, new TBXResolver());
    }
    
    /**
     * Create an XCS document for the third stage TBX XCS validation.
     *
     * @param xcsURI The XCS's URI string. This may be a URI name or a URL
     *  path. If it is a URI name it will be resolved to a URL if the name
     *  is known, otherwise it will be assumed to be a URL.
     * @param resolver The resolver this XCS document should use to find
     *  files.
     * @throws IOException Any I/O exceptions building this document.
     * @throws ParserConfigurationException Problems with building the parser.
     * @throws SAXException Any parse exceptions building this document.
     */
    public XCSDocument(String xcsURI, TBXResolver resolver) throws IOException,
        ParserConfigurationException, SAXException
    {   //Open the input for the XCS file
        //Parse the XCS file
        //NOTDONE: should I switch to DOM2
        try
        {
            LOGGER.info("Parsing XCS file: " + xcsURI);
            InputSource source = resolver.resolveEntity(xcsURI, xcsURI);
            XCSParser parser = new XCSParser();
            parser.parse(this, source);
            languages = buildLangMap();
            LOGGER.info("    Success");
        }
        catch (SAXParseException err)
        {   //TODO: Need to put string in properties file for i18l.
            LOGGER.severe(String.format("SAXParseException on XCS URI: XCSName %s", xcsURI));
            LOGGER.log(Level.FINE, "SAXParseException on XCS URI in org.ttt.salt.XCSDocument", err);
            String msg = String.format("The XCS file '%s' is invalid. It failed XML validation.", xcsURI);
            throw new SAXParseException(msg, err.getPublicId(), err.getSystemId(),
                        err.getLineNumber(), err.getColumnNumber(), err);
        }
        catch (SAXException err)
        {
            LOGGER.severe(String.format("SAXException on XCS URI: XCSName %s", xcsURI));
            LOGGER.log(Level.FINE, "SAXException on XCS URI in org.ttt.salt.XCSDocument", err);
            throw err;
        }
    }
    
    /**
     * Create a new XCS element for this document. This should be called
     * instead of {@link org.w3c.dom.Document#createElement} when building a
     * new element because it allow for the location of the element to be
     * recorded.
     *
     * @param tagName The name of the element type to instantiate.
     * @param loc The {@link org.xml.sax.Locator} when this element tag is
     *  encountered.
     * @throws SAXParseException Any exceptions that occur will be wrapped in
     *  this exception.
     * @return The newly created XCS element.
     * @see #createElement
     */
    @SuppressWarnings("unchecked")
    public XCSElement createXCSElement(String tagName, Locator loc) throws SAXParseException
    {
        if (errorChecking && !isXMLName(tagName, true))
            super.createElement(tagName);   //let the superclass throw the error
        XCSElement ret = null;
        try
        {
            try
            {
                Class<XCSElement> clazz = (Class<XCSElement>) Class.forName(PREFIX + tagName,
                                            true, getClass().getClassLoader());
                Constructor<XCSElement> cstrct = clazz.getConstructor(XCSDocument.class,
                    String.class, Locator.class);
                ret = cstrct.newInstance(this, tagName, loc);
            }
            catch (ClassNotFoundException err)
            {
                if (!XCSParser.KNOWN_MISSING.contains(tagName))
                    Logger.getLogger("org.ttt.salt.dom.xcs").log(Level.INFO, "Unknown XCS Element: {0}", tagName);
                ret = new XCSElement(this, tagName, loc);
            }

            if (tagName.equals("TBXXCS"))
                docElement = ret;
        }
        catch (NoSuchMethodException err)
        {
            Logger.getLogger("org.ttt.salt.dom.xcs").log(Level.SEVERE, "Invalid XCS Class: {0}", tagName);
            throw new SAXParseException("Invalid XCS Class", loc, err);
        }
        catch (InstantiationException err)
        {
            Logger.getLogger("org.ttt.salt.dom.xcs").log(Level.SEVERE, "XCS class not concrete: {0}", tagName);
            throw new SAXParseException("XCS class not concrete", loc, err);
        }
        catch (IllegalAccessException err)
        {
            Logger.getLogger("org.ttt.salt.dom.xcs").log(Level.SEVERE, "XCS constructor not accessible on {0}", tagName);
            throw new SAXParseException("XCS constructor not accessible", loc, err);
        }
        catch (InvocationTargetException err)
        {
            Logger.getLogger("org.ttt.salt.dom.xcs").log(Level.SEVERE, "XCS element {0} creation error", tagName);
            throw new SAXParseException("XCS element creation error", loc, err);
        }
        return ret;
    }
    
    /** {@inheritDoc} */
    public Element createElement(String tagName)
    {
        try
        {
            return createXCSElement(tagName, null);
        }
        catch (SAXParseException err)
        {   //It is not appropriate to place this into a DOMException
            throw new IllegalStateException(String.format("Error creating XCSElement: %s", tagName), err);
        }
    }
    
    /**
     * Get the name of the XCS file, this is for convienience in using this
     * document.
     *
     * @return The name of the XCS file.
     */
    public String getXCSName()
    {
        Element root = getDocumentElement();
        return root.getAttribute("name");
    }
    
    /**
     * Get the version of the XCS file, this is for convienience in using this
     * document.
     *
     * @return The version of the XCS file.
     */
    public String getXCSVersion()
    {
        Element root = getDocumentElement();
        return root.getAttribute("version");
    }
    
    /**
     * Get the language of the XCS file, this is for convienience in using this
     * document.
     *
     * @return The language of the XCS file.
     */
    public String getXCSLang()
    {
        Element root = getDocumentElement();
        return root.getAttribute("lang");
    }
    
    /**
     * Get the title of the XCS file, this is for convienience in using this
     * document.
     *
     * @return The title of the XCS file.
     */
    public String getXCSTitle()
    {
        String ret = "";
        Element root = getDocumentElement();
        Element header = (Element) root.getElementsByTagName("header").item(0);
        Element title = (Element) header.getElementsByTagName("title").item(0);
        Node node = title.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.TEXT_NODE)
            {
                ret = ((Text) node).getWholeText();
                break;
            }
            node = node.getNextSibling();
        }
        return ret;
    }
    
    /**
     * Get a list of languages in this XCS.
     *
     * @return List of languages defined in the XCS.
     */
    public SortedMap<String, String> getLanguages()
    {
        return languages;
    }
    
    /**
     * Given a particular tag name give all the valid type attribute values
     * for that tag. If tag is dependent on parenttag then further restrict
     * the set.
     *
     * @param tag The elmement tag id to get spec types.
     * @param parenttag The element tag's parent element tag id.
     * @return Set of specification types in this XCS.
     */
    public SortedSet<String> getSpecTypes(String tag, String parenttag)
    {
        SortedSet<String> ret = typeSets.get(tag);
        if (ret == null)
        {
            ret = new java.util.TreeSet<String>();
            Element datcat = getNamedElement(getDocumentElement(), "datCatSet", 0);
            NodeList nl = datcat.getElementsByTagName(tag + "Spec");
            for (int i = 0; i < nl.getLength(); i++)
            {
                Element elem = (Element) nl.item(i);
                ret.add(elem.getAttribute("name"));
            }
            typeSets.put(tag, ret);
        }
        return ret;
    }
    
    /**
     * Check to see if the given tag is a valid XCS element tag.
     *
     * @param tag The elmement tag id to check.
     * @return true => the given tag is valid XCS element tag.
     */
    public static boolean isXcsTag(String tag)
    {
        return XCS_TAGS.contains(tag);
    }
    
    /**
     * Check to see if the key has a specifiation in this XCS.
     *
     * @param key The key to check for specifications.
     * @return true => a specification for the key is in this XCS.
     */
    public boolean hasSpec(Key key)
    {
        return specNodes.containsKey(key)
            || (getSpec(key) != null);
    }
    
    /**
     * Get the raw Element specification for the key or return null if
     * the XCS document does not have a specification for the key.
     *
     * @param key The key to get the specifications for.
     * @return The element that is the specification for the given key.
     */
    public Element getSpec(Key key)
    {
        Element ret = specNodes.get(key);
        if (ret == null)
        {
            Element datcat = getNamedElement(getDocumentElement(), "datCatSet", 0);
            NodeList nl = datcat.getElementsByTagName(key.getSpecName());
        FIND_NAME:
            for (int i = 0; i < nl.getLength(); i++)
            {
                Element elem = (Element) nl.item(i);
                String name = elem.getAttribute("name");
                if (name.equals(key.getType()))
                {
                    specNodes.put(key, elem);
                    ret = elem;
                    break FIND_NAME;
                }
            }
        }
        return ret;
    }
    
    /**
     * Validate an XCS element against its XCS specifications.
     *
     * @param elem The XCS element to be validated.
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    public void validateXCSElement(Element elem) throws XCSValidationException
    {
        String name = elem.getTagName();
        if (!isXcsTag(name))
            throw new IllegalArgumentException("Element is not a XCS element: " + name);
        String parent = elem.getParentNode().getNodeName();
        Key key = new Key(name, elem.getAttribute("type"));
        if (!hasSpec(key))
            throw new UnknownSpecificationException(elem);
        doesElementMatchSpec(elem, key);
        isElementAtProperLevel(elem, key);
    }
    
    /**
     * Validate a term entry element.
     *
     * @param elem The term entry element element to be validated.
     * @throws XCSValidationException The exception that describes any validation
     *  problems.
     */
    public void validateTermEntry(Element elem) throws XCSValidationException
    {
        if (!elem.getTagName().equals("termEntry"))
        {
            throw new IllegalArgumentException(
                "Element is not a termEntry: " + elem.getTagName());
        }
        validateTermEntry0(elem);
    }
    
    /**
     * Does the actual term entry recursive validation.
     *
     * @param elem The term entry element element to be validated.
     * @throws XCSValidationException The exception that describes any validation
     *  problems.
     */
    private void validateTermEntry0(Element elem) throws XCSValidationException
    {
        Node node = elem.getFirstChild();
        while (node != null)
        {
            if (node instanceof Element)
            {
                Element e = (Element) node;
                String name = e.getTagName();
                if (isXcsTag(name))
                    validateXCSElement(e);
                else
                    validateTermEntry0(e);
                if (elem.hasAttribute("xml:lang"))
                {
                    String lang = elem.getAttribute("xml:lang");
                    if (!languages.containsKey(lang))
                        throw new InvalidLanguageException(elem);
                }
                else if (elem.hasAttribute("lang"))
                {
                    String lang = elem.getAttribute("lang");
                    if (!languages.containsKey(lang))
                        throw new InvalidLanguageException(elem);
                }
            }
            node = node.getNextSibling();
        }
    }
    
    /**
     * @throws XCSValidationException The exception that describes any validation
     *  problems.
     */
    private void doesElementMatchSpec(Element elem, Key key)
            throws XCSValidationException
    {
        try
        {
            if (!XCS_PARENTS.contains(elem.getNodeName()))
            {
                String name = "check_" + getDataType(key);
                Class[] parm = {Class.forName("org.w3c.dom.Element")};
                Method meth = getClass().getDeclaredMethod(name, parm);
                Object[] args = {elem};
                meth.invoke(this, args);
            }
        }
        catch (NoSuchMethodException err)
        {
            throw new IllegalStateException(
                    "Unknown data type: " + key.getType()
                    + " in tag <" + key.getTag() + ">");
        }
        catch (InvocationTargetException err)
        {
            Throwable t = err.getTargetException();
            if (t instanceof XCSValidationException)
                throw (XCSValidationException) t;
            else if (t instanceof RuntimeException)
                throw (RuntimeException) t;
            else if (t instanceof Error)
                throw (Error) t;
            else if (t instanceof Exception)
                throw new UnsupportedOperationException(
                    "Unknown Exception: " + t.getClass().getName() + ").");
        }
        catch (Throwable err)
        {
            throw new UnsupportedOperationException(
                "Caught " + err.getClass().getName());
        }
    }
    
    /**
     * @param key The key to the <em>contents</em> that contains the datcatId.
     */
    public String getPosition(Key key)
    {
        Element spec = getSpec(key);
        return spec.getAttribute("datcatId");
    }
    
    /**
     * @param key The key to the <em>contents</em> that contains the datatype.
     */
    public String getDataType(Key key)
    {
        Element contents = getContents(key);
        String ret = contents.getAttribute("datatype");
        if ("".equals(ret))
            ret = "basicText";
        return ret;
    }
    
    /**
     * @param key The key to the <em>contents</em> that contains the targetType.
     */
    public String getTargetType(Key key)
    {
        Element contents = getContents(key);
        return contents.getAttribute("targetType");
    }
    
    /**
     * This will return a set of string objects that is in a picklist
     * type contents tag.
     *
     * @param key The key to the <em>contents</em> that contains the picklist.
     * @param The set of strings in the picklist.
     * @throws IllegalStateException If the key does not refer to a
     *  spec that is of datatype "picklist".
     */
    public Set<String> getPicklist(Key key)
    {
        Set<String> ret = new java.util.HashSet<String>();
        Element contents = getContents(key);
        if (!contents.getAttribute("datatype").equals("picklist"))
            throw new IllegalStateException("datatype != picklist");
        String val = contents.getFirstChild().getNodeValue();
        StringTokenizer tok = new StringTokenizer(val);
        while (tok.hasMoreElements())
            ret.add(tok.nextToken());
        return ret;
    }
    
    /**
     */
    public boolean isForTermComp(Key key)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get the set of comments for the key.
     *
     * @param key Key to the specification that contains a comment.
     * @return The desired comment.
     */
    public String getComment(Key key)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get a <em>contents</em> that is specified by the specific key. This will
     * only return the first <em>contents</em> element if there is more than
     * one.
     *
     * @param key Key to the specification that contains <em>contents</em>
     *  elements.
     * @return The element that contains the <em>contents</em>
     */
    public Element getContents(Key key)
    {
        Element spec = getSpec(key);
        NodeList nl = spec.getElementsByTagName("contents");
        return (Element) nl.item(0);
    }
    
    /**
     * Check that the tag is at an allowed level. If the tag should
     * be checked.
     *
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void isElementAtProperLevel(Element elem, Key key)
        throws XCSValidationException
    {
        if (LEVELS_CHECK.contains(key.getTag()))
        {
            Set<String> lvls = getLevels(key);
            if (lvls == null)
                throw new IllegalStateException("Invalid XCS key for levels");
            Element pp = elem;
        SEARCH_TO_BODY:
            while (!pp.getTagName().equals("body"))
            {
                String tag = pp.getTagName();
                if (tag.equals("ntig") || tag.equals("tig"))
                {
                    if (lvls.contains("term"))
                        break SEARCH_TO_BODY;
                    else
                        throw new InvalidLevelsException(elem);
                }
                else if (tag.equals("langSet"))
                {
                    if (lvls.contains("langSet"))
                        break SEARCH_TO_BODY;
                    else
                        throw new InvalidLevelsException(elem);
                }
                else if (tag.equals("termEntry"))
                {
                    if (lvls.contains("termEntry"))
                        break SEARCH_TO_BODY;
                    else
                    {
                        System.err.println(lvls);
                        throw new InvalidLevelsException(elem);
                    }
                }
                pp = (Element) pp.getParentNode();
            }
        }
    }
    
    /**
     * Get the set of allowable parent tags.
     *
     * @param key The primary key to look up levels set on.
     * @return Set of all allowable parent tag names. If no levels tag
     *  specified this returns null.
     */
    private Set<String> getLevels(Key key)
    {
        String tag = key.getTag();
        if (!levels.containsKey(key))
        {
            Element spec = getSpec(key);
            StringBuffer buf = new StringBuffer();
            NodeList nodes = spec.getElementsByTagName("levels");
            Element lvlselem = (Element) nodes.item(0);
            String levelstr = lvlselem.getTextContent();
            String[] lvls = levelstr.split("\\s+");
            Set<String> set = new java.util.HashSet<String>();
            for (int i = 0; i < lvls.length; i++)
                set.add(lvls[i]);
            if (set.isEmpty())
            {
                set.add("termEntry");
                set.add("langSet");
                set.add("term");
            }
            levels.put(key, Collections.unmodifiableSet(set));
        }
        return levels.get(key);
    }
    
    /**
     * Build a language code to language name map.
     *
     * @return Map of language codes to language names.
     */
    private SortedMap<String, String> buildLangMap()
    {
        SortedMap<String, String> ret = new java.util.TreeMap<String, String>();
        Element langs = getNamedElement(getDocumentElement(), "languages", 0);
        if (langs != null)
        {
            NodeList langInfos = langs.getElementsByTagName("langInfo");
            for (int i = 0; i < langInfos.getLength(); i++)
            {
                Element langInfo = (Element) langInfos.item(i);
                Element langCodeElem = getNamedElement(langInfo, "langCode", 0);
                Text langCode = getTextNode(langCodeElem, 0);
                Element langNameElem = getNamedElement(langInfo, "langName", 0);
                Text langName = getTextNode(langNameElem, 0);
                ret.put(langCode.getData(), langName.getData());
            }
        }
        return Collections.unmodifiableSortedMap(ret);
    }
    
    /**
     * Utility method to search for the nth named node.
     *
     * @param elem Element to search for a named node.
     * @param name The name of the element tag.
     * @param index The index of the desired named node in the ordered set of
     *  all named nodes in elem.
     * @return The {@link org.w3c.dom.Element} node found.
     */
    private Element getNamedElement(Element elem, String name, int index)
    {
        Element ret = null;
        NodeList nl = elem.getElementsByTagName(name);
        if ((nl.getLength() > index) && (nl.item(index) instanceof Element))
            ret = (Element) nl.item(index);
        return ret;
    }
    
    /**
     * Utility method to search for the nth text node.
     *
     * @param elem Element to search for a text node.
     * @param index The index of the desired text node in the ordered set of
     *  all text nodes in elem.
     * @return The {@link org.w3c.dom.Text} node found.
     */
    private Text getTextNode(Element elem, int index)
    {
        Node node = elem.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.TEXT_NODE)
                index--;
            if (index < 0)
                break;
            node = node.getNextSibling();
        }
        return (Text) node;
    }
    
//CHECKSTYLE: MethodName OFF
    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_geoList(Element elem) throws XCSValidationException
    {
        throw new UnsupportedOperationException("geoList");
    }
    
    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_picklist(Element elem) throws XCSValidationException
    {
        Key key = new Key(elem.getTagName(), elem.getAttribute("type"));
        Set pick = getPicklist(key);
        NodeList nodes = elem.getChildNodes();
        Node node = elem.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() != Node.TEXT_NODE)
                throw new InvalidSpecificationException(elem);
            if (!pick.contains(node.getNodeValue()))
                throw new InvalidPickListException(elem, node.getNodeValue());
            node = node.getNextSibling();
        }
    }

    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_plainText(Element elem) throws XCSValidationException
    {
        NodeList nodes = elem.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.TEXT_NODE)
                throw new InvalidSpecificationException(elem);
        }
    }
    
    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_basicText(Element elem) throws XCSValidationException
    {
        Node node = elem.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.TEXT_NODE)
            {
                //no op
            }
            else if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                String name = node.getNodeName();
                if (!name.equals("hi"))
                    throw new InvalidSpecificationException(elem);
            }
            node = node.getNextSibling();
        }
    }
    
    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_noteText(Element elem) throws XCSValidationException
    {
        boolean ret = true;
        Node node = elem.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.TEXT_NODE)
            {
                //no op
            }
            else if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                String name = node.getNodeName();
                boolean valid = name.equals("hi")
                        || name.equals("foreign")
                        || name.equals("bpt")
                        || name.equals("ept")
                        || name.equals("it")
                        || name.equals("ph")
                        || name.equals("ut");
                if (!valid)
                    throw new InvalidSpecificationException(elem);
            }
            node = node.getNextSibling();
        }
    }
    
    /**
     * @throws XCSValidationException Exception that describes validation
     *  problems.
     */
    private void check_elements(Element elem) throws XCSValidationException
    {
        throw new UnsupportedOperationException("elements");
    }
//CHECKSTYLE: MethodName ON
}
