/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.sysinfo.items;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.sysinfo.Hive;
import org.openscada.da.server.sysinfo.utils.FileUtils;
import org.openscada.utils.collection.MapBuilder;

public class LoadAverageJob implements Runnable
{

    private final File _file = new File ( "/proc/loadavg" );

    private final DataItemInputCommon _avg1 = new DataItemInputCommon ( "loadavg1" );

    private final DataItemInputCommon _avg5 = new DataItemInputCommon ( "loadavg5" );

    private final DataItemInputCommon _avg15 = new DataItemInputCommon ( "loadavg15" );

    private Hive _hive = null;

    private FolderCommon _folder = null;

    public LoadAverageJob ( final Hive hive, final FolderCommon folder )
    {
        this._hive = hive;
        this._folder = folder;

        this._hive.registerItem ( this._avg1 );
        this._folder.add ( "1", this._avg1, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "The 1 minute load avarage" ) ).getMap () );

        this._hive.registerItem ( this._avg5 );
        this._folder.add ( "5", this._avg5, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "The 5 minute load avarage" ) ).getMap () );

        this._hive.registerItem ( this._avg15 );
        this._folder.add ( "15", this._avg15, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "The 15 minute load avarage" ) ).getMap () );
    }

    public void run ()
    {
        try
        {
            read ();
        }
        catch ( final Exception e )
        {
            // handle error
        }
    }

    private void read () throws IOException
    {
        final String[] data = FileUtils.readFile ( this._file );

        final StringTokenizer tok = new StringTokenizer ( data[0] );

        this._avg1.updateData ( new Variant ( Double.parseDouble ( tok.nextToken () ) ), null, null );
        this._avg5.updateData ( new Variant ( Double.parseDouble ( tok.nextToken () ) ), null, null );
        this._avg15.updateData ( new Variant ( Double.parseDouble ( tok.nextToken () ) ), null, null );
    }
}