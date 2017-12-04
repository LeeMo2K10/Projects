package com.biz.Authenticator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.apache.log4j.Logger;

public class AuthenticatorTest {
	static Logger log = Logger.getLogger(AuthenticatorTest.class);

	public static void main(String[] argv) throws Exception {
		Authenticator.setDefault(new MyAuthenticator());
		URL url = new URL("https://www.google.com/");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String str;
		while ((str = in.readLine()) != null) {
			log.debug(str);
		}
		in.close();
	}
}

class MyAuthenticator extends Authenticator {
	static Logger log = Logger.getLogger(MyAuthenticator.class);

	protected PasswordAuthentication getPasswordAuthentication() {
		String promptString = getRequestingPrompt();
		log.debug(promptString);
		String hostname = getRequestingHost();
		log.debug(hostname);
		InetAddress ipaddr = getRequestingSite();
		log.debug(ipaddr);
		int port = getRequestingPort();
		log.debug(port);

		String username = "chinna.rao@bizruntime.com";
		String password = "chinnarao";
		return new PasswordAuthentication(username, password.toCharArray());
	}
}
