package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.Client;
import org.openscada.net.io.IOProcessor;

public class ClientConnection extends ConnectionHandlerBase
{
	private static Logger _log = Logger.getLogger(ClientConnection.class);
	
	private Client _client = null;
    private IOProcessor _processor = null;
    private SocketAddress _remote = null;
    
	public ClientConnection ( IOProcessor processor, SocketAddress remote )
	{
        _processor = processor;
	    _remote = remote;   
	}
    
    /**
     * start connecting to the server
     *
     */
    public void start ()
    {
        _client = new Client ( _processor, getMessageProcessor(), this, _remote );
        setConnection ( _client.getConnection() );
        _client.connect ();
    }
	
	@Override
	public void opened ()
	{
	    setConnection ( _client.getConnection() );
	    super.opened ();
	}
}
