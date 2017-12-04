package com.hive.HiveClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class HiveGroupBy {
	Connection con;
	Statement st;
	ResultSet rs;
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	static Logger logger = Logger.getLogger(HiveGroupBy.class);

	public void test() throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		con = DriverManager.getConnection("jdbc:hive2://192.168.1.146:10000/bizruntime2", "", "");
		String sql = "FROM users us INSERT OVERWRITE TABLE user_details select us.age,us.position,sum(age) where us.position='student' GROUP BY age,position ";
		st = con.createStatement();
		st.execute(sql);
		
		sql = "select * from user_details";
		rs = st.executeQuery(sql);
		while (rs.next()) {
			logger.info(String.valueOf(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getInt(3)));

		}
		logger.info("Data Retrived Group By  successfully.");
		con.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		HiveGroupBy retrieve = new HiveGroupBy();
		retrieve.test();
	}

}
