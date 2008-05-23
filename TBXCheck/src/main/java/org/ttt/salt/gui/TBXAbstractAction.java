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
package org.ttt.salt.gui;

import java.awt.Event;
import java.awt.Toolkit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.Icon;
import javax.swing.AbstractAction;

/**
 * This is the base class for all actions in the TBXValidator GUI.
 * <p>
 * For the GUI to use this it needs to set the resource bundle to build all
 * actions from, and then use getAction for an appropriate action object.</p>
 * <p>
 * This is designed to reduce the startup costs when opening a new window---
 * the actions will be built for the first window, but after that the actions
 * will be reused.</p>
 *
 * @author Lance Finn Helsten
 * @version $Id$
 * @license Licensed under the Apache License, Version 2.0.
 */
public abstract class TBXAbstractAction extends AbstractAction
{
    /*
     */

    /** SCM information. */
    public static final String RCSID = "$Id$";

    /** Common logger for all actions. */
    protected static final Logger LOGGER = Logger.getLogger("org.ttt.salt.gui");
    
    /** Resource bundle used for all actions. */
    private static ResourceBundle bundle;
    
    /** Map of all action ID to the action. */
    private static final Map<Class, TBXAbstractAction> ACTIONS
            = new java.util.HashMap<Class, TBXAbstractAction>();
 
    /**
     * Maps an observable sub-class type to a collection of actions
     * to update.
     */
    private static final Map<Class, Collection<TBXAbstractAction>> OBSERVABLE_TO_ACTIONS
            = new java.util.HashMap<Class, Collection<TBXAbstractAction>>();
    
    /**
     * Set the resource bundle to be used in building all actions.
     *
     * @param bndl The {@link java.util.ResourceBundle} that contains all the
     *  resources to build all the actions that are sub-classes of
     *  TBXAbstractAction.
     */
    public static void setResourceBundle(ResourceBundle bndl)
    {
        bundle = bndl;
    }
    
    /**
     * @param clazz The {@link java.lang.Class} of the action to build. This must
     *  be a sub-class of TBXAbstractAction.
     * @return The {@link javax.swing.Action} that is created. This may be cast
     *  to a TBXAbstractAction.
     */
    public static Action getAction(Class clazz)
    {
        if (!ACTIONS.containsKey(clazz))
        {
            try
            {
                Class[] ptype = {};
                Object[] param = {};
                Constructor init = clazz.getConstructor(ptype);
                TBXAbstractAction action = (TBXAbstractAction) init.newInstance(param);
                ACTIONS.put(action.getClass(), action);
                Class[] obs = action.getObservableClasses();
                for (int i = 0; i < obs.length; i++)
                {
                    if (!OBSERVABLE_TO_ACTIONS.containsKey(obs[i]))
                        OBSERVABLE_TO_ACTIONS.put(obs[i], new java.util.ArrayList<TBXAbstractAction>());
                    Collection<TBXAbstractAction> c = OBSERVABLE_TO_ACTIONS.get(obs[i]);
                    c.add(action);
                }
            }
            catch (NoSuchMethodException err)
            {
                LOGGER.log(Level.SEVERE, "", err);
            }
            catch (IllegalAccessException err)
            {
                LOGGER.log(Level.SEVERE, "", err);
            }
            catch (InstantiationException err)
            {
                LOGGER.log(Level.SEVERE, "", err);
            }
            catch (InvocationTargetException err)
            {
                LOGGER.log(Level.SEVERE, "", err);
            }
        }
        return ACTIONS.get(clazz);
    }
    
