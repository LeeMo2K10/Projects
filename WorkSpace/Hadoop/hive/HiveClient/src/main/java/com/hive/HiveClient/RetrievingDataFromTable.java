package com.hive.HiveClient;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class RetrievingDataFromTable {
	Connection con;
	Statement st;
	ResultSet rs;
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	// static Logger logger = Logger.getLogger(DataLoadIntoTable.class);

	public void test() throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		con = DriverManager.getConnection("jdbc:hive2://192.168.1.146:10000/bizruntime2", "", "");
		String sql = "FROM customers emp INSERT OVERWRITE TABLE cust_details SELECT emp.CustomerName,emp.ContactName,emp.Address,emp.City,emp.PostalCode";
		st = con.createStatement();
		st.execute(sql);
		
		System.out.println("Data Retrieved into Table successfully.");
		System.out.println("Running: " + sql);
		sql = "select * from cust_details";
		rs = st.executeQuery(sql);
		while (rs.next()) {

			System.out.println(String.valueOf(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "/t"
					+ "\t"+rs.getString(4) + "\t" + rs.getInt(5)));

		}
		con.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		RetrievingDataFromTable retrieve = new RetrievingDataFromTable();
		retrieve.test();
	}

}