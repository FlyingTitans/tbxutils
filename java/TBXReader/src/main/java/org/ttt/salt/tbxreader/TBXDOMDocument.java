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

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Text;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;

/**
 * A TBX W3C DOM Document that holds the entire TBX file.
 * <p>
 * This will either build the full DOM Document for the TBX file or it will
 * fail if the TBX file is not well-formed, valid, and conformant.</p>
 * <p>
 * Before any change is possible in this document the changes will be checked
 * against the associated XCS for conformance, and if it will cause this
 * document to not conform to the XCS then the change will be rejected.</p>
 *
 * @author Lance Finn Helsten
 * @version 2.0-SNAPSHOT
 * @license Licensed under the Apache License, Version 2.0.
 */
public class TBXDOMDocument implements Document
{    
    /** URL to the TBX file to be parsed. */
    private URL url;
    
    /**
     * Create a new TBX W3C DOM Document.
     *
     * @param tbx URL to the TBX file to process.
     * @throws TBXException An XML fatal error or error occurred before the
     *  <code>martifHeader</code> element was read, or an errors occurred in
     *  building the {@link MartifHeader}.
     * @throws IOException A IOException occurred while trying to read from
     *  the TBX file.
     */
    public TBXDOMDocument(URL tbx) throws TBXException, IOException
    {
        throw new UnsupportedOperationException();
    }
    
    //===========================================
    // org.w3c.dom.Document
    //===========================================

    /** {@inheritDoc} */
    public Node adoptNode(Node source)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Attr createAttribute(String name)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public CDATASection createCDATASection(String data)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Comment createComment(String data)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public DocumentFragment createDocumentFragment()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Element createElement(String tagName)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Element createElementNS(String namespaceURI, String qualifiedName)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public EntityReference createEntityReference(String name)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public ProcessingInstruction createProcessingInstruction(String target, String data)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Text createTextNode(String data)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public DocumentType getDoctype()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Element getDocumentElement()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getDocumentURI()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public DOMConfiguration getDomConfig()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Element getElementById(String elementId)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getElementsByTagName(String tagname)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public DOMImplementation getImplementation()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getInputEncoding()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean getStrictErrorChecking()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getXmlEncoding()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean getXmlStandalone()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getXmlVersion()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node importNode(Node importedNode, boolean deep)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void normalizeDocument()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node renameNode(Node n, String namespaceURI, String qualifiedName)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setDocumentURI(String documentURI)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setStrictErrorChecking(boolean strictErrorChecking)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setXmlStandalone(boolean xmlStandalone)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setXmlVersion(String xmlVersion)
    {
        throw new UnsupportedOperationException();
    }

    
    
    //===========================================
    // org.w3c.dom.Node
    //===========================================
    
    /** {@inheritDoc} */
    public Node appendChild(Node newChild)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node cloneNode(boolean deep)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public short compareDocumentPosition(Node other)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NamedNodeMap getAttributes()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getBaseURI()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public NodeList getChildNodes()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Object getFeature(String feature, String version)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getFirstChild()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getLastChild()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getLocalName()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getNamespaceURI()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getNextSibling()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getNodeName()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public short getNodeType()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getNodeValue()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Document getOwnerDocument()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getParentNode()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getPrefix()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node getPreviousSibling()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String getTextContent()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Object getUserData(String key)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean hasAttributes()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean hasChildNodes()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node insertBefore(Node newChild, Node refChild)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean isDefaultNamespace(String namespaceURI)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean isEqualNode(Node arg)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean isSameNode(Node other)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public boolean isSupported(String feature, String version)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String lookupNamespaceURI(String prefix)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public String lookupPrefix(String namespaceURI)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void normalize()
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node removeChild(Node oldChild)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Node replaceChild(Node newChild, Node oldChild)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setNodeValue(String nodeValue)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setPrefix(String prefix)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public void setTextContent(String textContent)
    {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Object setUserData(String key, Object data, UserDataHandler handler)
    {
        throw new UnsupportedOperationException();
    }

    
}

