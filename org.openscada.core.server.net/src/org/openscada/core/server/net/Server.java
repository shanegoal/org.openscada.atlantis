/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.core.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.net.ConnectionHelper;
import org.openscada.core.server.common.NetworkHelper;
import org.openscada.net.mina.SocketImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server
{

    private final static Logger logger = LoggerFactory.getLogger ( Server.class );

    private IoAcceptor acceptor;

    private final ConnectionInformation connectionInformation;

    public Server ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    public Set<ConnectionInformation> start ( final SingleSessionIoHandlerDelegate ioHandler ) throws IOException
    {
        this.acceptor = createAcceptor ( ioHandler );
        return NetworkHelper.getLocalInterfaces ( this.acceptor, this.connectionInformation );
    }

    private IoAcceptor createAcceptor ( final IoHandler ioHandler ) throws IOException
    {
        IoAcceptor acceptor = null;

        final SocketImpl socketImpl = SocketImpl.fromName ( this.connectionInformation.getProperties ().get ( "socketImpl" ) );

        // create the acceptor
        acceptor = socketImpl.createAcceptor ();

        // set up the filter chain
        ConnectionHelper.setupFilterChain ( this.connectionInformation, acceptor.getFilterChain (), false );

        // set the session handler
        acceptor.setHandler ( ioHandler );

        // check if the primary target is the wildcard target
        String host = this.connectionInformation.getTarget ();
        if ( host != null && ( host.length () == 0 || "*".equals ( host ) ) )
        {
            host = null;
        }

        // bind
        if ( host != null )
        {
            acceptor.bind ( socketImpl.doLookup ( host, this.connectionInformation.getSecondaryTarget () ) );
        }
        else
        {
            acceptor.bind ( new InetSocketAddress ( this.connectionInformation.getSecondaryTarget () ) );
        }

        return acceptor;
    }

    public void dispose ()
    {
        if ( this.acceptor != null )
        {
            this.acceptor.dispose ();
            this.acceptor = null;
        }
    }
}
