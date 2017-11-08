/*
 * $Id$
 *-----------------------------------------------------------------------------
 * Copyright (C) 1997-2000 Lance Finn Helsten (helsten@acm.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.flyingtitans.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Iterator;

/**
 * Parses UNIX style command line options and parameter lists. Rules:
 *
 * <ol>
 * <li>
 * Single character options start with a single hyphen, '-'.</li>
 * <li>
 * Multi-character options start with a double hyphen, '--'.</li>
 * <li>
 * Single character options that take no parameters can be joined after
 * a single hyphen: e.g. -aefh.</li>
 * <li>
 * Options that take parameters may have an optional equals '=' character
 * between the option and the parameter: e.g. '-a=this' and '-b that' are
 * both valid.</li>
 * <li>
 * A double hyphen, '--', with no option indicates then end of the options,
 * and everything after is considered parameters.</li>
 * <li>
 * Multiple options are allowed.</li>
 * </ol>
 *
 * @author Lance Finn Helsten
 * @version 1.0
 */
public class GetOpt
{
    /*
     */

    /** SCM information. */
    private static final String RCSID = "$Id$";

    /** remaining arguments. */
    private String[] args = {};

    /** list of all the options and parameters. */
    private Map<String, String[]> options = new java.util.HashMap<String, String[]>();

    /**
     * Default constructor.
     */
    public GetOpt()
    {
    }

    /**
     * Constructor that adds a map of defaults to the options.
     *
     * @param defaults Map of defaults which map a string key to an array of
     *  string value.s
     */
    public GetOpt(Map<String, String[]> defaults)
    {
        this();

        Iterator<String> iter = defaults.keySet().iterator();
        while (iter.hasNext())
        {
            String key = iter.next();
            String[] val = defaults.get(key);
            options.put(key, val);
        }
    }

    /**
     * @return The argument list being parsed.
     */
    public final String[] getArgs()
    {
        return (String[]) args.clone();
    }

    /**
     * @param option The key for the option.
     * @return Is the option in the list of current options.
     */
    public final boolean containsOption(String option)
    {
        return options.containsKey(option);
    }

    /**
     * This will return the latest parameter value of the given option,
     * if the option does not exists the default parameter is returned.
     *
     * @param option The key for the option
     * @param defvalue The value to return if the option does not exists
     * @return The value associated with the option key
     */
    public final String getParameter(String option, String defvalue)
    {
        String ret = defvalue;
        String[] param = (String[]) options.get(option);
        if (param != null)
            ret = param[param.length - 1];
        return ret;
    }

    /**
     * This will return the latest parameter value of the given option,
     * if the option does not exists this will return an empty string.
     *
     * @param option The option for to get the current parameter.
     * @return The current parameter value.
     */
    public final String getParameter(String option)
    {
        return getParameter(option, "");
    }

    /**
     * @param option The option for to get a list of parameters.
     * @return The list of asociated parameters.
     */
    public final String[] getParameters(String option)
    {
        return (String[]) options.get(option);
    }

    /**
     * This will take an array of arguments and parse them based on
     * the rules given.
     * <p>
     * The <code>opt</code> argument is a sting that contains each of
     * the single character options possible. If the option can have a
     * parameter then the character should be followed by a colon, ':'.</p>
     * <p>
     * The <code>longopt</code> argument is an array of long option names.
     * If the option can have a parameter then the string should end
     * with an equals, '=', character.</p>
     * <p>
     * <strong>CAUTION:</strong> Do not put any hyphens into either
     * <code>opt</code> or <code>longopt</code>.</p>
     *
     * @param argv The arguments array that is given to main().
     * @param opt The string identifying the single character options.
     * @param lngopt The array of strings for long options.
     */
    public void parseArgs(String[] argv, String opt, String[] lngopt)
    {
        String[] longopt = (String[]) lngopt.clone();
        java.util.Arrays.sort(longopt);
        int nonopt = argv.length;
        for (int i = 0; i < argv.length; i++)
        {
            String arg = argv[i];
            if (arg.equals("--"))
            {
                nonopt = i + 1;
                i = argv.length;
            }
            else if (arg.startsWith("--"))
            {
                i = longOption(longopt, argv, i);
            }
            else if (arg.startsWith("-"))
            {
                i = shortOption(opt, argv, i);
            }
            else
            {
                nonopt = i;
                i = argv.length;
            }
        }

        if (nonopt < argv.length)
        {
            int len = argv.length - nonopt;
            String[] rem = new String[len + args.length];
            System.arraycopy(args, 0, rem, 0, args.length);
            System.arraycopy(argv, nonopt, rem, args.length, len);
            args = rem;
        }
    }

