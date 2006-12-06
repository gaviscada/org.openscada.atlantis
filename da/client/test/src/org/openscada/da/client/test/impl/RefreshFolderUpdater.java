/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.test.impl;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.core.server.browser.Entry;

public class RefreshFolderUpdater extends FolderUpdater
{
    private static Logger _log = Logger.getLogger ( RefreshFolderUpdater.class );
    private Job _refreshJob = null;

    public RefreshFolderUpdater ( HiveConnection connection, FolderEntry folder, boolean autoInitialize )
    {
        super ( connection, folder, autoInitialize );
    }
    
    synchronized public void refresh ()
    {
        if ( _refreshJob != null )
            return;
        
        _refreshJob = new Job ( "Refresh..." ) {

            @Override
            protected IStatus run ( IProgressMonitor monitor )
            {
                try
                {
                    performRefresh ( monitor );
                    return new OperationStatus ( OperationStatus.OK, Openscada_da_client_testPlugin.PLUGIN_ID, 0, "", null );
                }
                catch ( Exception e )
                {
                    return new OperationStatus ( OperationStatus.ERROR, Openscada_da_client_testPlugin.PLUGIN_ID, 0, "Failed to refresh", e );
                }
                finally
                {
                    monitor.done ();
                    _refreshJob = null;
                }
            }};
            
            _refreshJob.schedule ();
    }

    private void performRefresh ( IProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask ( "Refreshing tree", 1 );

        Entry [] entries = getConnection ().getConnection ().browse ( getFolder ().getLocation ().asArray () );

        Map<String, BrowserEntry> list = convert ( Arrays.asList ( entries ) );

        update ( list );
        
        for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
        {
            _log.debug ( "Entry: " + entry.getKey () );
        }
        
        monitor.worked ( 1 );
    }

    @Override
    public void dispose ()
    {
        clear ();
    }

    @Override
    public void init ()
    {
        refresh ();
    }
}
