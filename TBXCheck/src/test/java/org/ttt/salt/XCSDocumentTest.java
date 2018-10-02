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

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.ttt.salt.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.ttt.salt.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version 1.0
 */
public class XCSDocumentTest
{
    /*
     * TO-DO
     * 1) Add a check for invalid XCS files where file has empty picklist.
     */

    private static String SYSTEM_ID
            = "file:" + System.getProperty("user.dir") + "/";

    private static TBXResolver RESOLVER;

    private static String DXLT_XCS
            = "Demo XCS";
    
    private static XCSDocument.Key KEY_GOOD
            = new XCSDocument.Key("termNote", "abbreviatedFormFor");
    
    private static XCSDocument.Key KEY_PICKLIST
            = new XCSDocument.Key("termNote", "animacy");
    
    private static XCSDocument.Key KEY_BAD_0
            = new XCSDocument.Key("FUBAR", "TARFU");
    
    private static XCSDocument.Key KEY_BAD_1
            = new XCSDocument.Key("FUBAR", "commonNameFor");
    
    private static XCSDocument.Key KEY_BAD_2
            = new XCSDocument.Key("termNote", "TARFU");
        
    Configuration config;
    XCSDocument dcsdoc;

    @BeforeClass
    public static void classSetup() throws IOException
    {
        RESOLVER = new TBXResolver(new File(System.getProperty("user.dir")).toURI().toURL());
        LogManager.getLogManager().reset();
        Formatter simplefmt = new SimpleFormatter();
        Handler logfile = new FileHandler(System.getProperty("user.dir")
                + "/target/test-logs/"
                + "org.ttt.salt.XCSDocumentTest.log");
        logfile.setLevel(Level.FINEST);
        logfile.setFormatter(simplefmt);
        Logger.getLogger("org.ttt.salt").setLevel(Level.INFO);
        Logger.getLogger("org.ttt.salt").addHandler(logfile);
    }
    
    @Before
    public void setUp() throws Exception
    {
        Logger.getLogger("org.ttt.salt").setLevel(Level.INFO);
        config = new Configuration();
        dcsdoc = new XCSDocument(DXLT_XCS, RESOLVER, config);
    }
    
    @Test
    public void getLanguages() throws Exception
    {
        java.util.Map<String, String> map = dcsdoc.getLanguages();
        assertTrue(map != null);
        assertTrue(map.get("en").equals("English"));
        assertTrue(map.get("hu").equals("Hungarian"));
        assertTrue(map.get("fr") == null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getLanguagesModify()
    {
        java.util.Map<String, String> map = dcsdoc.getLanguages();
        map.put("fubar", "tarfu");
    }
    
    @Test
    public void getSpec() throws Exception
    {
        assertTrue(dcsdoc.getSpec(KEY_GOOD) != null);
        assertTrue(dcsdoc.getSpec(KEY_GOOD) != null);
        assertTrue(dcsdoc.getSpec(KEY_BAD_0) == null);
        assertTrue(dcsdoc.getSpec(KEY_BAD_1) == null);
        assertTrue(dcsdoc.getSpec(KEY_BAD_2) == null);
    }
    
    @Test
    public void hasSpec() throws Exception
    {
        assertTrue(dcsdoc.hasSpec(KEY_GOOD));
        assertTrue(!dcsdoc.hasSpec(KEY_BAD_0));
        assertTrue(!dcsdoc.hasSpec(KEY_BAD_1));
        assertTrue(!dcsdoc.hasSpec(KEY_BAD_2));
    }
    
    @Test
    public void getPosition() throws Exception
    {
        assertEquals("ISO12620A-02013002", dcsdoc.getPosition(KEY_GOOD));
    }
    
    @Test
    public void getDataType() throws Exception
    {
        assertEquals("noteText", dcsdoc.getDataType(KEY_GOOD));
    }
    
    @Test
    public void getTargetType() throws Exception
    {
        assertEquals("term", dcsdoc.getTargetType(KEY_GOOD));
    }
    
    @Test
    public void getPickList() throws Exception
    {
        java.util.Set set = dcsdoc.getPicklist(KEY_PICKLIST);
        assertTrue(set != null);
        assertEquals(3, set.size());
        assertTrue(set.contains("animate"));
        assertTrue(set.contains("inanimate"));
        assertTrue(set.contains("otherAnimacy"));
    }
    
    @Test(expected=IllegalStateException.class)
    public void getPickListBadState() throws Exception
    {
        dcsdoc.getPicklist(KEY_GOOD);
    }
    
    @Test
    public void isForTermComp() throws Exception
    {
    }
    
    @Test
    public void getComment() throws Exception
    {
    }
    
    @Test
    public void getLevels() throws Exception
    {
    }
}

