<%@ page session="false"%>
<%@ page import="java.util.*"%>
<%
	String thisURL = HttpUtils.getRequestURL(request).toString();
	thisURL = java.net.URLEncoder.encode(thisURL);
	Object[][] locales = { { new Locale("en", "US"), "English" }, { new Locale("de", "DE"), "Deutsch" },
			{ new Locale("es", "ES"), "Espa�ol" }, { new Locale("fr", "FR"), "Fran�ais" },
			{ new Locale("it", "IT"), "Italiano" } };

	for (int i = 0; i < locales.length; i++) {
		Locale locale = (Locale) locales[i][0];
		String name = (String) locales[i][1];
		StringBuffer sb = new StringBuffer();
		if (i > 0)
			sb.append(" | ");
		sb.append("<A HREF=\"setPreferences.jsp?cameFrom=");
		sb.append(thisURL);
		sb.append("&language=");
		sb.append(locale.getLanguage());
		sb.append("&country=");
		sb.append(locale.getCountry());
		sb.append("\"");
		sb.append(">");
		sb.append(name);
		sb.append("</A>");
		out.println(sb);
	}
%>