package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=fast_delivery_db;encrypt=false;trustServerCertificate=true";

	private static final String USER = "shop_user";
	private static final String PASSWORD = "33521412";

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver not found!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}
}