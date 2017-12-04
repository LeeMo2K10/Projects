<%@ page session="false"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<HTML>
<HEAD>
<TITLE>Department Managers</TITLE>
</HEAD>
<BODY>
	<p>
	<hr color="#000000">
	<H2>Department Managers</H2>

	<%
		String DRIVER = "com.mysql.jdbc.Driver";
		String URL = "jdbc:mysql://localhost/bizruntime";
		// Open a database connection
		Class.forName(DRIVER);
		Connection con = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			con = DriverManager.getConnection(URL, "root", "root");
			// Get department manager information
			String sql1 = "SELECT deptno,deptname,deptmgr from departments";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql1);
	%>

	<%
		while (rs.next()) {
				String deptno = rs.getString(1);
				String deptname = rs.getString(2);
				String deptmgr = rs.getString(3);

				{
	%>
	<table>
	<tbody>		<tr>
		<td><%=deptno%></td>
		</tr>
		<tr>
			<td><%=deptname%></td>
		</tr>
		<tr>
			<td><%=deptmgr%></td>
		</tr>
		</tbody>
		
	</table>

	<%
		}

			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
	%>

</BODY>
</HTML>
