/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcAuthenticationException.java,v 1.2 2005/11/11 21:01:44 haraldalbrecht Exp $
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
 * The class <code>OncRpcAuthenticationException</code> indicates an
 * authentication exception.
 *
 * @version $Revision: 1.2 $ $Date: 2005/11/11 21:01:44 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcAuthenticationException extends OncRpcException {

    /**
	 * Defines the serial version UID for <code>OncRpcAuthenticationException</code>.
	 */
	private static final long serialVersionUID = 7747394107888423440L;

	/**
     * Initializes an <code>OncRpcAuthenticationException</code>
     * with a detail of {@link OncRpcException#RPC_AUTHERROR} and
     * the specified {@link OncRpcAuthStatus authentication status} detail.
     *
     * @param authStatus The authentication status, which can be any one of
     *   the {@link OncRpcAuthStatus OncRpcAuthStatus constants}.
     */
    public OncRpcAuthenticationException(int authStatus) {
        super(RPC_AUTHERROR);

        authStatusDetail = authStatus;
    }

    /**
     * Returns the authentication status detail of this ONC/RPC exception
     * object.
     *
     * @return  The authentication status of this <code>OncRpcException</code>.
     */
    public int getAuthStatus() {
        return authStatusDetail;
    }

    /**
     * Specific authentication status detail (reason why this authentication
     * exception was thrown).
     *
     * @serial
     */
    private int authStatusDetail;

}

// End of OncRpcAuthenticationException.java

