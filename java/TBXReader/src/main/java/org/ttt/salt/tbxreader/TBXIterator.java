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

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A TBX iterator, for use in an iterator design patter, that deliver
 * each processed <code>termEntry</code> element as a {@link TermEntry}
 * object from an iterator.
 * 
 * <h4>TBX Problems</h4>
 * <ol>
 *     <li>If the TBX file cannot be read, is in an unknown transformation
 *         format, or any other basic I/O type problems then this will fail
 *         on construction.</li>
 *     <li>If the TBX file is not well-formed or valid before the martif
 *         header completes then this will fail on construction.</li>
 *     <li>If a term entry is not well-formed then this will deliver all
 *         term entries up to that point.</li>
 *     <li>If a term entry is well-formed, but is not valid or XCS conformant
 *         then this will deliver the term entry object and that object will
 *         contain the exceptions.</li>
 * </ol>
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXIterator implements Iterator<TermEntry>
{        
    /** URL to the TBX file to be parsed. */
    private URL url;
    
    /**
     * Create a new TBX iterator.
     *
     * @param tbx URL to the TBX file to process.
     * @param config TBX and XCS parser configuration. This may be null or
     *  empty and the defaults from TBXReader will be used. Any key in this
     *  map will override the defaults from TBXReader.
     * @throws TBXException An XML fatal error or error occurred before the
     *  <code>martifHeader</code> element was read, or an errors occurred in
     *  building the {@link MartifHeader}.
     * @throws IOException A IOException occurred while trying to read from
     *  the TBX file.
     */
    public TBXIterator(URL tbx,  Map<String, Object> config)
        throws TBXException, IOException
    {
        url = tbx;
        throw new UnsupportedOperationException();
    }
        
    /**
     * Get the {@link MartifHeader} for the TBX file's <code>martifHeader</code>
     * element.
     *
     * @return The martif header object.
     * @throws TBXException Contains the information on why the martif
     *  header could not be returned.
     */
    public MartifHeader getMartifHeader() throws TBXException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * This will check to see if an XML fatal exception has occurred.
     * <p>
     * This will return with no exception until all term entries that did
     * not have a fatal exception have been read from the iterator. After
     * {@link #hasNext} returns false this may throw an exception if an
     * XML fatal error occurred.</p>
     *
     * @throws TBXException The TBX exception that contains the XML fatal
     *  error that stopped processing.
     */
    public void checkFatalError() throws TBXException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Check if a new {@link TermEntry} is available.
     * <p>
     * This will block until one is available or an XML fatal error occurs.</p>
     *
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return the next {@link TermEntry} from the file.
     * <p>
     * This will block until one is available, there are no more term
     * entries, or an XML fatal error occurs. In the final two cases a
     * {@link java.lang.NoSuchElementException} shall be thrown. If there
     * was a XML fatal error then the exception cause will also be set.</p>
     * 
     * {@inheritDoc}
     */
    public TermEntry next()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Unsupported operation.
     * <p>
     * This is an unsupported operation since this is not backed by a java
     * collections type object. Each term entry that is read is forgotten
     * by the system.</p>
     *
     * {@inheritDoc}
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}

