/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcProgramNotRegisteredException.java,v 1.2 2005/11/11 21:03:30 haraldalbrecht Exp $
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
 * The class <code>OncRpcProgramNotRegisteredException</code> indicates
 * that the requests ONC/RPC program is not available at the specified host.
 *
 * @version $Revision: 1.2 $ $Date: 2005/11/11 21:03:30 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcProgramNotRegisteredException extends OncRpcException {

    /**
	 * Defines the serial version UID for <code>OncRpcProgramNotRegisteredException</code>.
	 */
	private static final long serialVersionUID = 5073156463000368270L;

	/**
     * Constructs an ONC/RPC program not registered exception with a detail
     * code of <code>OncRpcException.RPC_PROGNOTREGISTERED</code> and an
     * appropriate clear-text detail message.
     */
    public OncRpcProgramNotRegisteredException() {
        super(OncRpcException.RPC_PROGNOTREGISTERED);
    }

}

// End of OncRpcProgramNotRegisteredException.java

