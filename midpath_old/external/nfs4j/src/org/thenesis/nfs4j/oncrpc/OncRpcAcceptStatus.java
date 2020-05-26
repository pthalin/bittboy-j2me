/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcAcceptStatus.java,v 1.1.1.1 2003/08/13 12:03:39 haraldalbrecht Exp $
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
 * A collection of constants used to identify the acceptance status of
 * ONC/RPC reply messages.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:39 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcAcceptStatus {

    /**
     * The remote procedure was called and executed successfully.
     */
    public static final int ONCRPC_SUCCESS = 0;

    /**
     * The program requested is not available. So the remote host
     * does not export this particular program and the ONC/RPC server
     * which you tried to send a RPC call message doesn't know of this
     * program either.
     */
    public static final int ONCRPC_PROG_UNAVAIL = 1;

    /**
     * A program version number mismatch occured. The remote ONC/RPC
     * server does not support this particular version of the program.
     */
    public static final int ONCRPC_PROG_MISMATCH = 2;

    /**
     * The procedure requested is not available. The remote ONC/RPC server
     * does not support this particular procedure.
     */
    public static final int ONCRPC_PROC_UNAVAIL = 3;

    /**
     * The server could not decode the arguments sent within the ONC/RPC
     * call message.
     */
    public static final int ONCRPC_GARBAGE_ARGS = 4;

    /**
     * The server encountered a system error and thus was not able to
     * process the procedure call. Causes might be memory shortage,
     * desinterest and sloth.
     */
    public static final int ONCRPC_SYSTEM_ERR = 5;

}

// End of OncRpcAcceptStatus.java
