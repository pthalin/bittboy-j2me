/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcTimeoutException.java,v 1.2 2005/11/11 21:05:00 haraldalbrecht Exp $
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
 * The class <code>OncRpcTimeoutException</code> indicates a timed out
 * call exception.
 *
 * @version $Revision: 1.2 $ $Date: 2005/11/11 21:05:00 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcTimeoutException extends OncRpcException {

    /**
	 * Defines the serial version UID for <code>OncRpcTimeoutException</code>.
	 */
	private static final long serialVersionUID = 2777518173161399732L;

	/**
     * Initializes an <code>OncRpcTimeoutException</code>
     * with a detail of {@link OncRpcException#RPC_TIMEDOUT}.
     */
    public OncRpcTimeoutException() {
        super(RPC_TIMEDOUT);
    }

}

// End of OncRpcTimeoutException.java
