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
package org.ttt.salt.editor.xmleditor;

/**
 * This will load all the editor kits and classes which they need.
 * <p>
 * The primary purpose is to increase security on the editor classes.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class EditorClassLoader extends ClassLoader
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** singlton instance of this loader */
    private static EditorClassLoader instance;
    
    /**
     */
    protected EditorClassLoader()
    {
        super();
    }
    
    /**
     */
    protected EditorClassLoader(ClassLoader parent)
    {
        super(parent);
    }
    
    /**
     */
    public static EditorClassLoader getInstance()
    {
        if (instance == null)
        {
            instance = new EditorClassLoader();
        }
        return instance;
    }
    
    /** {@inheritDoc} */
    public Class loadClass(String name) throws ClassNotFoundException
    {
        return super.loadClass(name);
    }
}