    /**
     * @param opt A string option.
     * @param argv The list of arguments.
     * @param idx The index into the arguments to work.
     * @return The new index in the argument list.
     */
    private int shortOption(String opt, String[] argv, int idx)
    {
        int index = idx;
        String arg = argv[index];
        int eq = arg.indexOf('=');
        if (eq == -1)
        {
            if (arg.length() > 2)
            {
                for (int i = 1; i < arg.length(); i++)
                {
                    String o = "-" + arg.substring(i, i + 1);
                    int oi = opt.indexOf(o.substring(1));
                    if (oi == -1)
                        unknownArgument(o);
                    else if (((oi + 1) < opt.length())
                            && (opt.charAt(oi + 1) == ':'))
                        parameterNeeded(o);
                    else
                        addOption(o, "");
                }
            }
            else
            {
                int oi = opt.indexOf(arg.substring(1));

                if (oi == -1)
                {
                    unknownArgument(arg);
                }
                else if (((oi + 1) < opt.length())
                        && (opt.charAt(oi + 1) == ':'))
                {
                    if ((index + 1) >= argv.length)
                        parameterNeeded(arg);
                    index++;
                    addOption(arg, argv[index]);
                }
                else
                {
                    addOption(arg, "");
                }
            }
        }
        else
        { //single character option with parameter and equals
            int oi = opt.indexOf(arg.substring(1, 2));
            if (oi == -1)
                unknownArgument(arg);
            else if (((oi + 1) == opt.length()) || (opt.charAt(oi + 1) != ':'))
                noParameterAllowed(arg);
            else
                addOption(arg.substring(0, eq), arg.substring(eq + 1));
        }

        return index;
    }

    /**
     * @param opt A list of string options.
     * @param argv The list of arguments.
     * @param idx The index into the arguments to work.
     * @return The new index in the argument list.
     */
    private int longOption(String[] opt, String[] argv, int idx)
    {
        int index = idx;
        String arg = argv[index];
        int eq = arg.indexOf('=');
        if (eq == -1)
        {
            int oi = java.util.Arrays.binarySearch(opt, arg.substring(2));
            if (oi < 0)
            {
                oi = java.util.Arrays.binarySearch(opt, arg.substring(2) + "=");
                if (oi < 0)
                {
                    unknownArgument(arg);
                }
                else
                {
                    if ((index + 1) >= argv.length)
                        parameterNeeded(arg);
                    index++;
                    addOption(arg, argv[index]);
                }
            }
            else
            {
                addOption(arg, "");
            }
        }
        else
        {
            //The following needs to be fully qualified to get the correct
            //Arrays class.
            int oi = java.util.Arrays.binarySearch(opt, arg.substring(2, eq + 1));
            if (oi < 0)
                unknownArgument(arg);
            else if (!opt[oi].endsWith("="))
                noParameterAllowed(arg);
            else
                addOption(arg.substring(0, eq), arg.substring(eq + 1));
        }
        return index;
    }

    /**
     * @param key The option's key.
     * @param val The option's value.
     */
    private void addOption(String key, String val)
    {
        String[] parm = {val};
        if (options.containsKey(key))
        {
            String[] old = (String[]) options.get(key);
            parm = new String[old.length + 1];
            System.arraycopy(old, 0, parm, 0, old.length);
            parm[parm.length - 1] = val;
        }
        options.put(key, parm);
    }

    /**
     * Throw an IllegalArgumentException on a particular argument.
     *
     * @param arg Argument that caused the error.
     */
    private void unknownArgument(String arg)
    {
        String str = "Unknown argument {0}.";
        Object[] parms = {arg};
        String msg = MessageFormat.format(str, parms);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Throw an IllegalArgumentException on a particular argument.
     *
     * @param arg Argument that caused the error.
     */
    private void parameterNeeded(String arg)
    {
        String str = "Option {0} requires a parameter.";
        Object[] parms = {arg};
        String msg = MessageFormat.format(str, parms);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Throw an IllegalArgumentException on a particular argument.
     *
     * @param arg Argument that caused the error.
     */
    private void noParameterAllowed(String arg)
    {
        String str = "Option {0} cannot take a parameter.";
        Object[] parms = {arg};
        String msg = MessageFormat.format(str, parms);
        throw new IllegalArgumentException(msg);
    }

    /**
     * This will print out what the command line would look like.
     *
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuffer ret = new StringBuffer();
        Iterator opts = options.keySet().iterator();
        while (opts.hasNext())
        {
            String opt = (String) opts.next();
            String[] parms = (String[]) options.get(opt);
            for (int i = 0; i < parms.length; i++)
            {
                String parm = parms[i];
                if (parm.equals(""))
                    ret.append(opt).append(" ");
                else
                    ret.append(opt).append("=").append(parm).append(" ");
            }
        }

        for (int i = 0; i < args.length; i++)
            ret.append(args[i]).append(" ");

        return ret.toString();
    }
}
