/*
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
    /** TBX title statement. */
    private String title;
    
    /** TBX publication statement. */
    private String publication;
    
    /** List of source descriptions. */
    private List<String> sources = new java.util.ArrayList<String>();
    
    /** List of encoding descriptions. */
    private List<String> encodings = new java.util.ArrayList<String>();
    
    /** List of revision changes. */
    private List<String> revisions = new java.util.ArrayList<String>();
    
    /**
     * @param elem The DOM element that contains all of the validated
     *  information for the martif header.
     */
    public MartifHeader(Element elem)
    {
    }
}

