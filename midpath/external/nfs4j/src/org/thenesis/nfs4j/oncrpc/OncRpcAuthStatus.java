/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcAuthStatus.java,v 1.1.1.1 2003/08/13 12:03:40 haraldalbrecht Exp $
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
 * A collection of constants used to identify the authentication status
 * (or any authentication errors) in ONC/RPC replies of the corresponding
 * ONC/RPC calls.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcAuthStatus {

    /**
     * There is no authentication problem or error.
     */
    public static final int ONCRPC_AUTH_OK = 0;

    /**
     * The ONC/RPC server detected a bad credential (that is, the seal was
     * broken).
     */
    public static final int ONCRPC_AUTH_BADCRED = 1;

    /**
     * The ONC/RPC server has rejected the credential and forces the caller
     * to begin a new session.
     */
    public static final int ONCRPC_AUTH_REJECTEDCRED = 2;

    /**
     * The ONC/RPC server detected a bad verifier (that is, the seal was
     * broken).
     */
    public static final int ONCRPC_AUTH_BADVERF = 3;

    /**
     * The ONC/RPC server detected an expired verifier (which can also happen
     * if the verifier was replayed).
     */
    public static final int ONCRPC_AUTH_REJECTEDVERF = 4;

    /**
     * The ONC/RPC server rejected the authentication for security reasons.
     */
    public static final int ONCRPC_AUTH_TOOWEAK = 5;

    /**
     * The ONC/RPC client detected a bogus response verifier.
     */
    public static final int ONCRPC_AUTH_INVALIDRESP = 6;

    /**
     * Authentication at the ONC/RPC client failed for an unknown reason.
     */
    public static final int ONCRPC_AUTH_FAILED = 7;
}

// End of OncRpcAuthStatus.java