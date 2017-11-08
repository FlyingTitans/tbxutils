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
package org.ttt.salt.editor;

import java.io.File;
import java.util.*;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class ExtensionFileFilter extends FileFilter implements Comparable
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Extension list resource key should have this suffix. */
    public static final String KEY_EXT = "_ext";

    /** Description resource key should have this suffix. */
    public static final String KEY_DESC = "_desc";

    /**
     * Create a file filter from the given resource bundle.
     *
     * @param bndl The resource bundle that contains extensions to be accepted.
     * @return A collection of of file filters based on file extensions.
     */
    public static Collection<ExtensionFileFilter> createFileFilters(ResourceBundle bndl)
    {
        Collection<ExtensionFileFilter> ret = new ArrayList<ExtensionFileFilter>();
        Enumeration propkeys = bndl.getKeys();
        while (propkeys.hasMoreElements())
        {
            String key = (String) propkeys.nextElement();
            if (key.endsWith(KEY_EXT))
            {
                String base = key.substring(0, key.lastIndexOf(KEY_EXT));
                StringTokenizer tkns = new StringTokenizer(
                        bndl.getString(key));
                String[] extns = new String[tkns.countTokens()];
                for (int i = 0; i < extns.length; i++)
                {
                     extns[i] = tkns.nextToken();
                }
                if (extns.length > 0)
                {
                    String desc;
                    try
                    {
                        desc = bndl.getString(base + KEY_DESC);
                    }
                    catch (MissingResourceException err)
                    {
                        desc = null;
                    }
                    ret.add(new ExtensionFileFilter(extns, desc));
                }
            }
        }
        return ret;
    }

    /** Description for the file. */
    private String description = "";

    /** Collection of acceptable extensions. */
    private Collection<String> filters = new ArrayList<String>();

    /**
     * @param extensions List of extensions that will be accepted.
     * @param desc Description prefix for the files.
     */
    public ExtensionFileFilter(String[] extensions, String desc)
    {
        super();
        filters.addAll(Arrays.asList(extensions));
        buildDescription(desc);
    }

    /** {@inheritDoc} */
    public boolean accept(File f)
    {
        boolean ret = false;
        if (f != null)
        {
            String extension = getExtension(f);
            if (f.isDirectory())
                ret = true;
            else
                ret = filters.contains(extension);
        }
        return ret;
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return description;
    }

    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
        boolean ret = o == this;
        if (!ret && (o != null) && (o instanceof ExtensionFileFilter))
        {
            ExtensionFileFilter p = (ExtensionFileFilter) o;
            ret = this.description == p.description;
        }
        return ret;
    }
    
    /** {@inheritDoc} */
    public int hashCode()
    {
        return description.hashCode();
    }

    /** {@inheritDoc} */
    public int compareTo(Object o)
    {
        ExtensionFileFilter p = (ExtensionFileFilter) o;
        return this.description.compareTo(p.description);
    }

    /**
     * Create the human description string.
     *
     * @param desc The prefix description string.
     */
    private void buildDescription(String desc)
    {
        StringBuffer buf = new StringBuffer(desc == null ? "(" : desc + " (");
        Iterator iter = filters.iterator();
        if (iter.hasNext())
        {
            buf.append("*.");
            buf.append(iter.next());
            while (iter.hasNext())
            {
                buf.append(", *.");
                buf.append(iter.next());
            }
        }
        buf.append(")");
        description = buf.toString();
    }

    /**
     * Return the extension portion of the file's name.
     *
     * @param f The file to extract the extension for.
     * @return The extension of the given file, empty string if there is none.
     */
    private String getExtension(File f)
    {
        String ret = "";
        if (f != null)
        {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (0 < i && i < filename.length() - 1)
                ret = filename.substring(i + 1);
        }
        return ret;
    }
}
