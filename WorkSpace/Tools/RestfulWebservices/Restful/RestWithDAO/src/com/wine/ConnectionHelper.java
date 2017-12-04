package com.wine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ConnectionHelper {
	public static Connection getConnection() {

		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost/chinna";

		// Database credentials
		final String USER = "root";
		final String PASS = "root";

		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE STUDENTS";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
		return conn;
	}// end main
}// end JDBCExample

/*
 * private String url; private static ConnectionHelper instance;
 * 
 * private ConnectionHelper() { String driver = null; try {
 * Class.forName("com.mysql.jdbc.Driver"); url =
 * "jdbc:mysql://localhost/directory?user=root"; ResourceBundle bundle =
 * ResourceBundle.getBundle("chinna"); driver = bundle.getString("jdbc.driver");
 * Class.forName(driver); url=bundle.getString("jdbc.url"); } catch (Exception
 * e) { e.printStackTrace(); } }
 * 
 * public static Connection getConnection() throws SQLException { if (instance
 * == null) { instance = new ConnectionHelper(); } try { return
 * DriverManager.getConnection(instance.url); } catch (SQLException e) { throw
 * e; } }
 * 
 * public static void close(Connection connection) { try { if (connection !=
 * null) { connection.close(); } } catch (SQLException e) { e.printStackTrace();
 * } }
 * 
 * }
 */