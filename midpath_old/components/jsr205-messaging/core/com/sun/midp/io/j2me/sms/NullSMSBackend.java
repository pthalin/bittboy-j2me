package com.sun.midp.io.j2me.sms;

import java.io.IOException;

import com.sun.midp.io.j2me.sms.Protocol.SMSPacket;

public class NullSMSBackend implements SMSBackend {
	
	private static final int SUCCESS = 0;
	private static final int FAILURE = -1;
	private static final int HANDLE = 1;
	
	private boolean waiting = false;

	public int close(int port, int handle, int deRegister) {
		return SUCCESS;
	}

	public int open(String host, int msid, int port) throws IOException {
		return HANDLE;
	}

	public int receive(int port, int msid, int handle, SMSPacket smsPacket) throws IOException {
		smsPacket.messageType = Protocol.GSM_TEXT;
		smsPacket.address = "+5121234567".getBytes();
		smsPacket.port = 5000;
		String msg = "SMS Test";
		smsPacket.message = TextEncoder.encode(TextEncoder.toByteArray(msg));
		return msg.length();
	}

	public int send(int handle, int type, String host, int destPort, int sourcePort, byte[] message) throws IOException {
		return message.length;
	}

	public int waitUntilMessageAvailable(int port, int handle) throws IOException {
		// Wait a bit to simulate real delays
		waiting = true;
		try {
			long startTime = System.currentTimeMillis();
			while(waiting && ((System.currentTimeMillis() - startTime) < 5000)) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
		}
		return SUCCESS;
	}

	public int unblockReceiveThread() throws IOException {
		waiting = false;
		return HANDLE;
	}

}
