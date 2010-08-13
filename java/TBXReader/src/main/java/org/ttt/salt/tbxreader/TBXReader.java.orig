/*
 * TermBase eXchange conformance checker library.
 * Copyright (C) 2010 Lance Finn Helsten (helsten@acm.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ttt.salt.tbxreader;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * TBXReader is the system used to read TBX files and check to see if it
 * is XML well-formed, XML valid against schema, and XCS conformant.
 * <p>
 * This is where global configuration of the TBX reader system is performed.
 * Any changes made here will effect all other TBX classes from that point
 * in time forward. If changes for a single TBX or XCS file is desired then
 * that configurtion should be given to the processor (e.g. {@link TBXObservable}
 * when it is constructed.</p>
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public final class TBXReader
{    
	/** Logger for all classes in this package. */
	static final Logger LOGGER = Logger.getLogger("org.ttt.salt.tbxreader");
	
	/** The single instance of TBXReader in the system. */
	private static TBXReader instance = new TBXReader();
	
	/**
	 * Get the singleton TBXReader in the system.
	 */
	public static TBXReader getInstance()
	{
		return instance;
	}
	
	/** Properties for this parser. */
	private Map<String, Object> properties;
	
	/** The SAX parser factory that is used to generate all XML parsers. */
	private SAXParserFactory saxfactory;
	
	/** The XPath factory used to generate all XPath expression objects. */
	private XPathFactory xpathfactory;
	
	/** The basic namespace for use with XPath. */
	private NamespaceContext defaultNamespaceContext;
	
	/**
	 * 
	 */
	private TBXReader()
	{
		properties = new java.util.HashMap<String, Object>();
		saxfactory = SAXParserFactory.newInstance();
		saxfactory.setNamespaceAware(true);
        saxfactory.setValidating(true);
	}
	
	/**
	 * Get the value of a property on this parser to control the parsing,
	 * validation, and conformance checking.
	 *
	 * @param name The name of the property to get. If the name is not known
	 *  by the parser then an {@link java.lang.IllegalArgumentException} will
	 *  be thrown.
	 * @return The value of the named property.
	 */
	public Object getProperty(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Set a property on this parser to control the parsing, validation, and
	 * conformance checking.
	 *
	 * @param name The name of the property to set. If the name is not known
	 *  by the parser then an {@link java.lang.IllegalArgumentException} will
	 *  be thrown.
	 * @param value The new value of the property. If the value is invalid for
	 *  the parser then an {@link java.lang.IllegalArgumentException} will
	 *  be thrown. If the value is not an appropriate type for the property
	 *  then an {@link java.lang.ClassCastException} will be thrown.
	 */
	public void setProperty(String name, Object value)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get an XML parser that is properly configured to parse a TBX file.
	 *
	 * @param config Configuration that will effect only the single parser.
	 *  This may be <code>null</code> or empty; values in this config will
	 *  override the global configuration.
	 */
	public SAXParser getTBXFileSAXParser(Map<String, Object> config)
	{
		SAXParser ret;
		synchronized (saxfactory)
		{
			try
			{
				//TODO: set the validation state for the parser
				//TODO: set the schema for the parser
				ret = saxfactory.newSAXParser();
			}
			catch (ParserConfigurationException err)
			{
				throw new RuntimeException("Unable to initialize XML parser.", err);
			}
			catch (SAXException err)
			{
				throw new RuntimeException("Unable to initialize XML parser.", err);
			}
		}
		return ret;
	}
	
	/**
	 * Get an XPath object.
	 *
	 * @return The XPath object for processing XPath expressions.
	 */
	public XPath getXPath()
	{
		if (xpathfactory == null)
		{
			xpathfactory = XPathFactory.newInstance();
		}
		if (defaultNamespaceContext == null)
		{
            Map<String, String> map = new java.util.HashMap<String, String>();
			defaultNamespaceContext = new SimpleNamespace("", map);
		}
		XPath xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(defaultNamespaceContext);
		return xpath;
	}
	
	/**
	 * Simplified and basic namespace context for XPath.
	 *
	 * @author Lance Finn Helsten
	 * @version 2.0-SNAPSHOT
	 * @license Licensed under the Apache License, Version 2.0.
	 */
	public class SimpleNamespace implements NamespaceContext
	{
		/** Default namespace prefix. */
		private String defPrefix;
		
		/** Default namespace URI. */
		private String defURI;
		
		/** Namespace mapping. */
		private Map<String, String> map;
		
		/**
		 * Create a new SimpleNamespace with a mapping of prefixes to namespaces.
		 *
		 * @param def Default namespace prefix.
		 * @param nsm Namespace mapping.
		 */
		public SimpleNamespace(String def, Map<String, String> nsm)
		{
			if (def == null)
				throw new NullPointerException("Default Prefix Required.");
			defPrefix = def;
			defURI = nsm.get(def);
			map = nsm;
		}
		
		/** {@inheritDoc} */
		public String getNamespaceURI(String prefix)
		{
			String ret = XMLConstants.NULL_NS_URI;
			if (prefix == null)
				throw new NullPointerException("Prefix may not be null.");
			else if (map.containsKey(prefix))
				ret = map.get(prefix);
			else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
				ret = defURI;
			else if (prefix.equals(XMLConstants.XML_NS_PREFIX))
				ret = XMLConstants.XML_NS_URI;
			else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
				ret = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
			return ret;
		}
		
		/** {@inheritDoc} */
		public String getPrefix(String namespaceURI)
		{
			String ret = "";
			if (namespaceURI == null)
				throw new NullPointerException("Namespace may not be null.");
			else if (namespaceURI.equals(XMLConstants.XML_NS_URI))
				ret = XMLConstants.XML_NS_PREFIX;
			else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
				ret = XMLConstants.XMLNS_ATTRIBUTE;
			else if (namespaceURI.equals(defURI))
				ret = XMLConstants.DEFAULT_NS_PREFIX;
			else
			{
				for (Map.Entry<String, String> e : map.entrySet())
				{
					if (namespaceURI.equals(e.getValue()))
					{
						ret = e.getKey();
						break;
					}
				}
			}
			return ret;
		}
		
		/** {@inheritDoc} */
		public Iterator getPrefixes(String namespaceURI)
		{
			Collection<String> ret = new java.util.ArrayList<String>();
			if (namespaceURI == null)
				throw new NullPointerException("Namespace may not be null.");
			else if (namespaceURI.equals(XMLConstants.XML_NS_URI))
				ret.add(XMLConstants.XML_NS_PREFIX);
			else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
				ret.add(XMLConstants.XMLNS_ATTRIBUTE);
			else
			{
				for (Map.Entry<String, String> e : map.entrySet())
				{
					if (namespaceURI.equals(e.getValue()))
						ret.add(e.getKey());
				}
			}
			return Collections.unmodifiableCollection(ret).iterator();
		}
	}
}

