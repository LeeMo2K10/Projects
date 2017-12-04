package com.hive.HiveClient;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import java.sql.DriverManager;

public class HiveClientJdbc {

	public static Logger logger = Logger.getLogger(HiveClientJdbc.class);

	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public void test() throws SQLException {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Connection con = DriverManager.getConnection("jdbc:hive2://192.168.1.146:10000/bizruntime5", "", "");
		Statement stmt = con.createStatement();
		String tableName = "users";
		String sql = "create table users(uid int,age smallint,gender string,position string,regId Bigint) ROW FORMAT DELIMITED FIELDS TERMINATED BY '|' STORED AS TEXTFILE LOCATION '/user/hive/warehouse/output/output.txt'";
		stmt.execute("drop table if exists " + tableName);
		stmt.execute(sql);

		String sql1 = "show tables";
		logger.info("Running: " + sql1);

		ResultSet res = stmt.executeQuery(sql1);
		while (res.next()) {
			logger.info(res.getString(1));
		}

		String sql3 = "describe users";
		logger.info("Running: " + sql3);
		res = stmt.executeQuery(sql3);
		while (res.next()) {
			logger.info(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3));

		}
		String filepath = "/inputs/user.txt";
		sql = "load data inpath '" + filepath + "' overwrite into table " + tableName;
		logger.info("Running: " + sql);
		stmt.execute(sql);

		sql = "select * from " + tableName;

		logger.info("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			logger.info(String.valueOf(res.getInt(1) + "\t" + res.getInt(2) + "\t" + res.getString(3) + "\t"
					+ res.getString(4) + "\t" + res.getInt(5)));
		}

		String sql5 = "select count(*) from " + tableName + " where gender='M'";
		logger.info("Running: " + sql5);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			logger.info(res.getString(3));
		}
	}

	public static void main(String[] args) throws SQLException {
		HiveClientJdbc hc = new HiveClientJdbc();
		hc.test();
	}
}
