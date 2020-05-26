/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcAuthType.java,v 1.1.1.1 2003/08/13 12:03:40 haraldalbrecht Exp $
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
 * A collection of constants used to identify the authentication schemes
 * available for ONC/RPC. Please note that currently only
 * <code>ONCRPC_AUTH_NONE</code> is supported by this Java package.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcAuthType {

    /**
     * No authentication scheme used for this remote procedure call.
     */
    public static final int ONCRPC_AUTH_NONE = 0;
    /**
     * The so-called "Unix" authentication scheme is not supported. This one
     * only sends the users id as well as her/his group identifiers, so this
     * is simply far too weak to use in typical situations where
     * authentication is requested.
     */
    public static final int ONCRPC_AUTH_UNIX = 1;
    /**
     * The so-called "short hand Unix style" is not supported.
     */
    public static final int ONCRPC_AUTH_SHORT = 2;
    /**
     * The DES authentication scheme (using encrypted time stamps) is not
     * supported -- and besides, it's not a silver bullet either.
     */
    public static final int ONCRPC_AUTH_DES = 3;

}

// End of OncRpcAuthType.java
