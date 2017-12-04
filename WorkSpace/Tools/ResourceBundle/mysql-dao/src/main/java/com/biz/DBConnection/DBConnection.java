package com.biz.DBConnection;

import java.io.IOException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBConnection {
	protected static final Logger LOGGER = Logger.getLogger(DBConnection.class);

	private static final String DATASOURCE_FILE = "db.properties";
	public static final String URL = "url";
	public static final String DRIVER_CLASS = "driver";
	public static final String USER = "user";
	public static final String PASSWORD = "password";

	/**
	 * @return JDBCconnection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException {

		Properties prop = new Properties();

		// getting property file from classpath
		InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(DBConnection.DATASOURCE_FILE);
		try {
			prop.load(input);
		} catch (IOException e) {
			LOGGER.error("property file not found in DBConnection class" + e.getMessage());
		}

		Connection connection = null;

		if (connection == null) {
			Class.forName(prop.getProperty(DBConnection.DRIVER_CLASS));
			connection = (Connection) DriverManager.getConnection(prop.getProperty(DBConnection.URL),
					prop.getProperty(DBConnection.USER), prop.getProperty(DBConnection.PASSWORD));
		}

		LOGGER.debug("driver class : " + prop.getProperty(DBConnection.DRIVER_CLASS) + ", url : "
				+ prop.getProperty(DBConnection.URL) + ", user : " + prop.getProperty(DBConnection.USER)
				+ ", password : " + prop.getProperty(DBConnection.PASSWORD));

		/*
		 * Class.forName(DRIVER_CLASS); Connection connection = (Connection)
		 * DriverManager.getConnection(URL, USER, PASSWORD);
		 */
		return connection;
	}

	public void test() {

	}

	public static void dbCleanup(Connection con, PreparedStatement ptst, ResultSet rs) {
		close(con);
		close(ptst);
		close(rs);
	}

	public static void dbCleanUp(Connection conn, PreparedStatement ps) {
		close(conn);
		close(ps);
	}

	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}

	public static void close(PreparedStatement pStatement) {
		if (pStatement != null) {
			try {
				pStatement.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}

	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}
}
