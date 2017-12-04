package com.bizruntime.MultiCastSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class MulticastSocketClient {
	static Logger log = Logger.getLogger(MulticastSocketClient.class);
	
	final static String INET_ADDR = "228.5.6.7";
	final static int PORT = 8080;

	public static void main(String[] args) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(INET_ADDR);
		byte[] buf = new byte[256];
		log.debug("somethng............");
		try (MulticastSocket clientSocket = new MulticastSocket(8080)) {
			clientSocket.joinGroup(address);
			while (true) {
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(msgPacket);
				String msg = new String(buf, 0, buf.length);
				log.debug("Socket 1 received msg: " + msg);
			}
		} catch (IOException ex) {
			log.error("IOException......", ex);
		}
	}
}