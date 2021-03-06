/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.monitor.datasource;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.data.Severity;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.DemoteImpl;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public abstract class AbstractDemotingMasterItemMonitor extends AbstractMasterItemMonitor
{

    private final DemoteImpl demoteProcessor = new DemoteImpl ();

    public AbstractDemotingMasterItemMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String factoryId, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, factoryId, prefix, defaultMonitorType );
    }

    @Override
    protected void handleDataUpdate ( final Map<String, Object> context, final DataItemValue.Builder value )
    {
        this.demoteProcessor.handleDataUpdate ( context, value );

        super.handleDataUpdate ( context, value );
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        this.demoteProcessor.update ( userInformation, properties );
    }

    @Override
    protected void setFailure ( final Variant value, final Long valueTimestamp, final Severity severity, final boolean requireAck )
    {
        final Severity result = this.demoteProcessor.demoteSeverity ( severity );
        if ( result == null )
        {
            setOk ( value, valueTimestamp );
        }
        else
        {
            super.setFailure ( value, valueTimestamp, result, this.demoteProcessor.demoteAck ( requireAck ) );
        }
    }

}
