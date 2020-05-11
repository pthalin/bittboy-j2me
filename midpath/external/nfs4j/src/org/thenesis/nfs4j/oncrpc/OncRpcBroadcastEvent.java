/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcBroadcastEvent.java,v 1.3 2005/11/11 21:19:20 haraldalbrecht Exp $
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

import java.util.EventObject;
import java.net.InetAddress;

/**
 * The class <code>OncRpcBroadcastEvent</code> defines an event fired by
 * {@link OncRpcUdpClient ONC/RPC UDP/IP-based clients} whenever replies
 * to a
 * {@link OncRpcUdpClient#broadcastCall(int, XdrAble, XdrAble, OncRpcBroadcastListener) broadcast call}
 * are received.
 *
 * @see OncRpcBroadcastListener
 * @see OncRpcBroadcastAdapter
 * @see OncRpcUdpClient
 *
 * @version $Revision: 1.3 $ $Date: 2005/11/11 21:19:20 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcBroadcastEvent extends EventObject {

    /**
	 * Defines the serial version UID for <code>OncRpcBroadcastEvent</code>.
	 */
	private static final long serialVersionUID = 1604512454490873965L;

	/**
     * Creates a new <code>KscPackageUpdateEvent</code> object and
     * initializes its state.
     *
     * @param source The {@link OncRpcUdpClient ONC/RPC client object} which has
     *   fired this event.
     * @param replyAddress Internetaddress of reply's origin.
     * @param procedureNumber Procedure number of ONC/RPC call.
     * @param params The ONC/RPC call resulting in this reply.
     * @param reply The ONC/RPC reply itself.
     */
    public OncRpcBroadcastEvent(OncRpcUdpClient source, InetAddress replyAddress,
                                int procedureNumber,
                                XdrAble params, XdrAble reply) {
        super(source);
        this.replyAddress = replyAddress;
        this.procedureNumber = procedureNumber;
        this.params = params;
        this.reply = reply;
    }

    /**
     * Returns the address of the sender of the ONC/RPC reply message.
     *
     * @return address of sender of reply.
     */
    public InetAddress getReplyAddress() {
        return replyAddress;
    }

    /**
     * Returns ONC/RPC reply message.
     *
     * @return reply message object.
     */
    public XdrAble getReply() {
        return reply;
    }

    /**
     * Returns the number of the remote procedure called.
     * 
     * @return procedure number.
     */
    public int getProcedureNumber() {
    	return procedureNumber;
    }
    
    /**
     * Returns the parameter message sent in a broadcast RPC.
     * 
     * @return parameter message object.
     */
    public XdrAble getParams() {
    	return params;
    }
    
    /**
     * Contains the address of the sender of the ONC/RPC reply message.
     *
     * @serial
     */
    private InetAddress replyAddress;

    /**
     * Contains the number of the remote procedure called.
     *
     * @serial
     */
    private int procedureNumber;

    /**
     * Contains the parameters sent in the ONC/RPC broadcast call.
     *
     * @serial
     */
    private XdrAble params;

    /**
     * Contains the reply from a remote ONC/RPC server, which answered
     * the broadcast call.
     *
     * @serial
     */
    private XdrAble reply;

}

// End of OncRpcBroadcastEvent.java
