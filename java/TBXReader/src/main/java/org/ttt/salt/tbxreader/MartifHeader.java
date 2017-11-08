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

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * This represents the entire TBX martif header from a TBX file.
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class MartifHeader
{
	/** The W3C DOM Element that holds the <code>martifHeader</code> information. */
	private Element element;
	
	/** The single XPath expression handler I will use for this header. */
	private XPath xpath;
	
    /**
     * @param elem The DOM element that contains all of the validated
     *  information for the martif header.
     */
    public MartifHeader(Element elem)
    {
		element = elem;
		xpath = TBXReader.getInstance().getXPath();
    }
	
	/**
	 * Get the TBX document title from
	 * <code>//martif/martifHeader/fileDesc/titleStmt/title</code>.
	 *
	 * @return TBX document title.
	 */
	public String getTitle()
	{
		return getXPathString("//title");
	}
	
	/**
	 * Get the TBX publication statement from
	 * <code>//martif/martifHeader/fileDesc/publicationStmt</code>.
	 *
	 * @return TBX publication statement.
	 */
	public String getPublicationStatement()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the TBX sources from
	 * <code>//martif/martifHeader/fileDesc/sourceDesc</code>.
	 *
	 * @return List of sources.
	 */
	public List<String> getSources()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the TBX encodings from
	 * <code>//martif/martifHeader/encodingDesc</code>.
	 *
	 * @return List of encodings.
	 */
	public List<String> getEncodings()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Get the TBX revision from
	 * <code>//martif/martifHeader/fileDesc/revisionDesc</code>.
	 *
	 * @return List of revisions.
	 */
	public List<String> getRevisions()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Evaluate an XPath expression and return a node.
	 *
	 * @param exp The XPath expression that should result in a node value.
	 * @return The value of the expression.
	 */
	private Node getXPathNode(String exp)
	{
		try
		{
			return (Node) xpath.evaluate(exp, element, XPathConstants.NODE);
		}
		catch (XPathExpressionException err)
		{
			TBXReader.LOGGER.severe("Invalid Node XPath expression: " + exp);
			throw new RuntimeException("Invalid Node XPath expression.", err);
		}
	}
	 
	
	/**
	 * Evaluate an XPath expression in the martif header context.
	 *
	 * @param exp The XPath expression that should result in a string value.
	 * @return The value of the expression.
	 */
	private String getXPathString(String exp)
	{
		try
		{
			return xpath.evaluate(exp, element);
		}
		catch (XPathExpressionException err)
		{
			TBXReader.LOGGER.severe("Invalid String XPath expression: " + exp);
			throw new RuntimeException("Invalid String XPath expression.", err);
		}
	}
}

