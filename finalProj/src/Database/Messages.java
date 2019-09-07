package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Messages {
	
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
	
	
	
	public static void createMessagesTable() throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		PreparedStatement constraint = null;
		try {
			conn = getConnection();
			System.out.println("Creating table: messages...");
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "messages(mid int NOT NULL AUTO_INCREMENT, "
					+ "to_userid int not null, "
					+ "msg varchar(255) not null, "
					+ "status int not null, "  // 0 for not printed to user yet; 1 for printed
					+ "PRIMARY KEY (mid))");
			create.executeUpdate();
			
			constraint = conn.prepareStatement("alter table messages "
					+ "add constraint fk5 "
					+ "foreign key (to_userid) "
					+ "references users (uid) "
					+ "on delete cascade ");
			constraint.executeUpdate();
			System.out.println("Create succcessful!");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			constraint.close();
			create.close();
			conn.close();
		}
	}
	
	
	
	public static void insertAMessage(int touserid, String msg) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			insert = conn.prepareStatement("INSERT INTO messages(to_userid, msg, status) "
					+ " VALUES ('"+touserid+"', '"+msg+"', 0)");
			insert.executeUpdate();
			System.out.println("Insert succcessful!");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			insert.close();
			conn.close();
		}
	}
	
	
	
	public static void printAllRelatedMessages(int touserid) throws Exception {
		//  message index
		int i = 1;

		Connection conn = null;
		PreparedStatement insert = null;
		PreparedStatement update = null;
		ResultSet rs = null;
		try {
			System.out.println("Checking new message for you!");
			conn = getConnection();
			insert = conn.prepareStatement("SELECT * " 
					+ "FROM messages "
					+ "WHERE to_userid='"+touserid+"' and status=0 ");
			rs = insert.executeQuery();
			
			if (rs.next()) {
				while(rs.next()) {
					System.out.println("message#" + String.valueOf(i) + ": " + rs.getString("msg"));
					i++;
				}
				System.out.println("\n");
			} else {
				System.out.println("No new message for you!\n");
			}
			
			// set all message associated with the user to be status=1
			update = conn.prepareStatement("update messages "
					+ "set status=1 "
					+ "where to_userid='"+touserid+"'");
			update.executeUpdate();
			
			
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			update.close();
			rs.close();
			insert.close();
			conn.close();
		}
	}

}
