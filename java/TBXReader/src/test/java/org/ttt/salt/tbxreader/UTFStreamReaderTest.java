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

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 */
public class UTFStreamReaderTest
{
	public static final String teststring
		= "abcdefghijklmnopqrstuvwxyz"
		+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		+ "0123456789";
	
    @BeforeClass
    public static void initialize() throws Exception
    {
        TBXReaderTest.configureLoggers("UTFStreamReaderTest");
        //TBXParser.PARSE_LOG.setLevel(java.util.logging.Level.FINEST);
    }
    
    public String readTestString(String enc, String prefix) throws Exception
    {
		String prefixstr = teststring;
		if (prefix != null)
			prefixstr = prefix + prefixstr;
		byte[] bytes = prefixstr.getBytes(enc);
		InputStream in = new java.io.ByteArrayInputStream(bytes);
		Reader reader = new UTFStreamReader(in);
		char[] chars = new char[teststring.length() * 4];
		int count = reader.read(chars);
		return new String(chars, 0, count);
    }
		
	@Test
	public void utf8() throws Exception
	{
		assertEquals(teststring, readTestString("UTF-8", null));
	}
	
	@Test
	public void utf8_BOM() throws Exception
	{
		assertEquals(teststring, readTestString("UTF-8", "\ufeff"));
	}
	
	@Test
	public void utf8_2byte() throws Exception
	{
		assertEquals("\u00A2" + teststring, readTestString("UTF-8", "\u00A2"));
	}	
	
	@Test
	public void utf8_3byte() throws Exception
	{
		assertEquals("\u20AC" + teststring, readTestString("UTF-8", "\u20AC"));
	}	
	
	@Test
	public void utf8_4byte() throws Exception
	{
		assertEquals("\u024B62" + teststring, readTestString("UTF-8", "\u024B62"));
	}	
	
	@Test
	public void utf16() throws Exception
	{
		assertEquals(teststring, readTestString("UTF-16", null));
	}
	
	@Test
	public void utf16le() throws Exception
	{
		assertEquals(teststring, readTestString("UTF-16le", "\ufeff"));
	}
	
	@Test
	public void utf16be() throws Exception
	{
		assertEquals(teststring, readTestString("UTF-16be", "\ufeff"));
	}
	
	//===========================================
	// Error cases
	//===========================================
			
	@Test(expected=UnsupportedEncodingException.class)
	public void utf16le_BadBOMff() throws Exception
	{
		readTestString("UTF-16le", "\uffff");
	}

	@Test(expected=UnsupportedEncodingException.class)
	public void utf16le_BadBOMfe() throws Exception
	{
		readTestString("UTF-16le", "\ufefe");
	}

	@Test(expected=UnsupportedEncodingException.class)
	public void utf16be_BadBOMff() throws Exception
	{
		readTestString("UTF-16be", "\uffff");
	}

	@Test(expected=UnsupportedEncodingException.class)
	public void utf16be_BadBOMfe() throws Exception
	{
		readTestString("UTF-16be", "\ufefe");
	}
}

