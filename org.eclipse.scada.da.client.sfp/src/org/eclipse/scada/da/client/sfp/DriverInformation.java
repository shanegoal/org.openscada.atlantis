/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.da.client.sfp;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.core.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverInformation implements org.eclipse.scada.core.client.DriverInformation
{

    private final static Logger logger = LoggerFactory.getLogger ( DriverInformation.class );

    @Override
    public Connection create ( final ConnectionInformation connectionInformation )
    {
        if ( connectionInformation.getSecondaryTarget () == null )
        {
            return null;
        }

        try
        {
            return new org.eclipse.scada.da.client.sfp.ConnectionImpl ( connectionInformation );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create connection", e );
            return null;
        }
    }

    @Override
    public Class<?> getConnectionClass ()
    {
        return org.eclipse.scada.da.client.sfp.ConnectionImpl.class;
    }

    @Override
    public void validate ( final ConnectionInformation connectionInformation ) throws Throwable
    {
    }

}