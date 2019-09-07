package Database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Users {
	
	public static Connection getConnection() throws ClassNotFoundException {
		try {
			String dbClassName = "com.mysql.cj.jdbc.Driver";
			String CONNECTION = "jdbc:mysql://localhost:3306/c43proj";

			// Register JDBC driver
			Class.forName(dbClassName);

			// Database credentials
			String USER = "root";
			String PASS = "wocaonimadebi1";

			// Establish connection
			Connection conn = DriverManager.getConnection(CONNECTION, USER, PASS);
			return conn;
		} catch (SQLException e) {
			System.out.println(e);
			System.err.println("Connection error occured!");
		}
		return null;
	}
	
	
	
	public static void createUsersTable() throws Exception {	
		try {
			Connection conn = getConnection();
			System.out.println("Creating table: users...");
			PreparedStatement create = conn
					.prepareStatement("CREATE TABLE IF NOT EXISTS users(uid int NOT NULL AUTO_INCREMENT, "
					+ "name varchar(255), "
					+ "password varchar(20), "
					+ "user_type int, "		// 0 for host and 1 for renter
					+ "date_of_birth int, "
					+ "address varchar(255), "
					+ "occupation varchar(255), "
					+ "SIN_no varchar(10), "
					+ "payment_info varchar(64), "  // hosts receive money; renters pay money
					+ "UNIQUE(name, user_type), "
					+ "PRIMARY KEY (uid))");
			create.executeUpdate();
			create.close();
			conn.close();
			System.out.println("Table: users successfully created!");
		} catch (SQLException e) {
			System.out.println("User name has been taken, please choose another one!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	
	public static int insertAUser(String name, String password, int user_type, 
			int date_of_birth, String address, String occupation, String SIN_no, 
			String payment_info) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Inserting a user...");
			insert = conn.prepareStatement("INSERT INTO users(name, password, user_type, "
					+ "date_of_birth, address, occupation, SIN_no, payment_info)"
					+ " VALUES ('" + name + "', '" + password + "', '" + user_type + "', "
							+ "'" + date_of_birth + "', '" + address + "', "
					+ "'" + occupation + "', '" + SIN_no + "', '" + payment_info + "')");
			int i = insert.executeUpdate();
			return i;
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			insert.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	public static int createNewUser(int user_type) throws Exception {
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name;
		System.out.println("Enter account name (e.g = 'Harry Potter'): ");
		name = br.readLine();
		String password;
		System.out.println("Enter account password (e.g = 'CSCC43proj'): ");
		password = br.readLine();
		
		int date_of_birth;
		System.out.println("Enter your date of birth (e.g = '20480131'): ");
		String temp = br.readLine();
		if (temp.length() != 8) {
			return 0;
		} else {
			int year = Integer.parseInt(temp.substring(0, 4));
			int month = Integer.parseInt(temp.substring(4, 6));
			int day = Integer.parseInt(temp.substring(6));
			if (year < 1 || year > 2019 || month < 1|| month >12 || day < 1 || day > 31) {
				return 0;
			}
			date_of_birth = Integer.parseInt(temp);
		}
		
		String address;
		System.out.println("Enter your address (e.g = '1295 Military Trail'): ");
		address = br.readLine();
		String occupation;
		System.out.println("Enter your occupation (e.g = 'Student'): ");
		occupation = br.readLine();
		
		String SIN_no;
		System.out.println("Enter your SIN number (e.g = '88888888'): ");
		SIN_no = br.readLine();
		if (!SIN_no.matches("[0-9]+") || SIN_no.length() != 8) {
			return 0;
		}
		
		String payment_info;
		System.out.println("Enter your payment info (e.g = 8888888888888888): ");
		payment_info = br.readLine();
		if (!payment_info.matches("[0-9]+") || payment_info.length() != 16) {
			return 0;
		}
		
		// insert
		return insertAUser(name, password, user_type, date_of_birth, address, occupation, SIN_no, payment_info);
	}
	
	
	
	public static int removeAUser(int uid) throws Exception {
		Connection conn = null;
		PreparedStatement delete = null;
		
		try {
			conn = getConnection();
			System.out.println("Removing a user...");
			delete = conn.prepareStatement("DELETE FROM users "
					+ "WHERE uid = '"+uid+"'");
			return delete.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			delete.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	public static int userLogin(int user_type) throws Exception {
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name;
		System.out.println("Enter account name (e.g = 'Harry Potter'): ");
		name = br.readLine();
		String password;
		System.out.println("Enter account password (e.g = 'CSCC43proj'): ");
		password = br.readLine();
		// find id
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(""
					+ "SELECT uid "
					+ "FROM users "
					+ "WHERE name = '"+ name +"' AND password = '"+ password +"' "
							+ "AND user_type = '"+ user_type +"' ");
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("uid");
			} else {
				return 0;
			}
		} catch (SQLException e) {
			System.out.print(e);
		} finally {
			stmt.close();
			conn.close();
		}
		return 0;
	}

}
