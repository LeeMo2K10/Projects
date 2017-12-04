package com.client;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class PriceQuoteReader {
	public static void main(String[] args)
	{
		String supplier =
				"http://www.lyricnote.com/client.jsp";
				String product = "Clarinet";
				int quantity = 3;
				// Append the search arguments to the URL so that
				// they will be recognized as parameters to an
				// HTTP GET request
				StringBuffer sb = new StringBuffer();
				sb.append(supplier);
				sb.append("?product=");
				sb.append(URLEncoder.encode(product));
				sb.append("&quantity=");
				sb.append(URLEncoder.encode(String.valueOf(quantity)));
				String supplierURL = sb.toString();
				try {
				// Now create the URL instance
				URL url = new URL(supplierURL);
				// and open its input stream
				InputStream stream = url.openStream();
				BufferedReader in =
						new BufferedReader(
						new InputStreamReader(stream));
						while (true) {
						String line = in.readLine();
						if (line == null)
						break;
						System.out.println(line);
						}
						in.close();
						}
						catch (IOException e) {
						e.printStackTrace();
						}
						}
}