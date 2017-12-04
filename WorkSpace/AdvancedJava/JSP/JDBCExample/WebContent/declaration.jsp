
<html>
<body>
	<%@ page errorPage="ErrorPage.jsp" import="java.io.*,java.util.*"%>
	<%
		Enumeration enames;
		Map map;
		String title;
		// Print the request headers
		map = new TreeMap();
		enames = request.getHeaderNames();
		while (enames.hasMoreElements()) {
			String name = (String) enames.nextElement();
			String value = request.getHeader(name);
			map.put(name, value);
		}
		printTable(out, map, "Request Headers");
		// Print the session attributes
		map = new TreeMap();
		enames = session.getAttributeNames();
		while (enames.hasMoreElements()) {
			String name = (String) enames.nextElement();
			String value = "" + session.getAttribute(name);
			map.put(name, value);
		}
		printTable(out, map, "Session Attributes");
	%>
	<%-- Define a method to print a table --%>
	<%!private static void printTable(Writer writer, Map map, String title) {
		// Get the output stream
		PrintWriter out = new PrintWriter(writer);
		// Write the header lines
		// Write the table rows
		Iterator imap = map.entrySet().iterator();
		while (imap.hasNext()) {
			Map.Entry entry = (Map.Entry) imap.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			out.println("<TR>");
			out.println("<TD>" + key + "</TD>");
			out.println("<TD>" + value + "</TD>");
			out.println("</TR>");
		}
		// Write the footer lines
		out.println("</TABLE>");
		out.println("<P>");
	}%>
</body>
</html>