package org.openscada.da.server.ice.impl;

import org.apache.log4j.Logger;

import Ice.LocalException;
import OpenSCADA.DA.AMI_DataCallback_valueChange;

public class AsyncValueChange extends AMI_DataCallback_valueChange
{
    private static Logger _log = Logger.getLogger ( AsyncValueChange.class );
    
    private SessionImpl _session = null;
    
    public AsyncValueChange ( SessionImpl session )
    {
        super ();
        _session = session;
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _log.debug ( "Failed to notify", ex );
        _session.handleListenerError ();
    }

    @Override
    public void ice_response ()
    {
        // no-op
    }

}
