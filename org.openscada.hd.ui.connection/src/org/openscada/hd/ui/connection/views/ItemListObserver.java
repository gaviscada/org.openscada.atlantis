package org.openscada.hd.ui.connection.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.connection.provider.ConnectionService;
import org.openscada.hd.ui.connection.internal.ItemWrapper;

public class ItemListObserver extends WritableSet implements ItemListListener
{
    private final ConnectionService service;

    private final ConnectionWrapper connection;

    private final Map<String, ItemWrapper> items = new HashMap<String, ItemWrapper> ();

    public ItemListObserver ( final ConnectionWrapper connection )
    {
        this.connection = connection;
        this.service = connection.getService ();
        synchronized ( this )
        {
            this.service.getConnection ().addListListener ( this );
        }
    }

    @Override
    public synchronized void dispose ()
    {
        this.service.getConnection ().removeListListener ( this );
        super.dispose ();
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        if ( !isDisposed () )
        {
            getRealm ().asyncExec ( new Runnable () {
                public void run ()
                {
                    handleUpdate ( addedOrModified, removed );
                }
            } );
        }
    }

    protected void handleUpdate ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed )
    {
        setStale ( true );
        try
        {
            if ( removed != null )
            {
                for ( final String itemId : removed )
                {
                    final ItemWrapper info = this.items.remove ( itemId );
                    if ( info != null )
                    {
                        remove ( info );
                    }
                }
            }
            if ( addedOrModified != null )
            {
                for ( final HistoricalItemInformation item : addedOrModified )
                {
                    final ItemWrapper wrapper = new ItemWrapper ( this.connection, item );
                    this.items.put ( item.getId (), wrapper );
                    add ( wrapper );
                }
            }
        }
        finally
        {
            setStale ( false );
        }
    }
}
