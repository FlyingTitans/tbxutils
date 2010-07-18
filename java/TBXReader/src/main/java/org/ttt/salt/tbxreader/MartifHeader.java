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

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
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
	private Element martifHeaderElement;
	
    /**
     * @param elem The DOM element that contains all of the validated
     *  information for the martif header.
     */
    public MartifHeader(Element elem)
    {
		martifHeaderElement = elem;
    }
	
	/**
	 * Get the TBX document title from
	 * <code>//martif/martifHeader/fileDesc/titleStmt/title</code>.
	 *
	 * @return TBX document title.
	 */
	public String getTitle()
	{
		throw new UnsupportedOperationException();
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
}

