/**
 * A utility class for managing Connection objects as well as resetting the database to a starting
 * state in case I break it.
 * 
 * @author Andrew Curry
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
	
	// constants
	public static final String TEST_ADDRESS = "jdbc:postgresql://localhost:5432/";
	public static final String TEST_USERNAME = "postgres";
	public static final String TEST_PASSWORD = "password";

	// class / static variables
	private static String databaseAddress;
	private static String databaseUsername;
	private static String databasePassword;
	
	
	/**
	 * Retrieves the necessary information about the database.
	 */
	public static void loadConfiguration() {
		
		databaseAddress = TEST_ADDRESS;
		databaseUsername = TEST_USERNAME;
		databasePassword = TEST_PASSWORD;
	}
	
	/**
	 * Returns the address of the database being used.
	 * @return
	 */
	public static String getAddress() {
		
		return databaseAddress;
	}
	
	/**
	 * Based on the ConnectionUtil method from the demo
	 * Should probably be private but public makes it easier to test
	 * @return
	 */
	public static Connection getConnection() {
		
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(
					databaseAddress,
					databaseUsername,
					databasePassword
					);
		} catch (SQLException e) {
			System.out.println("Unable to obtain connection to database: " + e.getMessage());
		}
		
		return conn;
	}
}
