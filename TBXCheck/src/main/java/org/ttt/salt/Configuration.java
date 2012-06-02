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

import org.xml.sax.EntityResolver;

/**
 * This holds validation and compliance options for working with TBX and XCS
 * documents.
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public class Configuration implements Cloneable
{
    /** Should language code compliance check be performed. */
    private boolean checklang = true;
    
    /** Should each termEntry be checked as it is built. */
    private boolean checkeachterm = true;
	
	/** Custom entity resolver for the XCS file, if required. */
	private EntityResolver customEntityResolver = null;
    
    /**
     * Construct the options object.
     */
    public Configuration()
    {
    }
    
    /**
     * Set the check language flag.
     *
     * @param v The new value for the flag.
     */
    public void setCheckLang(boolean v)
    {
        checklang = v;
    }
    
    /**
     * Test the check language flag.
     *
     * @return The current value of the flag.
     */
    public boolean getCheckLang()
    {
        return checklang;
    }
    
    /**
     * Set the term by term validation flag.
     *
     * @param v The new value for the flag.
     */
    public void setCheckEachTerm(boolean v)
    {
        checkeachterm = v;
    }
    
    /**
     * Test the term by term validation flag.
     *
     * @return The current value of the flag.
     */
    public boolean getCheckEachTerm()
    {
        return checkeachterm;
    }
    
	/**
	 * Set the custom EntityResolver.
	 *
	 * @param er The new value for the resolver.
	 */
	public void setCustomEntityResolver(EntityResolver er)
	{
		customEntityResolver = er;
	}
	
	/**
	 * Get the custom EntityResolver.
	 *
	 * @return The current value of the resolver.
	 */
	public EntityResolver getCustomEntityResolver()
	{
		return customEntityResolver;
	}
	
    /** {@inheritDoc} */
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException err)
        {   //This is a linker error
            Error e = new InternalError();
            e.initCause(err);
            throw e;
        }
    }
}

