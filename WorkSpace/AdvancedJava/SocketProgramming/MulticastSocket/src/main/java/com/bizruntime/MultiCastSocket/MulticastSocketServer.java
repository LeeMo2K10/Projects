package com.bizruntime.MultiCastSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class MulticastSocketServer {
	static Logger log = Logger.getLogger(MulticastSocketServer.class);
	
	final static String INET_ADDR = "228.5.6.7";
	final static int PORT = 8080;

	public static void main(String[] args) throws UnknownHostException,
			InterruptedException {
		InetAddress addr = InetAddress.getByName(INET_ADDR);
		try (DatagramSocket serverSocket = new DatagramSocket()) {
			for (int i = 0; i < 10; i++) {
				String msg = "Sent message no " + i;
				DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
						msg.getBytes().length, addr, PORT);
				serverSocket.send(msgPacket);
				log.debug("Server sent packet with msg: " + msg);
				Thread.sleep(5000);
			}
		} catch (IOException ex) {
			log.error("IOException", ex);
		}
	}
}