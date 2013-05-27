/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.snmp;

import org.eclipse.emf.ecore.EObject;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.HiveCreator;

public class HiveCreatorImpl implements HiveCreator
{

    @Override
    public Hive createHive ( final String reference, final Object configuration ) throws Exception
    {
        if ( reference.equals ( org.openscada.da.server.snmp.Hive.class.getName () ) )
        {
            return null;
        }

        if ( configuration instanceof EObject )
        {
            return new org.openscada.da.server.snmp.Hive ( (EObject)configuration );
        }
        else if ( configuration instanceof String )
        {
            return new org.openscada.da.server.snmp.Hive ( (String)configuration );
        }
        else
        {
            return null;
        }
    }

}
