package com.javacodegeeks.snippets.URLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;

import org.apache.log4j.Logger;

public class URLConnectionExample {
	static Logger log = Logger.getLogger(URLConnectionExample.class);
	private static final String FINAL_URL = "http://www.javacodegeeks.com/";

	public static void main(String args[]) throws IOException {
		StringBuilder content = new StringBuilder();

		URL url = new URL(FINAL_URL);

		URLConnection urlConnection = url.openConnection();

		log.info("Content-Type: " + urlConnection.getContentType());

		log.info("Content-Length: " + urlConnection.getContentLength());

		log.info("Date: " + new Date(urlConnection.getDate()));

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			content.append(line + "\n");
		}
		bufferedReader.close();
		log.info("output:\n " + content);
	}
}