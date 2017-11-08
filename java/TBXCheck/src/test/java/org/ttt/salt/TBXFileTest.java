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
import java.net.URL;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.*;
import org.ttt.salt.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.ttt.salt.*;
import org.ttt.salt.dom.tbx.*;

/**
 *
 * @author  Lance Finn Helsten
 * @version $Id$
 */
public class TBXFileTest
{
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    Document doc;
    Configuration config;
        
    @BeforeClass
    public static void initialize() throws Exception
    {
        factory.setNamespaceAware(true);
        factory.setValidating(false);

/*
        java.util.logging.LogManager.getLogManager().reset();
        java.util.logging.Formatter simplefmt = new java.util.logging.SimpleFormatter();
        java.util.logging.Handler handler = new java.util.logging.ConsoleHandler();
        handler.setLevel(Level.FINEST);
        handler.setFormatter(simplefmt);
        Logger.getLogger("org.ttt.salt").addHandler(handler);
        Logger.getLogger("org.ttt.salt").setLevel(Level.INFO);
*/
    }
    
    private URL getFileURL(String name) throws IOException, URISyntaxException
    {
        URL url = getClass().getResource("/org/ttt/salt/" + name);
        assertNotNull("/org/ttt/salt/" + name + " not found.", url);
        return url;
    }
            
