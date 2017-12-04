package com.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

public class DataSender {

	static Logger logger = Logger.getLogger(DataReciever.class);

	public static void main(String[] args) throws IOException {

		DatagramSocket socket = new DatagramSocket();
		String str = "welcome to bizruntime ";
		InetAddress ia = InetAddress.getByName("localhost");
		DatagramPacket packet = new DatagramPacket(str.getBytes(), str.length(), ia, 3000);
		socket.send(packet);
		logger.debug("packet delivered");
		logger.debug(socket.getBroadcast());
		logger.debug(socket.getLocalPort());
		logger.debug(socket.getLocalAddress());
		logger.debug(socket.getLocalSocketAddress());
		logger.debug(socket.getSendBufferSize());
		logger.debug(socket.isConnected());
		socket.close();
	}
}
