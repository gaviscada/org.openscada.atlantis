/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.net.operations;

import org.openscada.core.Variant;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class WriteOperationController
{
    private LongRunningController _controller = null;

    public WriteOperationController ( final Messenger connection )
    {
        this._controller = new LongRunningController ( connection, Messages.CC_WRITE_OPERATION_RESULT );
    }

    public void register ()
    {
        this._controller.register ();
    }

    public void unregister ()
    {
        this._controller.unregister ();
    }

    public LongRunningOperation start ( final String itemName, final Variant value, final LongRunningListener listener )
    {
        final Message message = org.openscada.net.da.handler.WriteOperation.create ( itemName, value );

        return this._controller.start ( message, listener );
    }

}