    @Before
    public void setUp() throws Exception
    {
        Logger.getLogger("org.ttt.salt").setLevel(Level.INFO);
        config = new Configuration();
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    @Test
    public void getType() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("ValidDTD.xml"), config);
        dv.parseAndValidate();
        assertEquals(TBXFile.Type.DTD, dv.getType());
    }
    
    @Test(expected=NoSuchElementException.class)
    public void getInvalidatingException() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("ValidDTD.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!errs.isEmpty())
        {
            System.err.println("*******************");
            System.err.println("getInvalidatingException() exceptions");
            Iterator eiter = errs.iterator();
            while (eiter.hasNext())
                ((Throwable) eiter.next()).printStackTrace();
            System.err.println("*******************");
        }
        assertTrue("Exceptions not empty", errs.isEmpty());
        assertTrue("Exceptions not empty", errs.size() == 0);
        assertTrue("Iterator hasNext() returned true", !errs.iterator().hasNext());
        errs.iterator().next();
    }

    /**
     * This will check to make sure that the DOM and SAX parsers are working
     * correctly and that the correct parser features are on.
     */
    @Test
    public void xmlParser() throws Exception
    {
        DocumentBuilder build = factory.newDocumentBuilder();
        doc = build.parse(getFileURL("ValidParser.xml").openStream());
        Element root = doc.getDocumentElement();
        
        Node node = root.getElementsByTagName("fubar").item(0);
        Node nsnode = root.getElementsByTagNameNS("http://www.oreilly.com/",
                "fubar").item(0);
        assertEquals("Incorrect root tag", "Books",
                root.getTagName());
        assertEquals("Invalid tag name", "fubar",
                root.getElementsByTagName("fubar").item(0).getLocalName());
        assertEquals("Invalid NS/tag combination", "fubar",
                root.getElementsByTagNameNS("http://www.oreilly.com/",
                        "fubar").item(0).getLocalName());
        //System.out.println(root.getFirstChild());
    }
    
    /**
     * Check that the XML document is correct without using TBXFile
     */
    @Test
    public void xmlCheckDTD() throws Exception
    {
        //factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        URL url = new File(System.getProperty("user.dir")).toURI().toURL();
        builder.setEntityResolver(new TBXResolver(url));
        builder.parse(getFileURL("ValidDTD.xml").openStream());
    }
        
    @Test(expected=IllegalArgumentException.class)
    public void fileInvalidNull() throws Exception
    {
        TBXFile dv = new TBXFile(null, config);
        dv.parseAndValidate();
    }
            
    @Test
    public void fileValid() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("ValidDTD.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw errs.get(0);
    }
    
    @Ignore
    @Test
    public void xmlFindLocalXCS() throws Exception
    {
        URL file = getFileURL("LocalXCS.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw errs.get(0);
    }

    @Ignore
    @Test
    public void xcsNameNoPublic() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("ValidXCS_NoPUBLIC.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw errs.get(0);
    }
    
    @Test(expected=StreamCorruptedException.class)
    public void preParseCorruptStreamEOF() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("CorruptStreamEOF.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw (Exception) errs.get(0).getCause();
    }
    
    @Test(expected=StreamCorruptedException.class)
    public void preParseCorruptStreamIllform() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("CorruptStreamIllform.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw (Exception) errs.get(0).getCause();
    }
    
    @Test(expected=StreamCorruptedException.class)
    public void preParseCorruptStreamNoXml() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("CorruptStreamNoXml.xml"), config);
        dv.parseAndValidate();
        List<TBXException> errs = dv.getInvalidatingExceptions();
        if (!dv.isValid() && errs.get(0) != null)
            throw (Exception) errs.get(0).getCause();
    }
        
    /**
     * Check that preparse reports invalid files
     */
    @Test
    public void preParseOverflow() throws Exception
    {
    /*
        TBXFile dv = new TBXFile(getFileURL("PreParseOverflow.xml"), config);
        dv.parseAndValidate();
        assertTrue("preParseCheck overflow not caught", !dv.preParseCheck());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        assertTrue("StreamCorruptedException not thrown",
            err.getCause() instanceof StreamCorruptedException);
    */
    }
    
    /**
     * Check that a bad DTD path will fail with the correct error message.
     */
    @Test
    public void badDTDPath() throws Exception
    {
        URL file = getFileURL("BadDTDPath.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Bad DTD path not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof FileNotFoundException))
        {
            err.printStackTrace(System.out);
            fail("Exception not FileNotFoundException");
        }
    }
    
    /**
     * Check that a bad DTD path will fail with the correct error message.
     */
    @Test
    public void invalidTagName() throws Exception
    {
        URL file = getFileURL("InvalidTagName.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid tag name not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof SAXParseException))
        {
            err.printStackTrace(System.out);
            fail("Exception not SAXParseException");
        }
    }
    
    @Test
    public void invalidDescrip() throws Exception
    {
        URL file = getFileURL("InvalidDescrip.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid description spec not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof UnknownSpecificationException))
        {
            err.printStackTrace(System.out);
            fail("Exception not UnknownSpecificationException");
        }
    }
    
    @Test
    public void invalidLanguage() throws Exception
    {
        URL file = getFileURL("InvalidLanguage.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid language spec not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof InvalidLanguageException))
        {
            err.printStackTrace(System.out);
            fail("Exception not InvalidLanguageException");
        }
    }
    
    @Test
    public void invalidPickList() throws Exception
    {
        URL file = getFileURL("InvalidPickList.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid pick in picklist not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof InvalidPickListException))
        {
            err.printStackTrace(System.out);
            fail("Exception not InvalidPickListException");
        }
    }
    
    @Test
    public void invalidLevel() throws Exception
    {
        URL file = getFileURL("InvalidLevel.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid level nesting not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof InvalidLevelsException))
        {
            err.printStackTrace(System.out);
            fail("Exception not InvalidLevelsException");
        }
    }
    
    @Test
    public void invalidLevelInheritance() throws Exception
    {
        URL file = getFileURL("InvalidLevelInheritance.xml");
        TBXFile dv = new TBXFile(file, config);
        dv.parseAndValidate();
        assertTrue("Invalid level nesting not reported", !dv.isValid());
        TBXException err = (TBXException) dv.getInvalidatingExceptions().get(0);
        if (!(err.getCause() instanceof InvalidLevelsException))
        {
            err.printStackTrace(System.out);
            fail("Exception not InvalidLevelsException");
        }
    }
    
    @Test
    public void termEntryMap() throws Exception
    {
        TBXFile dv = new TBXFile(getFileURL("ValidDTD.xml"), config);
        dv.parseAndValidate();
        dv.isValid();
        assertTrue("Term entry map is empty", !dv.getTermEntryMap().isEmpty());
        Element elem = (Element) dv.getTermEntryMap().get("ID67");
        assertTrue("No term entry for ID", elem != null);
        Element clone = (Element) elem.cloneNode(true);
        clone.removeAttribute("id");
        /*
        try
        {
            NodeList list = clone.getElementsByTagName("descrip");
            Element subj = (Element) list.item(0);
            subj.setAttribute("type", "fubar");
            fail("Invalid term entry exception not thrown");
        }
        catch (XCSValidationException err)
        {
            //success
        }
        */
    }
}

