package com.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class InetAddressTest {
	static Logger logger = Logger.getLogger(InetAddressTest.class);

	public static void main(String[] args) throws IOException {
		InetAddress address = InetAddress.getLocalHost();
		logger.debug(address);
		logger.debug("HostName  : " + address.getHostName());
		logger.debug(address.isLinkLocalAddress());
		logger.debug(address.isMCGlobal());
		logger.debug(address.isMCNodeLocal());
		logger.debug(address.isMCLinkLocal());
		logger.debug(address.isMCOrgLocal());
		logger.debug(address.isMCSiteLocal());
		logger.debug(address.isMulticastAddress());
		address = InetAddress.getByName("bizruntime.com");
		logger.debug(address);
		InetAddress sw[] = InetAddress.getAllByName("www.google.com");
		for (int i = 0; i < sw.length; i++) {
			logger.debug(sw[i]);
		}
		int timeOutinMillis = 10000;
		System.out.println("isReachable(): " + address.isReachable(timeOutinMillis));
		logger.debug("isLoopbackAddress(): " + address.isLoopbackAddress());
		logger.debug(address.getCanonicalHostName());

	}

}
