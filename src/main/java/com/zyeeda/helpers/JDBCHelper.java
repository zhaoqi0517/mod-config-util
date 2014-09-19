package com.zyeeda.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCHelper {

	private static Connection getConnection(String className, String URL, String userName, String password) throws ClassNotFoundException, SQLException {
		Class.forName(className);
		return DriverManager.getConnection(URL, userName, password);
	}

	public static boolean executeCreateDB(String className, String URL, String userName, String password, String dbName) throws SQLException, ClassNotFoundException {
		Connection con = null;
		Statement stat = null;
		try {
			con = getConnection(className, URL, userName, password);
			stat = con.createStatement();
			int count = stat.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);

			return count > 0 ? true : false;
		}  finally {
			if (stat != null) {
				stat.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}

    public static String generateURL(String host, String port) {
        return "jdbc:mysql://" + host + ":" + port ;
    }

    public static String generateURL(String host, String port, String dbName) {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?pinGlobalTxToPhysicalConnection=true&amp;useUnicode=yes&amp;";
    }
}

