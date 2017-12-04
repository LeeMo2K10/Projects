package com.socket;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

public class InterfaceAddressTest {
	static Logger logger = Logger.getLogger(InterfaceAddressTest.class);

	public static void main(String[] args) throws UnknownHostException, SocketException {

		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

		while (en.hasMoreElements()) {

			NetworkInterface ni = en.nextElement();
			List<InterfaceAddress> inAdd = ni.getInterfaceAddresses();
			for (InterfaceAddress ia : inAdd) {

				System.out.println(ia.getAddress());

				System.out.println(ia.getBroadcast());

				System.out.println(ia.getNetworkPrefixLength());
			}

		}
	}

}
