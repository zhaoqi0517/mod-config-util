package com.zyeeda.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCHelper {

	public static Connection getConnection(String className, String URL, String userName, String password) throws ClassNotFoundException, SQLException {
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

	public static String getVersion(String className, String URL, String userName, String password) throws SQLException, ClassNotFoundException {
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			con = getConnection(className, URL, userName, password);
			stat = con.createStatement();
			rs = stat.executeQuery("select version() as version");

			String version = null;
			while (rs.next()) {
				version = rs.getString("version");
			}
			
			String[] versions = {};
			
			try {
				versions = version.split("[.]");
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("数据库版本获取异常");
			}
			
			if (new Integer(versions[0]).intValue() >= 5) {
				if (new Integer(versions[1]) > 5) {
					return "mysql56";
				} else {
					return "mysql55";
				}
			} else {
				throw new SQLException("不支持 MySQL 5 以下的数据库");
			}
		}  finally {
			if (rs != null) {
				rs.close();
			}
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

    public static String generateURL(String host, String port, String dbName, Boolean isParam) {
    	if (isParam) {
    		return generateURL(host, port, dbName);
    	} else {
    		return "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    	}
    }
    
    public static String generateURL(String host, String port, String dbName) {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?pinGlobalTxToPhysicalConnection=true&amp;useUnicode=yes&amp;";
    }
}

