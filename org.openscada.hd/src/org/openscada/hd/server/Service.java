package org.openscada.hd.server;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;

public interface Service extends org.openscada.core.server.Service
{
    public Query createQuery ( Session session, String itemId, QueryParameters parameters, QueryListener listener );
}