/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcClientAuthUnix.java,v 1.1.1.1 2003/08/13 12:03:40 haraldalbrecht Exp $
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

import java.io.IOException;

/**
 * The <code>OncRpcClientAuthUnix</code> class handles protocol issues of
 * ONC/RPC <code>AUTH_UNIX</code> (and thus <code>AUTH_SHORT</code>)
 * authentication.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcClientAuthUnix extends OncRpcClientAuth {

    /**
     * Constructs a new <code>OncRpcClientAuthUnix</code> authentication
     * protocol handling object capable of handling <code>AUTH_UNIX</code>
     * and <code>AUTH_SHORT</code>.
     *
     * <p>Please note that the credential information is typically only
     * unique within a particular domain of machines, user IDs and
     * group IDs.
     *
     * @param machinename Name of the caller's machine (like
     *   "ebankruptcy-dot-com", just for instance...).
     * @param uid Caller's effective user ID.
     * @param gid Caller's effective group ID.
     * @param gids Array of group IDs the caller is a member of.
     */
    public OncRpcClientAuthUnix(String machinename,
                                int uid, int gid, int [] gids) {
        this.stamp = (int)(System.currentTimeMillis() / 1000);
        this.machinename = machinename;
        this.uid = uid;
        this.gid = gid;
        this.gids = gids;
    }

    /**
     * Constructs a new <code>OncRpcClientAuthUnix</code> authentication
     * protocol handling object capable of handling <code>AUTH_UNIX</code>
     * and <code>AUTH_SHORT</code>.
     *
     * <p>Please note that the credential information is typically only
     * unique within a particular domain of machines, user IDs and
     * group IDs.
     *
     * @param machinename Name of the caller's machine (like
     *   "ebankruptcy-dot-com", just for instance...).
     * @param uid Caller's effective user ID.
     * @param gid Caller's effective group ID.
     */
    public OncRpcClientAuthUnix(String machinename, int uid, int gid) {
        this(machinename, uid, gid, NO_GIDS);
    }

    /**
     * Encodes ONC/RPC authentication information in form of a credential
     * and a verifier when sending an ONC/RPC call message. The
     * <code>AUTH_UNIX</code> authentication method only uses the credential
     * but no verifier. If the ONC/RPC server sent a <code>AUTH_SHORT</code>
     * "shorthand" credential together with the previous reply message, it
     * is used instead of the original credential.
     *
     * @param xdr XDR stream where to encode the credential and the verifier
     *   to.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    protected void xdrEncodeCredVerf(XdrEncodingStream xdr)
              throws OncRpcException, IOException {
        if ( shorthandCred == null ) {
            //
            // Encode the credential, which contains some unsecure information
            // about user and group ID, etc. Note that the credential itself
            // is encoded as a variable-sized bunch of octets.
            //
            if ( (gids.length > OncRpcAuthConstants.ONCRPC_MAX_GROUPS)
                 || (machinename.length() >
                        OncRpcAuthConstants.ONCRPC_MAX_MACHINE_NAME) ) {
                throw(new OncRpcAuthenticationException(
                              OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
            }
            xdr.xdrEncodeInt(OncRpcAuthType.ONCRPC_AUTH_UNIX);
            int len =   4 // length of stamp
                      + ((machinename.length() + 7) & ~3) // len string incl. len
                      + 4 // length of uid
                      + 4 // length of gid
                      + gids.length * 4 + 4 // length of vector of gids incl. len
                      ;
            if ( len > OncRpcAuthConstants.ONCRPC_MAX_AUTH_BYTES ) {
                throw(new OncRpcAuthenticationException(
                              OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
            }
            xdr.xdrEncodeInt(len);
            xdr.xdrEncodeInt(stamp);
            xdr.xdrEncodeString(machinename);
            xdr.xdrEncodeInt(uid);
            xdr.xdrEncodeInt(gid);
            xdr.xdrEncodeIntVector(gids);
        } else {
            //
            // Use shorthand credential instead of original credential.
            //
            xdr.xdrEncodeInt(OncRpcAuthType.ONCRPC_AUTH_SHORT);
            xdr.xdrEncodeDynamicOpaque(shorthandCred);
        }
        //
        // We also need to encode the verifier, which is always of
        // type AUTH_NONE.
        //
        xdr.xdrEncodeInt(OncRpcAuthType.ONCRPC_AUTH_NONE);
        xdr.xdrEncodeInt(0);
    }

    /**
     * Decodes ONC/RPC authentication information in form of a verifier
     * when receiving an ONC/RPC reply message. 
     *
     * @param xdr XDR stream from which to receive the verifier sent together
     *   with an ONC/RPC reply message.
     *
     * @throws OncRpcAuthenticationException if the received verifier is
     *   not kosher.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    protected void xdrDecodeVerf(XdrDecodingStream xdr)
              throws OncRpcException, IOException {
        //
        // The verifier sent in response to AUTH_UNIX or AUTH_SHORT credentials
        // can only be AUTH_NONE or AUTH_SHORT. In the latter case we drop
        // any old shorthand credential and use the new one.
        //
        switch ( xdr.xdrDecodeInt() ) {
        case OncRpcAuthType.ONCRPC_AUTH_NONE:
            //
            // Make sure that the verifier does not contain any opaque data.
            // Anything different from this is not kosher and an authentication
            // exception will be thrown.
            //
            if ( xdr.xdrDecodeInt() != 0 ) {
                throw(new OncRpcAuthenticationException(
                              OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
            }
            break;
        case OncRpcAuthType.ONCRPC_AUTH_SHORT:
            //
            // Fetch the credential from the XDR stream and make sure that
            // it does conform to the length restriction as set forth in
            // the ONC/RPC protocol.
            //
            shorthandCred = xdr.xdrDecodeDynamicOpaque();
            if ( shorthandCred.length >
                   OncRpcAuthConstants.ONCRPC_MAX_AUTH_BYTES ) {
                throw(new OncRpcAuthenticationException(
                              OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
            }
            break;
        default:
            //
            // Do not accept any other kind of verifier sent.
            //
            throw(new OncRpcAuthenticationException(
                          OncRpcAuthStatus.ONCRPC_AUTH_INVALIDRESP));
        }
    }

    /**
     * Indicates whether the ONC/RPC authentication credential can be
     * refreshed.
     *
     * @return true, if the credential can be refreshed
     */
    protected boolean canRefreshCred() {
        //
        // If we don't use a shorthand credential at this time, then there's
        // no hope to refresh the credentials.
        //
        if ( shorthandCred == null ) {
            return false;
        }
        //
        // Otherwise just dump the shorthand credentials and let the caller
        // retry. This will probably result in the ONC/RPC server replying
        // with a new shorthand credential.
        //
        shorthandCred = null;
        //
        // Ah, yes. We need to update the "stamp" (more a timestamp, but
        // Sun coding style is sometimes interesting). As is my style too.
        //
        stamp = (int)(System.currentTimeMillis() / 1000);
        //
        // Oh, yes. We can refresh the credential. Maybe.
        //
        return true;
    }

    /**
     * Sets the timestamp information in the credential.
     *
     * @param stamp New timestamp
     */
    public void setStamp(int stamp) {
        this.stamp = stamp;
    }

    /**
     * Returns the timestamp information from the credential.
     *
     * @return timestamp from credential.
     */
    public int getStamp() {
        return stamp;
    }

    /**
     * Sets the machine name information in the credential.
     *
     * @param machinename Machine name.
     */
    public void setMachinename(String machinename) {
        this.machinename = machinename;
    }

    /**
     * Returns the machine name information from the credential.
     *
     * @return machine name.
     */
    public String getMachinename() {
        return machinename;
    }

    /**
     * Sets the user ID in the credential.
     *
     * @param uid User ID.
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Returns the user ID from the credential.
     *
     * @return user ID.
     */
    public int getUid() {
        return uid;
    }

    /**
     * Sets the group ID in the credential.
     *
     * @param gid Group ID.
     */
    public void setGid(int gid) {
        this.gid = gid;
    }

    /**
     * Returns the group ID from the credential.
     *
     * @return group ID.
     */
    public int getGid() {
        return gid;
    }

    /**
     * Sets the group IDs in the credential.
     *
     * @param gids Array of group IDs.
     */
    public void setGids(int [] gids) {
        this.gids = gids;
    }

    /**
     * Returns the group IDs from the credential.
     *
     * @return array of group IDs.
     */
    public int [] getGids() {
        return gids;
    }

    /**
     * Contains timestamp as supplied through credential.
     */
    private int stamp;

    /**
     * Contains the machine name of caller supplied through credential.
     */
    private String machinename;

    /**
     * Contains the user ID of caller supplied through credential.
     */
    private int uid;

    /**
     * Contains the group ID of caller supplied through credential.
     */
    private int gid;

    /**
     * Contains a set of group IDs the caller belongs to, as supplied
     * through credential.
     */
    private int [] gids;

    /**
     * Holds the "shorthand" credentials of type <code>AUTH_SHORT</code>
     * optionally returned by an ONC/RPC server to be used on subsequent
     * ONC/RPC calls.
     */
    private byte [] shorthandCred;

    /**
     * Contains an empty array of group IDs.
     */
    public final static int [] NO_GIDS = new int [0];

}

// End of OncRpcClientAuthUnix.java