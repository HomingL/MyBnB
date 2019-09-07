package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Amenities {
	
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
	
	
	
	public static void createAmenitiesTable() throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		PreparedStatement constraint = null;
		try {
			conn = getConnection();
			System.out.println("Creating table: amenities...");
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "amenities(aid int NOT NULL AUTO_INCREMENT, "
					+ "listid int not null, "
					+ "amenity varchar(255) not null, "
					+ "UNIQUE(listid, amenity), "
					+ "PRIMARY KEY (aid))");
			create.executeUpdate();
			
			constraint = conn.prepareStatement("alter table amenities "
					+ "add constraint fk2 "
					+ "foreign key (listid) "
					+ "references listings (listid) "
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
	
	
	
	public static int insertAnAmenity(int listid, String amenity) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Inserting an amenity...");
			insert = conn.prepareStatement("INSERT INTO amenities(listid, amenity) "
					+ "VALUES ('"+listid+"', '"+amenity+"')");
			insert.executeUpdate();
			return 1;
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			insert.close();
			conn.close();
		}
		return 0;
	}

}
