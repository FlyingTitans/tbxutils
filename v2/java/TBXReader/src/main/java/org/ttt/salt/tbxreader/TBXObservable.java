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
import java.util.Observer;
import java.util.Observable;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A TBX observable, for use in an observer design pattern, that will send
 * each processed <code>termEntry</code> element as a {@link TermEntry}
 * object to all observers.
 * 
 * <h4>Objects</h4>
 * <p>
 * The following objects may be delivered to observers as the processing
 * thread operates.</p>
 * <ol>
 *     <li>{@link TermEntry} will be sent to the observers as each
 *         <code>termEntry</code> element is parsed, validated, and checked
 *         for XCS conformance.</li>
 *     <li>{@link TBXException} will be sent to the observers if there is a
 *         fatal error that is a result of a fatal error while processing
 *         the next {@link TermEntry} object. When this is sent there will
 *         be no more objects and this observable should be considered
 *         dead.</li>
 *     <li>A <code>null</code> will be sent to the observers if all the
 *         {@link TermEntry} objects have been processed and delivered.
 *         No more objects will be sent and this observable should be
 *         considered dead.</li>
 * </ol>
 *
 * <h4>TBX Problems</h4>
 * <p>
 * There are a few problems that may occur in processing a TBX or XCS file.
 * This defines how those problems will be delivered to the observers.</p>
 * <ol>
 *     <li>If the TBX file cannot be read, is in an unknown transformation
 *         format, or any other basic I/O type problems then this will fail
 *         on construction.</li>
 *     <li>If the TBX file is not well-formed or valid before the martif
 *         header completes then this will fail on construction.</li>
 *     <li>If a term entry is not well-formed then this will deliver all
 *         term entries up to that point and then will deliver an not
 *         well-formed exception.</li>
 *     <li>If a term entry is well-formed, but is not valid or XCS conformant
 *         then this will deliver the term entry object and that object will
 *         contain the exceptions.</li>
 * </ol>
 *
 * <h4>Example</h4>
 * To receive each <code>termEntry</code> element as a {@link TermEntry}
 * object, with full parsing, validation, and conformance checks in place,
 * the following would be sufficient.
 * <code><pre>
 *
 * public void update(Observable o, Object arg)
 * {
 *     TBXObservable tbxo = (TBXObservable) o;
 *     if (arg == null)
 *     {
 *         …The TBX file has completed processing.
 *     }
 *     else if (arg instanceof TBXException)
 *     {
 *         …A fatal error has occurred and processing is complete.
 *     }
 *     else if (arg instanceof TermEntry)
 *     {
 *         …Process the TermEntry
 *     }
 * }
 *
 *
 * public void initReader(URL url)
 * {
 *     TBXObservable tbxo = new TBXObservable(url, null);
 *     tbxo.addObserver(this);
 *     tbxo.start();
 * }
 * </pre></code>
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXObservable extends Observable implements Runnable
{    
    /** URL to the TBX file to be parsed. */
    private URL url;
    
    /**
     * Create a new TBX parser.
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
    public TBXObservable(URL tbx, Map<String, Object> config)
        throws TBXException, IOException
    {
        url = tbx;
        throw new UnsupportedOperationException();
    }
	
	/**
	 * Start delivering term entry objects to the observers.
	 */
	public void start()
	{
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
    
    /** {@inheritDoc} */
    public void run()
    {
        throw new UnsupportedOperationException();
    }
}

