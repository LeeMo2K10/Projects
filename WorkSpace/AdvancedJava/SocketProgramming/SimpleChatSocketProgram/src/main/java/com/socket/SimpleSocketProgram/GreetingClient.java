package com.socket.SimpleSocketProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class GreetingClient {
	static Logger logger = Logger.getLogger(GreetingClient.class);
	static BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));

	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner sc = new Scanner(System.in);
		logger.info("Enter Server name : ");
		String serverName = sc.nextLine();
		logger.info("Enter Port number : ");

		int port = sc.nextInt();
		Socket sock = new Socket(serverName, port);

		BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));

		OutputStream ostream = sock.getOutputStream();
		PrintWriter pwrite = new PrintWriter(ostream, true);

		InputStream istream = sock.getInputStream();
		BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

		logger.info("Start the chitchat, type and press Enter key");

		String receiveMessage, sendMessage;
		while (true) {
			sendMessage = keyRead.readLine();
			pwrite.println(sendMessage);
			pwrite.flush();
			if ((receiveMessage = receiveRead.readLine()) != null) {
				logger.info(receiveMessage);
			}
		}
	}
}
