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

import org.xml.sax.Locator;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class Element_martif extends TBXElement
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /**
     * @param ownerDoc The TBXDocument this element is created in.
     * @param name The element tag id.
     * @param loc The SAX locator that describes the start tag location.
     */
    public Element_martif(TBXDocument ownerDoc, String name, Locator loc)
    {
        super(ownerDoc, name, loc);
    }
}
