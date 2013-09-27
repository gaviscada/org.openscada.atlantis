/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.server.modbus.io;

public class ModbusProtocolError extends RuntimeException
{
    private static final long serialVersionUID = 943209047791339327L;

    public ModbusProtocolError ()
    {
        super ();
    }

    public ModbusProtocolError ( final String message, final Throwable cause )
    {
        super ( message, cause );
    }

    public ModbusProtocolError ( final String message )
    {
        super ( message );
    }

    public ModbusProtocolError ( final Throwable cause )
    {
        super ( cause );
    }
}