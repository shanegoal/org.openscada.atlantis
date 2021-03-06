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

package org.openscada.da.connection.provider.internal;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.da.client.Connection;
import org.openscada.da.connection.provider.ConnectionServiceImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager extends AbstractConnectionManager
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionManager.class );

    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        super ( context, connectionInformation, connectionId, autoReconnectDelay, initialOpen );
    }

    @Override
    protected AbstractConnectionService createConnection ()
    {
        logger.debug ( "Creating new DA connection: {}", getConnectionInformation () );

        final Connection connection = (Connection)getFactory ().getDriverInformation ( getConnectionInformation () ).create ( getConnectionInformation () );

        if ( connection == null )
        {
            return null;
        }

        return new ConnectionServiceImpl ( connection, getAutoReconnectDelay (), false );
    }
}
