package com.storm.kafka.mysql;

import java.sql.DriverManager;

import com.mysql.jdbc.Connection;

public class MySqlConnection {

	private Connection conn;

	public Connection getConnection() {
		return conn;
	}

	public boolean open() {
		boolean successful = true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.1.54:3306/bizruntime", "root", "root");
		} catch (Exception ex) {
			successful = false;
			ex.printStackTrace();
		}
		return successful;
	}

	public boolean close() {
		if (conn == null) {
			return false;
		}

		boolean successful = true;
		try {
			conn.close();
		} catch (Exception ex) {
			successful = false;
			ex.printStackTrace();
		}

		return successful;
	}
}
