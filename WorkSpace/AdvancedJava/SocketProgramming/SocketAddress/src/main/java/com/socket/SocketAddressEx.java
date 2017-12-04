package com.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

public class SocketAddressEx {
	static Logger logger = Logger.getLogger(SocketAddressEx.class);

	public static void main(String[] argv) throws Exception {
		new SocketAddressEx();
	}

	public SocketAddressEx() {
		String testServerName = "localhost";
		int port = 8080;
		try {

			Socket socket = openSocket(testServerName, port);

			String result = writeToAndReadFromSocket(socket, "GET /\n\n");

			logger.debug(result);

			socket.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private String writeToAndReadFromSocket(Socket socket, String writeTo) throws Exception {
		try {

			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(writeTo);
			bufferedWriter.flush();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str + "\n");
			}

			bufferedReader.close();
			return sb.toString();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	private Socket openSocket(String server, int port) throws Exception {
		Socket socket;

		try {
			InetAddress inteAddress = InetAddress.getByName(server);
			SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

			socket = new Socket();

			int timeoutInMs = 10 * 1000;
			socket.connect(socketAddress, timeoutInMs);

			return socket;
		} catch (SocketTimeoutException ste) {

			logger.error(ste.getMessage() + "Timed out waiting for the socket.");
			throw ste;
		}
	}
}
