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
package org.ttt.salt.editor.tbxedit;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.ttt.salt.editor.TBXEditor;
import org.flyingtitans.xml.ObservableElement;
import org.flyingtitans.xml.TreeViewMenuBar;

/**
 * @author Lance Finn Helsten
 * @version $Id$
 */
public class TBXWindow extends org.flyingtitans.xml.TreeView
    implements Observer
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";
    
    /** */
    private static final Map<String, String> TAGID = new java.util.HashMap<String, String>();
    
    /** */
    private static final String[] NOEXPAND =
    {
        "termEntry",
        "p"
    };

    /** Resource bundle that is used by all menus. */
    private static ResourceBundle bundle;
    
    /**
     */
    static
    {
        TAGID.put("p", "type");
        TAGID.put("termEntry", "id");
    }
    
    /**
     * @param ptag The parent tag id.
     * @param ctag The child tag id.
     * @return The insertion tag key.
     */
    private static String makeInsertKey(String ptag, String ctag)
    {
        return ptag + "_" + ctag;
    }
    
    /**
     * @return The {@link java.util.ResourceBundle} to be used for localization
     *  resources.
     */
    public static ResourceBundle getBundle()
    {
        return bundle;
    }
    
    /** Menu Bar controller. */
    private TBXMenuBar mbar;
    
    /** This provides for direct editing of the XML. */
    private TBXEditor editor;
    
    /** */
    private final Collection<JFrame> subWindows = new ArrayList<JFrame>();
    
    /** */
    private final Map<String, TreeViewMenuBar.Tag> nameToTag
            = new java.util.HashMap<String, TreeViewMenuBar.Tag>();
    
    /**
     * This will create a new editor with the given File as the original.
     *
     * @param   e TBXEditor to extract the JEditorPane from.
     * @throws  IOException Any I/O exceptions that occur.
     */
    public TBXWindow(TBXEditor e) throws IOException
    {
        super((ObservableElement) e.getTBXDocument().getDocumentElement(),
                TAGID, NOEXPAND);
        editor = e;
        editor.addObserver(this);
        editor.getTBXDocument().addObserver(this);
        addWindowListener(org.ttt.salt.editor.Main.getInstance());
        mbar = new TBXMenuBar(this);
        setJMenuBar(mbar.getJMenuBar());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle((String) editor.getProperty(TBXEditor.TITLE_PROPERTY));
        positionFrame();
    }

    /**
     * Send a message to close this window.
     */
    public final void close()
    {
        this.dispatchEvent(
            new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Get the JComponent that should be used as the parent for secondary
     * windows.
     *
     * @return The root component for all secondary windows.
     */
    public final JComponent getPrimaryComponent()
    {
        return getRootPane();
    }
    
    /**
     * @return The menu bar for this window.
     */
    public final TBXMenuBar getTBXMenuBar()
    {
        return mbar;
    }
    
    /**
     * @return Editor in use in this window.
     */
    public final TBXEditor getTBXEditor()
    {
        return editor;
    }

    /** {@inheritDoc} */
    public void windowOpened(WindowEvent evt)
    {
        JFrame win = (JFrame) evt.getWindow();
        if (win != this)
            subWindows.add(win);
        super.windowOpened(evt);
    }

    /** {@inheritDoc} */
    public void windowClosed(WindowEvent evt)
    {
        JFrame win = (JFrame) evt.getWindow();
        if (win != this)
        {
            win.removeWindowListener(this);
            subWindows.remove(win);
        }
        super.windowClosed(evt);
    }

    /** {@inheritDoc} */
    public void windowClosing(WindowEvent evt)
    {
        JFrame win = (JFrame) evt.getWindow();
        if (win == this)
        {
            Iterator<JFrame> iter = subWindows.iterator();
            while (iter.hasNext())
            {
                JFrame frame = iter.next();
                frame.toFront();
                frame.dispatchEvent(
                    new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
            if (editor.isDirty())
            {
                String msg = getBundle().getString("SaveAsk");
                Object[] parms = {editor.getProperty(TBXEditor.TITLE_PROPERTY)};
                int res = JOptionPane.showConfirmDialog(getPrimaryComponent(),
                        MessageFormat.format(msg, parms),
                        " ", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                switch (res)
                {
                    case JOptionPane.OK_OPTION:
                        getTBXEditor().save(this);
                        setVisible(false);
                        dispose();
                        break;
                    case JOptionPane.NO_OPTION:
                        setVisible(false);
                        dispose();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        break;
                    case JOptionPane.CLOSED_OPTION:
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown JOptionPane result: " + res);
                }
            }
            else
            {
                setVisible(false);
                dispose();
            }
        }
    }
        
    /** {@inheritDoc} */
    public void update(Observable o, Object arg)
    {
        if (o instanceof TBXEditor)
        {
            if (arg == TBXEditor.TITLE_CHANGE)
            {
                setTitle((String) editor.getProperty(TBXEditor.TITLE_PROPERTY));
            }
        }
    }
    
    /** {@inheritDoc} */
    protected Element createElement(String tag, Element parent)
    {
        Element elem = editor.getTBXDocument().createElement(tag);
        if (tag.equals("fileDesc"))
        {
            elem.appendChild(createElement("sourceDesc", elem));
        }
        else if (tag.equals("titleStmt"))
        {
            elem.appendChild(createElement("title", elem));
        }
        else if (tag.equals("publicationStmt"))
        {
            elem.appendChild(createElement("p", elem));
        }
        else if (tag.equals("sourceDesc"))
        {
            elem.appendChild(createElement("p", elem));
        }
        else if (tag.equals("encodingDesc"))
        {
            elem.appendChild(createElement("p", elem));
        }
        else if (tag.equals("ude"))
        {
            elem.appendChild(createElement("map", elem));
        }
        else if (tag.equals("revisionDesc"))
        {
            elem.appendChild(createElement("change", elem));
        }
        else if (tag.equals("change"))
        {
            elem.appendChild(createElement("p", elem));
        }
        else if (tag.equals("refObjectList"))
        {
            elem.appendChild(createElement("refObject", elem));
        }
        else if (tag.equals("refObject"))
        {
            elem.appendChild(createElement("item", elem));
        }
        return elem;
    }
    
    /** {@inheritDoc} */
    protected Node findInsertionPoint(Element parent, Element elem)
    {
        Node ret = null;
        TreeViewMenuBar.Tag ptag = findTag(parent);
        TreeViewMenuBar.TagGroup tg = ptag.getChildGroup(elem.getTagName());
        String[] tags = tg.getChildNames();
        if (tags != null)
            ret = findFirstInSet(parent, java.util.Arrays.asList(tags));
        return ret;
    }
    
    /**
     * @param elem The element to search for.
     * @return The tag that was found.
     */
    private TreeViewMenuBar.Tag findTag(Element elem)
    {
        TreeViewMenuBar.Tag ret = nameToTag.get(elem.getTagName());
        if (ret == null)
        {
            List<String> namelist = new java.util.ArrayList<String>();
            Node n = elem;
            while (n instanceof Element)
            {
                Element e = (Element) n;
                namelist.add(0, e.getTagName());
                n = n.getParentNode();
            }
            ret = mbar.MARTIF;
            Iterator<String> names = namelist.iterator();
            names.next();
            while (names.hasNext())
            {
                String name = names.next();
                ret = ret.getChildGroup(name).getChild(name);
            }
            nameToTag.put(elem.getTagName(), ret);
        }
        return ret;
    }
    
    /**
     * @param parent The parent element in which to search for the tags.
     * @param tags The tags to search for.
     * @return The first element found in parent.
     */
    private Element findFirstInSet(Element parent, Collection tags)
    {
        Element ret = null;
        Node node = parent.getFirstChild();
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element elem = (Element) node;
                if (tags.contains(elem.getTagName()))
                {
                    ret = elem;
                    break;
                }
            }
            node = node.getNextSibling();
        }
        return ret;
    }

    /**
     * Position the frame in a resonable location on the screen.
     */
    private void positionFrame()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
    }

    /**
     */
    static
    {
        bundle = ResourceBundle.getBundle(
                "org.ttt.salt.editor.tbxedit.TBXWindow");
    }
}

