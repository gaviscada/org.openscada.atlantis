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

package org.openscada.da.base.browser;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.openscada.da.core.browser.Entry;
import org.openscada.rcp.da.client.Activator;

public class RefreshFolderUpdater extends FolderUpdater
{
    private static Logger _log = Logger.getLogger ( RefreshFolderUpdater.class );

    private Job _refreshJob = null;

    public RefreshFolderUpdater ( final HiveConnection connection, final FolderEntry folder, final boolean autoInitialize )
    {
        super ( connection, folder, autoInitialize );
    }

    synchronized public void refresh ()
    {
        if ( this._refreshJob != null )
        {
            return;
        }

        this._refreshJob = new Job ( Messages.getString ( "RefreshFolderUpdater.RefreshJob.name" ) ) { //$NON-NLS-1$

            @Override
            protected IStatus run ( final IProgressMonitor monitor )
            {
                try
                {
                    performRefresh ( monitor );
                    return new OperationStatus ( OperationStatus.OK, Activator.PLUGIN_ID, 0, "", null ); //$NON-NLS-1$
                }
                catch ( final Exception e )
                {
                    return new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.getString ( "RefreshFolderUpdater.ErrorCondition.1" ), e ); //$NON-NLS-1$
                }
                finally
                {
                    monitor.done ();
                    RefreshFolderUpdater.this._refreshJob = null;
                }
            }
        };

        this._refreshJob.schedule ();
    }

    private void performRefresh ( final IProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask ( Messages.getString ( "RefreshFolderUpdater.RefreshTask.message" ), 1 ); //$NON-NLS-1$

        final Entry[] entries = getConnection ().getConnection ().browse ( getFolder ().getLocation () );

        final Map<String, BrowserEntry> list = convert ( Arrays.asList ( entries ) );

        update ( list );

        for ( final Map.Entry<String, BrowserEntry> entry : this._entries.entrySet () )
        {
            _log.debug ( "Entry: " + entry.getKey () ); //$NON-NLS-1$
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
