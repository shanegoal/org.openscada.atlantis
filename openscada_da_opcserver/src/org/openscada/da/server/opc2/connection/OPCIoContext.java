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

package org.openscada.da.server.opc2.connection;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OPCIoContext
{
    private Map<String, Boolean> activations;

    private List<ItemRequest> registrations;

    private List<OPCWriteRequest> writeRequests;

    private Set<String> readItems;

    public Map<String, Boolean> getActivations ()
    {
        return this.activations;
    }

    public void setActivations ( final Map<String, Boolean> activations )
    {
        this.activations = activations;
    }

    public List<ItemRequest> getRegistrations ()
    {
        return this.registrations;
    }

    public void setRegistrations ( final List<ItemRequest> registrations )
    {
        this.registrations = registrations;
    }

    public List<OPCWriteRequest> getWriteRequests ()
    {
        return this.writeRequests;
    }

    public void setWriteRequests ( final List<OPCWriteRequest> writeRequests )
    {
        this.writeRequests = writeRequests;
    }

    public Set<String> getReadItems ()
    {
        return this.readItems;
    }

    public void setReadItems ( final Set<String> readItems )
    {
        this.readItems = readItems;
    }

}
