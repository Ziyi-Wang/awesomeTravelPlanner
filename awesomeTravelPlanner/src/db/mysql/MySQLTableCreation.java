package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	public static void main(String[] args) {
		try {
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);

			if (conn == null) {
				System.out.println("error");
				return;
			}

			createRouteTable(conn);

			createUserTable(conn);

//			createPlaceTable(conn);

			conn.close();
			System.out.println("Tables created successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createPlaceTable(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		String sql = "DROP TABLE IF EXISTS places";
		statement.executeUpdate(sql);

	}

	private static void createUserTable(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		String sql = "DROP TABLE IF EXISTS users";
		statement.executeUpdate(sql);

		sql = "CREATE TABLE users (" + "user_id VARCHAR(255) NOT NULL," + "password VARCHAR(255) NOT NULL,"
				+ "first_name VARCHAR(255)," + "last_name VARCHAR(255)," + "PRIMARY KEY (user_id)" + ")";
		statement.executeUpdate(sql);
	}

	private static void createRouteTable(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		String sql = "DROP TABLE IF EXISTS routes";
		statement.executeUpdate(sql);

		sql = "CREATE TABLE routes (" + "user_id VARCHAR(255) NOT NULL," + "place_id VARCHAR(255)," + "day INT,"
				+ "index_of_day INT" + ")";
		statement.executeUpdate(sql);
	}
}
