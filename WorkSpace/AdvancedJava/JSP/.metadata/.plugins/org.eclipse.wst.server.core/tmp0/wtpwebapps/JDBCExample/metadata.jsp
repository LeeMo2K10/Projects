<%@ page session="false"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.reflect.*"%>
<%
	String driverName = request.getParameter("driverName");
	if (driverName == null)
		driverName = "";
	driverName = driverName.trim();
	if (driverName.equals(""))
		throw new ServletException("No driverName parameter");
	// Get required database URL parameter
	String url = request.getParameter("url");
	if (url == null)
		url = "";
	url = url.trim();
	if (url.equals(""))
		throw new ServletException("No url parameter");
	// Get optional userID parameter
	String userID = request.getParameter("userID");
	if (userID == null)
		userID = "";
	userID = userID.trim();
	// Get optional password parameter
	String password = request.getParameter("password");
	if (password == null)
		password = "";
	password = password.trim();
	// Load the driver
	Class.forName(driverName);
	Connection con = null;
	try {
		// Open the database connection and get the metadata
		con = DriverManager.getConnection(url, userID, password);
		DatabaseMetaData md = con.getMetaData();
		/*
		Use reflection to get a list of methods that the
		metadata class supports. Select only public methods
		that take no parameters and that return either
		a string or a boolean.*/
		Class mdclass = md.getClass();
		Method[] methods = mdclass.getDeclaredMethods();
		Map methodMap = new TreeMap();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// Public methods only
			if (!Modifier.isPublic(method.getModifiers()))
				continue;
			// with no parameters
			if (method.getParameterTypes().length > 0)
				continue;
			// that return String or boolean

			Class returnType = method.getReturnType();
			if ((returnType != java.lang.Boolean.TYPE) && (returnType != java.lang.String.class))
				continue;
			// Add selected methods to sorted map
			methodMap.put(method.getName(), method);
		}
		int row = 0;
		Iterator im = methodMap.keySet().iterator();
		while (im.hasNext()) {
			String methodName = (String) im.next();
			Object methodValue = null;
			Method method = (Method) methodMap.get(methodName);
			// Invoke the method and get the result
%>
<HTML>
<HEAD>
<TITLE>Metadata Explorer</TITLE>
<LINK REL="stylesheet" HREF="style.css">
</HEAD>
<BODY>

	<H3>
		Metadata Explorer for
		<%=md.getDatabaseProductName()%>
		<%=md.getDatabaseProductVersion()%>
		<BR> [<%=driverName%>]
	</H3>
	<TABLE>
		<TR CLASS="header">
			<TH CLASS="header">Method</TH>
			<TH CLASS="header">Value</TH>
		</TR>

		<%
			//Generate the table
					row = 0;
					im = methodMap.keySet().iterator();
					while (im.hasNext()) {
						methodName = (String) im.next();
						methodValue = null;
						method = (Method) methodMap.get(methodName);
						// Invoke the method and get the result
						try {
							Object[] noParameters = new Object[0];
							methodValue = method.invoke(md, noParameters);
						} catch (Exception ignore) {
						}
						// Display the results
						row++;
						String rowClass = "row" + (row % 2);
		%>
		<TR CLASS="<%=rowClass%>">
			<TD><%=methodName%></TD>
			<TD><%=formatLine(methodValue)%></TD>
		</TR>
		<%
			}
				}
			} finally {
				if (con != null)
					con.close();
			}
		%>
	</TABLE>

</BODY>
</HTML>
<%!/**
		* Formats an object in an HTML-friendly way,
		* making sure it doesn't exceed 48 characters
		* in width.
		*/
	private static String formatLine(Object obj) {
		if (obj == null)
			return "";
		StringBuffer out = new StringBuffer();
		StringBuffer line = new StringBuffer();
		StringTokenizer st = new StringTokenizer(obj.toString(), ",;", true);
		while (st.hasMoreTokens()) {
			if (line.length() > 48) {
				out.append(line.toString());
				out.append("<BR>");
				line = new StringBuffer();
			}
			line.append(st.nextToken());
		}
		out.append(line.toString());
		return out.toString();
	}%>
