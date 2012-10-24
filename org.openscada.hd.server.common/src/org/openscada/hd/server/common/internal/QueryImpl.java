/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.common.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query, QueryListener
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private Query query;

    private final SessionImpl session;

    private final QueryListener listener;

    private QueryParameters queryParameters;

    private HashSet<String> valueTypes;

    public QueryImpl ( final SessionImpl session, final QueryListener listener )
    {
        this.session = session;
        this.listener = listener;
        session.addQuery ( this );
    }

    public void setQuery ( final Query query )
    {
        if ( this.query == null )
        {
            this.query = query;
        }
    }

    @Override
    public void close ()
    {
        dispose ();
    }

    public void dispose ()
    {
        this.session.removeQuery ( this );
        if ( this.query != null )
        {
            this.query.close ();
        }
    }

    @Override
    public void changeParameters ( final QueryParameters parameters )
    {
        logger.debug ( "changeParameters: parameters: {}", parameters );
        this.query.changeParameters ( parameters );
    }

    @Override
    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        logger.debug ( "updateParameters: parameters: {}, valueTypes: {}", new Object[] { parameters, valueTypes } );
        if ( parameters == null )
        {
            throw new IllegalArgumentException ( "'parameters' must not be null" );
        }
        if ( valueTypes == null || valueTypes.isEmpty () )
        {
            throw new IllegalArgumentException ( "'valueTypes' must not be null or empty" );
        }

        synchronized ( this )
        {
            this.queryParameters = parameters;
            this.valueTypes = new HashSet<String> ( valueTypes );
        }
        this.listener.updateParameters ( parameters, valueTypes );
    }

    @Override
    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        if ( values == null )
        {
            throw new IllegalArgumentException ( "'values' must not be null" );
        }
        if ( valueInformation == null )
        {
            throw new IllegalArgumentException ( "'valueInformation' must not be null" );
        }

        logger.debug ( "updateData: index: {}, values: @{} ({}), valueInformation: @{}", new Object[] { index, values.size (), values.keySet (), valueInformation.length } );

        synchronized ( this )
        {
            if ( this.queryParameters == null )
            {
                throw new IllegalStateException ( "'updateData' must be called after a call to 'updateParameters'" );
            }
            if ( !this.valueTypes.equals ( values.keySet () ) )
            {
                throw new IllegalArgumentException ( "'updateData' must receive the same data series as the 'updateParameters' call" );
            }
        }
        if ( index < 0 || index >= this.queryParameters.getEntries () )
        {
            throw new IllegalArgumentException ( "'index' must be greater or equal to zero and lower than the number of reported entries" );
        }
        for ( final Map.Entry<String, Value[]> entry : values.entrySet () )
        {
            if ( entry.getValue () == null )
            {
                throw new IllegalArgumentException ( String.format ( "The values for '%s' are null", entry.getKey () ) );
            }
            if ( entry.getValue ().length != valueInformation.length )
            {
                throw new IllegalArgumentException ( String.format ( "The number of entries for '%s' is not equal to the rest of the entry count", entry.getKey () ) );
            }
        }
        if ( index + valueInformation.length > this.queryParameters.getEntries () )
        {
            throw new IllegalArgumentException ( "The reported data exceeds reported number of entries" );
        }

        // finally we can pass on the event
        this.listener.updateData ( index, values, valueInformation );
    }

    @Override
    public void updateState ( final QueryState state )
    {
        logger.debug ( "updateState: state: {}", new Object[] { state } );

        if ( state == null )
        {
            throw new IllegalArgumentException ( "'state' must not be null" );
        }

        try
        {
            this.listener.updateState ( state );
        }
        finally
        {
            switch ( state )
            {
                case DISCONNECTED:
                    this.session.removeQuery ( this );
                    break;
                default:
                    break;
            }
        }
    }

}
