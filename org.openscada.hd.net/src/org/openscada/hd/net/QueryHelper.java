/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.data.ValueInformation;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHelper
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryHelper.class );

    public static Value toValue ( final QueryParameters parameters )
    {
        final MapValue value = new MapValue ();

        if ( parameters == null )
        {
            return null;
        }

        value.put ( "startTimestamp", new LongValue ( parameters.getStartTimestamp () ) );
        value.put ( "endTimestamp", new LongValue ( parameters.getEndTimestamp () ) );
        value.put ( "numberOfEntries", new IntegerValue ( parameters.getNumberOfEntries () ) );

        return value;
    }

    public static QueryParameters fromValue ( final Value value )
    {
        try
        {
            final MapValue map = (MapValue)value;

            final long startTimestamp = ( (LongValue)map.get ( "startTimestamp" ) ).getValue ();
            final long endTimestamp = ( (LongValue)map.get ( "endTimestamp" ) ).getValue ();
            final int numberOfEntries = ( (IntegerValue)map.get ( "numberOfEntries" ) ).getValue ();

            return new QueryParameters ( startTimestamp, endTimestamp, numberOfEntries );
        }
        catch ( final ClassCastException e )
        {
            logger.debug ( "Failed to decode", e );
            return null;
        }
        catch ( final NullPointerException e )
        {
            logger.debug ( "Failed to decode", e );
            return null;
        }
        catch ( final IllegalArgumentException e )
        {
            logger.debug ( "Failed to decode", e );
            return null;
        }
    }

    public static Map<String, List<Double>> fromValueData ( final Value value )
    {
        final Map<String, List<Double>> result = new HashMap<String, List<Double>> ();

        final MapValue map = (MapValue)value;
        for ( final Map.Entry<String, Value> entry : map.getValues ().entrySet () )
        {
            result.put ( entry.getKey (), fromValues ( entry.getValue () ) );
        }

        return result;
    }

    private static List<Double> fromValues ( final Value value )
    {
        final ListValue list = (ListValue)value;

        final List<Double> result = new ArrayList<Double> ( list.size () );

        for ( final Value entry : list.getValues () )
        {
            if ( entry instanceof LongValue )
            {
                result.add ( (double) ( (LongValue)entry ).getValue () );
            }
            else
            {
                result.add ( ( (DoubleValue)entry ).getValue () );
            }
        }

        return result;
    }

    public static Value toValueData ( final Map<String, List<Double>> values )
    {
        final MapValue result = new MapValue ();

        for ( final Map.Entry<String, List<Double>> entry : values.entrySet () )
        {
            result.put ( entry.getKey (), toValues ( entry.getValue () ) );
        }

        return result;
    }

    private static Value toValues ( final List<Double> value )
    {
        final ListValue list = new ListValue ();

        for ( final Double entry : value )
        {
            list.add ( new DoubleValue ( entry ) );
        }

        return list;
    }

    public static List<ValueInformation> fromValueInfo ( final Value value )
    {
        final List<ValueInformation> result = new LinkedList<ValueInformation> ();

        try
        {
            final ListValue listValue = (ListValue)value;
            for ( final Value entry : listValue.getValues () )
            {
                result.add ( fromEntry ( entry ) );
            }
        }
        catch ( final ClassCastException e )
        {
            logger.debug ( "Failed to decode", e );
            return null;
        }
        catch ( final NullPointerException e )
        {
            logger.debug ( "Failed to decode", e );
            return null;
        }

        return result;
    }

    private static ValueInformation fromEntry ( final Value entry )
    {
        final MapValue value = (MapValue)entry;

        final double quality = ( (DoubleValue)value.get ( "quality" ) ).getValue ();
        final double manualPercentage = ( (DoubleValue)value.get ( "manual" ) ).getValue ();
        final long startTimestamp = ( (LongValue)value.get ( "startTimestamp" ) ).getValue ();
        final long endTimestamp = ( (LongValue)value.get ( "endTimestamp" ) ).getValue ();
        final long sourceValues = ( (LongValue)value.get ( "values" ) ).getValue ();
        return new ValueInformation ( quality, manualPercentage, startTimestamp, endTimestamp, sourceValues );
    }

    public static Value toValueInfo ( final List<ValueInformation> infos )
    {
        final ListValue result = new ListValue ();

        for ( final ValueInformation info : infos )
        {
            final MapValue entry = new MapValue ();
            entry.put ( "startTimestamp", new LongValue ( info.getStartTimestamp () ) );
            entry.put ( "endTimestamp", new LongValue ( info.getEndTimestamp () ) );
            entry.put ( "quality", new DoubleValue ( info.getQuality () ) );
            entry.put ( "manual", new DoubleValue ( info.getManualPercentage () ) );
            entry.put ( "values", new LongValue ( info.getSourceValues () ) );
            result.add ( entry );
        }

        return result;
    }

    public static Value toValueTypes ( final Set<String> valueTypes )
    {
        final ListValue result = new ListValue ();

        for ( final String entry : valueTypes )
        {
            result.add ( new StringValue ( entry ) );
        }

        return result;
    }

    public static Set<String> fromValueTypes ( final Value value )
    {
        final Set<String> result = new HashSet<String> ();
        try
        {
            final ListValue list = (ListValue)value;
            for ( final Value valueEntry : list.getValues () )
            {
                result.add ( ( (StringValue)valueEntry ).getValue () );
            }
        }
        catch ( final NullPointerException e )
        {
            logger.debug ( "Failed to decode", e );
        }
        catch ( final ClassCastException e )
        {
            logger.debug ( "Failed to decode", e );
        }
        return result;
    }

}
