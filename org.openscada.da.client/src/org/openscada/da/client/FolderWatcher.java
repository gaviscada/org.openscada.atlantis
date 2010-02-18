/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;

public class FolderWatcher extends Observable implements FolderListener
{
    protected Location location = null;

    protected Map<String, Entry> cache = new HashMap<String, Entry> ();

    public FolderWatcher ( final String... path )
    {
        this.location = new Location ( path );
    }

    public FolderWatcher ( final Location location )
    {
        this.location = location;
    }

    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        int changed = 0;

        synchronized ( this )
        {

            if ( full )
            {
                this.cache.clear ();
            }

            for ( final Entry entry : added )
            {
                this.cache.put ( entry.getName (), entry );
                changed++;
            }

            for ( final String name : removed )
            {
                if ( this.cache.remove ( name ) != null )
                {
                    changed++;
                }
            }

            if ( changed > 0 || full )
            {
                setChanged ();

            }
        }

        notifyObservers ();
    }

    public Location getLocation ()
    {
        return this.location;
    }

    public Map<String, Entry> getCache ()
    {
        return this.cache;
    }

    public Collection<Entry> getList ()
    {
        return this.cache.values ();
    }
}