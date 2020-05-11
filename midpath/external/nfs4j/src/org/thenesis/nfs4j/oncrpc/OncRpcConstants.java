/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcConstants.java,v 1.3 2005/11/11 21:02:47 haraldalbrecht Exp $
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
 * A collection of constants generally usefull for ONC/RPC.
 *
 * @version $Revision: 1.3 $ $Date: 2005/11/11 21:02:47 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcConstants {

    /**
     * The current version of the Remote Tea Java library as a string.
     */
    public static final String REMOTETEA_VERSION_STRING = "1.0.4";

    /**
     * The current major version number of the Remote Tea Java library.
     */
    public static final int REMOTETEA_VERSION_MAJOR = 1;

    /**
     * The current minor version number of the Remote Tea Java library.
     */
    public static final int REMOTETEA_VERSION_MINOR = 0;

    /**
     * The current patch level of the Remote Tea Java library.
     */
    public static final int REMOTETEA_VERSION_PATCHLEVEL = 4;

    /**
     * The current preversion version number. If not zero, then this
     * indicates a preversion (no, not perversion... ooops, sorry).
     */
    public static final int REMOTETEA_VERSION_PREVERSION = 0;

}

// End of OncRpcConstants.java