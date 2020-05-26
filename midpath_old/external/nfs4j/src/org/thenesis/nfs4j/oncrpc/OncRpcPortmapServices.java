/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcPortmapServices.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
 *
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 *
 * Copyright (c) 1999, 2000
 * Lehrstuhl fuer Prozessleittechnik (PLT), RWTH Aachen
 * D-52064 Aachen, Germany.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.thenesis.nfs4j.oncrpc;

/**
 * A collection of constants used for ONC/RPC messages to identify the
 * remote procedure calls offered by ONC/RPC portmappers.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcPortmapServices {

    /**
     * Procedure number of portmap service to register an ONC/RPC server.
     */
    public static final int PMAP_SET = 1;
    /**
     * Procedure number of portmap service to unregister an ONC/RPC server.
     */
    public static final int PMAP_UNSET = 2;
    /**
     * Procedure number of portmap service to retrieve port number of
     * a particular ONC/RPC server.
     */
    public static final int PMAP_GETPORT = 3;
    /**
     * Procedure number of portmap service to return information about all
     * currently registered ONC/RPC servers.
     */
    public static final int PMAP_DUMP = 4;
    /**
     * Procedure number of portmap service to indirectly call a remote
     * procedure an ONC/RPC server through the ONC/RPC portmapper.
     */
    public static final int PMAP_CALLIT = 5;

}

// End of OncRpcPortmapServices.java

