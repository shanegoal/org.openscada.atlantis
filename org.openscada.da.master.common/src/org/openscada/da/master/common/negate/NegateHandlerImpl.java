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

package org.openscada.da.master.common.negate;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.da.master.common.internal.Activator;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class NegateHandlerImpl extends AbstractCommonHandlerImpl
{
    private boolean active;

    private final String attrValueOriginal;

    private final String attrActive;

    public NegateHandlerImpl ( final String configurationId, final ObjectPoolTracker<MasterItem> poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, NegateHandlerFactoryImpl.FACTORY_ID, NegateHandlerFactoryImpl.FACTORY_ID );

        this.attrActive = getPrefixed ( "active", Activator.getStringInterner () );
        this.attrValueOriginal = getPrefixed ( "value.original", Activator.getStringInterner () );
    }

    @Override
    protected void processDataUpdate ( final Map<String, Object> context, final DataItemValue.Builder builder ) throws Exception
    {
        injectAttributes ( builder );

        if ( this.active )
        {
            builder.setAttribute ( this.attrValueOriginal, builder.getValue () );
        }

        final Variant val = builder.getValue ();
        if ( val == null || val.isNull () )
        {
            return;
        }

        builder.setValue ( handleDataUpdate ( builder.getValue () ) );
    }

    private Variant handleDataUpdate ( final Variant value )
    {
        if ( !this.active )
        {
            return value;
        }
        else
        {
            return Variant.valueOf ( !value.asBoolean () );
        }
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.active = cfg.getBoolean ( "active", false ); //$NON-NLS-1$

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( this.attrActive, this.active ? Variant.TRUE : Variant.FALSE );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" ); //$NON-NLS-1$

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () ); //$NON-NLS-1$
        }

        return updateConfiguration ( data, attributes, false, operationParameters );
    }

}
