/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.job.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.opc2.browser.BrowseRequest;
import org.openscada.da.server.opc2.browser.BrowseResult;
import org.openscada.da.server.opc2.browser.BrowseResultEntry;
import org.openscada.da.server.opc2.connection.OPCModel;
import org.openscada.da.server.opc2.job.JobResult;
import org.openscada.da.server.opc2.job.ThreadJob;
import org.openscada.opc.dcom.da.OPCBROWSEDIRECTION;
import org.openscada.opc.dcom.da.OPCBROWSETYPE;
import org.openscada.opc.dcom.da.impl.OPCBrowseServerAddressSpace;
import org.openscada.opc.lib.da.browser.Access;

public class BrowseJob extends ThreadJob implements JobResult<BrowseResult>
{

    private final OPCModel model;

    private BrowseResult result;

    private final int batchSize = 100;

    private final BrowseRequest request;

    public BrowseJob ( final long timeout, final OPCModel model, final BrowseRequest request )
    {
        super ( timeout );
        this.model = model;
        this.request = request;
    }

    @Override
    protected void perform () throws Exception
    {
        final OPCBrowseServerAddressSpace browser = this.model.getServer ().getBrowser ();
        if ( browser == null )
        {
            return;
        }

        final BrowseResult result = new BrowseResult ();
        final int accessMask = Access.READ.getCode () | Access.WRITE.getCode ();

        // move in position
        browser.changePosition ( null, OPCBROWSEDIRECTION.OPC_BROWSE_TO );
        for ( final String path : this.request.getPath () )
        {
            browser.changePosition ( path, OPCBROWSEDIRECTION.OPC_BROWSE_DOWN );
        }

        // get the branches 
        result.setBranches ( browser.browse ( OPCBROWSETYPE.OPC_BRANCH, "", accessMask, JIVariant.VT_EMPTY ).asCollection ( this.batchSize ) );

        // get the leaves
        final Collection<String> readLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.READ.getCode (), JIVariant.VT_EMPTY ).asCollection ( this.batchSize );
        final Collection<String> writeLeaves = browser.browse ( OPCBROWSETYPE.OPC_LEAF, "", Access.WRITE.getCode (), JIVariant.VT_EMPTY ).asCollection ( this.batchSize );
        processLeaves ( result, browser, readLeaves, writeLeaves );

        // result
        this.result = result;
    }

    private void processLeaves ( final BrowseResult result, final OPCBrowseServerAddressSpace browser, final Collection<String> readLeaves, final Collection<String> writeLeaves ) throws JIException
    {
        final Map<String, BrowseResultEntry> leavesResult = new HashMap<String, BrowseResultEntry> ();

        // add read leaves
        for ( final String leaf : readLeaves )
        {
            final BrowseResultEntry entry = new BrowseResultEntry ();
            entry.setEntryName ( leaf );
            final String itemId = browser.getItemID ( leaf );
            entry.setItemId ( itemId );
            entry.setIoDirections ( EnumSet.of ( IODirection.INPUT ) );

            leavesResult.put ( leaf, entry );
        }

        // add write leaves
        for ( final String leaf : writeLeaves )
        {
            BrowseResultEntry entry = leavesResult.get ( leaf );
            if ( entry != null )
            {
                entry.getIoDirections ().add ( IODirection.OUTPUT );
            }
            else
            {
                entry = new BrowseResultEntry ();
                entry.setEntryName ( leaf );
                final String itemId = browser.getItemID ( leaf );
                entry.setItemId ( itemId );
                entry.setIoDirections ( EnumSet.of ( IODirection.OUTPUT ) );

                leavesResult.put ( leaf, entry );
            }
        }

        result.setLeaves ( leavesResult.values () );
    }

    public BrowseResult getResult ()
    {
        return this.result;
    }

}
