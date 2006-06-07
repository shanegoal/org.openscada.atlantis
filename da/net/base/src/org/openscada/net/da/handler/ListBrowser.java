package org.openscada.net.da.handler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.ValueTools;

public class ListBrowser
{
    private static Logger _log = Logger.getLogger ( ListBrowser.class );
    
    public static Message createRequest ( String [] path )
    {
        Message message = new Message ( Messages.CC_BROWSER_LIST_REQ );
     
        ListValue value = new ListValue ();
        for ( String tok : path )
        {
            value.add ( new StringValue ( tok ) );
        }
        message.getValues ().put ( "path", value );
        
        return message;
    }
    
    public static String [] parseRequest ( Message message )
    {
        List<String> list = new ArrayList<String> ();
        List<Value> listValue = null;
        
        if ( message.getValues ().containsKey ( "path" ) )
            if ( message.getValues ().get ( "path" ) instanceof ListValue )
                listValue = ((ListValue)message.getValues ().get ( "path" )).getValues ();

        if ( listValue == null )
            return new String[0];
        
        for ( Value value : listValue )
        {
            list.add ( value.toString () );
        }
        
        return list.toArray ( new String[0] );
    }
    
    public static Message createResponse ( Message requestMessage, Entry [] entries )
    {
        Message message = new Message ( Messages.CC_BROWSER_LIST_RES, requestMessage.getSequence () );
        
        ListValue list = new ListValue ();
        for ( Entry entry : entries )
        {
            MapValue mapValue = new MapValue ();
            
            mapValue.put ( "name", new StringValue ( entry.getName () ) );
            mapValue.put ( "attributes", Messages.attributesToMap ( entry.getAttributes () ) );
            
            if ( entry instanceof FolderEntry )
            {
                mapValue.put ( "type", new StringValue ( "folder" ) );
            }
            else if ( entry instanceof DataItemEntry )
            {
                mapValue.put ( "type", new StringValue ( "item" ) );
                DataItemEntry dataItemEntry = (DataItemEntry)entry;
                mapValue.put ( "item-id", new StringValue ( dataItemEntry.getId () ) );
                mapValue.put ( "io-direction", new IntegerValue ( Messages.encodeIO ( dataItemEntry.getIODirections () )) );
            }
            else
            {
                mapValue.put ( "type", new StringValue ( "unknown" ) );
            }
            
            list.add ( mapValue );
        }
        
        message.getValues ().put ( "entries", list );
        
        return message;
    }
    
    public static Entry [] parseResponse ( Message message )
    {
        if ( !message.getValues ().containsKey ( "entries" ) )
        {
            _log.warn ( "Required value 'entries' missing" );
            return new Entry[0];
        }
        
        if ( !(message.getValues ().get ( "entries" ) instanceof ListValue) )
        {
            _log.warn ( "'entries' must be of type 'list'" );
            return new Entry[0];
        }

        ListValue entries = (ListValue)message.getValues ().get ( "entries" );
        List<Entry> list = new LinkedList<Entry> ();
        
        for ( Value value : entries.getValues () )
        {
            if ( !(value instanceof MapValue) )
            {
                _log.warn ( "list value is not of type 'map'. Skipping!" );
                continue;
            }
            MapValue mapValue = (MapValue)value;
            
            Entry entry = null;
            if ( !mapValue.containsKey ( "type" ) )
            {
                _log.warn ( "map misses required value 'type'" );
                continue;
            }
            if ( !mapValue.containsKey ( "name" ) )
            {
                _log.warn ( "map misses required value 'name'" );
                continue;
            }
            if ( !mapValue.containsKey ( "attributes" ) )
            {
                _log.warn ( "map misses required value 'attributes'" );
                continue;
            }
            if ( !(mapValue.get ( "attributes" ) instanceof MapValue) )
            {
                _log.warn ( "map entry 'attributes' is not of type MapValue" );
                continue;
            }
            
            String type = mapValue.get ( "type" ).toString ();
            Map<String, Variant> attributes = Messages.mapToAttributes ( (MapValue)mapValue.get ( "attributes" ) );
            
            _log.debug ( "entry type: '" + type + "'" );
            
            if ( type.equals ( "folder" ) )
            {
                entry = new FolderEntryCommon ( mapValue.get ( "name" ).toString (), attributes );
            }
            else if ( type.equals ( "item" ) )
            {
                if ( !mapValue.containsKey ( "item-id" ) )
                    continue;
                if ( !mapValue.containsKey ( "io-direction" ) )
                    continue;
                
                String id = mapValue.get ( "item-id" ).toString ();
                
                EnumSet<IODirection> io = Messages.decodeIO ( ValueTools.toInteger ( mapValue.get ( "io-direction" ), 0 ) );
                
                entry = new DataItemEntryCommon ( mapValue.get ( "name" ).toString (), io, attributes, id );
            }
            
            // now add the entry
            if ( entry != null )
                list.add ( entry );
        }
        
        return list.toArray ( new Entry[0] );
    }
}