    /**
     * @param id The root ID that identifies resources for this action in the
     *  resource bundle.
     */
    protected TBXAbstractAction(String id)
    {
        putValue(ACTION_COMMAND_KEY, id);
        putValue(NAME, bundle.getString(id));
        putValue(SHORT_DESCRIPTION, bundle.getString(id + "ShortDescription"));
        putValue(LONG_DESCRIPTION, bundle.getString(id + "LongDescription"));

        //Build the accelerator key stroke
        String accel = bundle.getString(id + "AcceleratorKey");
        if ((accel != null) && (accel.length() > 0))
        {
            KeyStroke ret = KeyStroke.getKeyStroke(accel);
            int keycode = ret.getKeyCode();
            int mod = ret.getModifiers();
            if ((mod & Event.CTRL_MASK) == Event.CTRL_MASK)
            { //Make sure the menu accelerator modifier is correct
                int pmod = mod;
                pmod ^= ~Event.CTRL_MASK;
                pmod &= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
                if (pmod != mod)
                    ret = KeyStroke.getKeyStroke(keycode, pmod, true);
            }
            putValue(ACCELERATOR_KEY, ret);
        }
        
        //Build the mnemonic
        if (!System.getProperty("os.name").equals("Mac OS X"))
        {
            try
            {
                String mnemonic = bundle.getString(id + "Mnemonic");
                if ((mnemonic != null) && (mnemonic.length() > 0))
                {
                    Integer ret = new Integer(mnemonic.charAt(0));
                    putValue(MNEMONIC_KEY, ret);
                }
            }
            catch (MissingResourceException err)
            {   //no op
                LOGGER.log(Level.FINE, "Mnemonic for {0} not found.", id);
            }
        }

        //Build the small icon
        String smicon = bundle.getString(id + "SmallIcon");
        if ((smicon != null) && (smicon.length() > 0))
        {
            java.net.URL url = getClass().getClassLoader().getResource(smicon);
            if (url != null)
            {
                Icon ret = new javax.swing.ImageIcon(url);
                putValue(SMALL_ICON, ret);
            }
        }
    }
    
    /**
     * Return the resource bundle used by all actions.
     *
     * @return ResoruceBundle set in this instance.
     */
    protected ResourceBundle getResourceBundle()
    {
        return bundle;
    }

    /**
     * Observable classes that will effect this action.
     *
     * @return Array of classes that this Action wants to watch for changes.
     */
    protected Class[] getObservableClasses()
    {
        return new Class[0];
    }

    /**
     * Let the action decide if it wants to listen to the specific listener
     * type of messages on the given object.
     * <p>
     * The sub-class will need to cast the component to the correct type
     * if Component does not handle registration of the specific listener
     * type.</p>
     * <p>
     * The default behavior is to ignore this call.</p>
     *
     * @param listenertype The class of the listener interface the object
     *  is allowing the Action to listen to
     * @param obj The object the action will listen to
     */
    protected void linkToListenable(Class listenertype, Object obj)
    {
        //no-op
    }

    /**
     * Let the action unlink from the given listenable object.
     * <p>
     * The default behavior is to ignore this call.</p>
     *
     * @param listenertype The class of the listener interface the object
     *  is allowing the Action to listen to
     * @param obj The object the action will listen to
     */
    protected void unlinkFromListenable(Class listenertype, Object obj)
    {
        //no-op
    }

    /**
     * This will allow all actions in the system to link to an arbitrary
     * object if they need to for recieving listenable events to which
     * they will change state.
     *
     * @param listenertype The class of the listener interface the object
     *  is allowing the Action to listen to
     * @param obj The object the action will listen to
     */
    public static void linkToListenerObjects(Class listenertype, Object obj)
    {
        Collection<TBXAbstractAction> c = OBSERVABLE_TO_ACTIONS.get(listenertype);
        if (c != null)
        {
            Iterator<TBXAbstractAction> iter = c.iterator();
            while (iter.hasNext())
            {
                TBXAbstractAction a = iter.next();
                a.linkToListenable(listenertype, obj);
            }
        }
    }

    /**
     * This will unlink all the actions in the system from the given object.
     *
     * @param listenertype The class of the listener interface the object
     *  is allowing the Action to listen to
     * @param obj The object the action will stop listening to
     */
    public static void unlinkFromListenerObjects(Class listenertype, Object obj)
    {
        Collection<TBXAbstractAction> c = OBSERVABLE_TO_ACTIONS.get(listenertype);
        if (c != null)
        {
            Iterator<TBXAbstractAction> iter = c.iterator();
            while (iter.hasNext())
            {
                TBXAbstractAction a = iter.next();
                a.unlinkFromListenable(listenertype, obj);
            }
        }
    }
}
