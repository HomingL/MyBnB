package Database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Listings {
	
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
	
	public static void createListingsTable() throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		PreparedStatement constraint = null;
		try {
			conn = getConnection();
			System.out.println("Creating table: listings...");
			
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "listings(listid int NOT NULL AUTO_INCREMENT, "
					+ "name varchar(255) not null, "
					+ "hostid int not null, "
					+ "room_type varchar(255) not null, "
					+ "country varchar(255) not null, "
					+ "city varchar(255) not null, "
					+ "latitude float not null, "
					+ "longitude float not null, "
					+ "address varchar(255) not null, "
					+ "postal_code int not null, "
					+ "room varchar(255), "
					+ "UNIQUE (latitude, longitude, postal_code, room), "
					+ "PRIMARY KEY (listid)) ");
			create.executeUpdate();
			
			constraint = conn.prepareStatement("alter table listings "
					+ "add constraint fk1 "
					+ "foreign key (hostid) "
					+ "references users (uid) "
					+ "on delete cascade ");
			constraint.executeUpdate();
			
			System.out.println("Create succcessful!");
		} catch (SQLException e) {
			System.out.println(e);
			System.err.println("Creating table error occured!");
		} finally {
			constraint.close();
			create.close();
			conn.close();
		}
	}
	
	
	
	public static int insertAListing(String name, int hostid, String room_type, 
			String country, String city, float latitude, float longitude, 
			String address, int postal_code, String room) throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		try {
			conn = getConnection();
			
			create = conn.prepareStatement("INSERT INTO listings(name, hostid, room_type, country, city, "
					+ "latitude, longitude, address, postal_code, room) "
					+ "VALUES ('" + name + "', '" + hostid + "', '" + room_type + "', '" + country + "', "
					+ "'" + city + "', '" + latitude + "', '" + longitude + "', "
					+ "'" + address + "', '" + postal_code + "', '" + room + "')");
			int i = create.executeUpdate();
			return i;
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			create.close();
			conn.close();
		}
		return 0;
	}
	
	
	// return the list id created
	public static int createNewListing(int hostid, float latitude, float longitude) throws Exception {
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name;
		System.out.println("Enter listing name (e.g = 'A sweet house like home!'): ");
		name = br.readLine();
		String room_type;
		System.out.println("Enter listing type (e.g = 'full house, apartment, room'): ");
		room_type = br.readLine();
		String country;
		System.out.println("Enter listing country (e.g. = 'Canada'): ");
		country = br.readLine();
		String city;
		System.out.println("Enter listing city (e.g. = 'Toronto'): ");
		city = br.readLine();
		
		if (latitude < 0.0f || latitude > 180.0f) {
			return 0;
		}
	
		if (longitude < 0.0f || longitude > 180.0f) {
			return 0;
		}
		
		String address;
		System.out.println("Enter listing address (e.g. = '1295 Military Trail'): ");
		address = br.readLine();
		int postal_code;
		
		System.out.println("Enter listing 6-digits postal code (e.g. = '123456'): ");
		String temp = br.readLine();
		if (temp.length() != 6) {
			return 0;
		}
		postal_code = Integer.parseInt(temp);
		
		String room;
		System.out.println("Enter listing roomid (e.g. = '208'): ");
		room = br.readLine();
		
		// insert
		return insertAListing(name, hostid, room_type, country, city, latitude, longitude, address, postal_code, room);
		
	}
	
	
	
	public static int removeAListing(int listid, int userid) throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		try {
			conn = getConnection();
			create = conn.prepareStatement("DELETE FROM listings "
					+ "WHERE listid='"+listid+"' AND hostid='"+userid+"'");
			int i = create.executeUpdate();
			return i;
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			create.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	public static void showAllListingsAssociatedWithAHostWithAmenities(int hostid) throws Exception {
		List<Integer> printedId = new ArrayList<Integer>();
		int currId = 0;
		
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("SELECT * "
					+ "FROM listings L, amenities a "
					+ "WHERE L.hostid='"+hostid+"' and a.listid=L.listid");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()){
				currId = Integer.parseInt(rs.getString("l.listid"));
				
				if (!printedId.contains(currId)) {
					printedId.add(currId);
					System.out.print("\n");
					System.out.print(" listid: " + rs.getString("L.listid"));
					System.out.print(";");
					System.out.print(" name: " + rs.getString("L.name"));
					System.out.print(";");
					System.out.print(" room type: " + rs.getString("L.room_type"));
					System.out.print(";");
					System.out.print(" country: " + rs.getString("L.country"));
					System.out.print(";");
					System.out.print(" city: " + rs.getString("L.city"));
					System.out.print(";");
					System.out.print(" latitude: " + rs.getString("L.latitude"));
					System.out.print(";");
					System.out.print(" longitude: " + rs.getString("L.longitude"));
					System.out.print(";");
					System.out.print(" address: " + rs.getString("L.address"));
					System.out.print(";");
					System.out.print(" postal code: " + rs.getString("L.latitude"));
					System.out.print(" amenity: " + rs.getString("a.amenity"));
				} else {
					System.out.print(" amenity: " + rs.getString("a.amenity"));
				}
				
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			rs.close();
			query.close();
			conn.close();
		}
	}
	
	
	public static void showAllListingsAssociatedWithAHost(int hostid) throws Exception {
		
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("SELECT * "
					+ "FROM listings L "
					+ "WHERE L.hostid='"+hostid+"'");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()){
				
				System.out.print(" listid: " + rs.getString("L.listid"));
				System.out.print(";");
				System.out.print(" name: " + rs.getString("L.name"));
				System.out.print(";");
				System.out.print(" room type: " + rs.getString("L.room_type"));
				System.out.print(";");
				System.out.print(" country: " + rs.getString("L.country"));
				System.out.print(";");
				System.out.print(" city: " + rs.getString("L.city"));
				System.out.print(";");
				System.out.print(" latitude: " + rs.getString("L.latitude"));
				System.out.print(";");
				System.out.print(" longitude: " + rs.getString("L.longitude"));
				System.out.print(";");
				System.out.print(" address: " + rs.getString("L.address"));
				System.out.print(";");
				System.out.print(" postal code: " + rs.getString("L.latitude"));
				System.out.print("\n");
				
				
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			rs.close();
			query.close();
			conn.close();
		}
	}

}
