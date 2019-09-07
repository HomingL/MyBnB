package Database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Calendars {
	
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
	
	
	
	public static void createCalendarsTable() throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		PreparedStatement constraint = null;
		try {
			conn = getConnection();
			System.out.println("Creating table: calendars...");
			create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "calendars(cid int NOT NULL AUTO_INCREMENT, "
					+ "listid int not null, "
					+ "start_date int not null, "
					+ "end_date int not null, "
					+ "price_per_day int not null, "
					+ "status int not null, "  // 0 for no bookings within; 1 for entirely booked, 2 for partially booked
					+ "UNIQUE(listid, start_date, status), "
					+ "PRIMARY KEY (cid))");
			create.executeUpdate();
			
			constraint = conn.prepareStatement("alter table calendars "
					+ "add constraint fk3 "
					+ "foreign key (listid) "
					+ "references listings (listid) "
					+ "on delete cascade ");
			constraint.executeUpdate();
			System.out.println("Create successful!");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			constraint.close();
			create.close();
			conn.close();
		}
	}
	
	
	//
	public static int insertACalendar(int listid, int start_date, int end_date, int price_per_day, int status) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Inserting a calendar...");
			insert = conn.prepareStatement("INSERT INTO calendars(listid, start_date, "
					+ "end_date, price_per_day, status)"
					+ " VALUES ('" + listid + "', '" + start_date + "', '" + end_date + "', "
							+ "'" + price_per_day + "', '"+status+"')");
			return insert.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			insert.close();
			conn.close();
		}
		return 0;
	}
	
	
	//
	public static int createNewCalender(int listid) throws Exception {
		String temp;
		int year, month, day;
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int start_date;
		System.out.println("Enter the start Date availble (e.g = '20190131'): ");
//		start_date = Integer.parseInt(br.readLine());
		temp = br.readLine();
		if (temp.length() != 8) {
			return 0;
		} else {
			year = Integer.parseInt(temp.substring(0, 4));
			month = Integer.parseInt(temp.substring(4, 6));
			day = Integer.parseInt(temp.substring(6));
			if (year < 1 || year > 2019 || month < 1|| month >12 || day < 1 || day > 31) {
				return 0;
			}
			start_date = Integer.parseInt(temp);
		}
			
		int end_date;
		System.out.println("Enter the end Date availble (e.g = '20200131'): ");
//		end_date = Integer.parseInt(br.readLine());
		temp = br.readLine();
		if (temp.length() != 8) {
			return 0;
		} else {
			year = Integer.parseInt(temp.substring(0, 4));
			month = Integer.parseInt(temp.substring(4, 6));
			day = Integer.parseInt(temp.substring(6));
			if (year < 1 || year > 2019 || month < 1|| month >12 || day < 1 || day > 31) {
				return 0;
			}
			end_date = Integer.parseInt(temp);
		}
		
		int price_per_day;
		System.out.println("Enter the price per day (e.g = '65'): ");
		temp = br.readLine();
		if (!temp.matches("[0-9]+")) {
			return 0;
		}
		price_per_day = Integer.parseInt(temp);
		
		try {
			insertACalendar(listid, start_date, end_date, price_per_day, 0);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}
	
	// 
	public static int findCalendarByStartAndEnd(int listid, int start_date, int end_date) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("select cid "
					+ "from calendars "
					+ "where listid = '"+listid+"'and start_date = '"+start_date+"' and end_date = '"+end_date+"' ");
			// print all info to the user
			rs = query.executeQuery();
			if (rs.next()) {
				return Integer.parseInt(rs.getString("cid"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			conn.close();
			query.close();
			rs.close();
		}
		return 0;
	}
	
	
	
	// 
	public static void showAllAssociatedCalendarsOfAListing(int listid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("SELECT * "
					+ "FROM calendars "
					+ "WHERE listid = '"+listid+"' AND status!=2");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()) {
				if (rs.getString("status").equals("1")) {
					// booked
					System.out.print("BOOKED	");
				} else {
					System.out.print("      	");
				}
				System.out.print(" calendar id: " + rs.getString("cid"));
				System.out.print(";");
				System.out.print(" listing id: " + String.valueOf(listid));
				System.out.print(";");
				System.out.print(" start date availble: " + rs.getString("start_date"));
				System.out.print(";");
				System.out.print(" end date availble: " + rs.getString("end_date"));
				System.out.print(";");
				System.out.print(" price per day: " + rs.getString("price_per_day"));
				System.out.print("\n");
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	
	
	// 
	public static void showAllAssociatedCalendarsOfAHost(int hostid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement(""
					+ "SELECT * "
					+ "FROM calendars C NATURAL JOIN listings L "
					+ "WHERE l.hostid = '"+hostid+"' and status!=2 ");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()) {
				System.out.print("STATUS: ");
				if (rs.getString("C.status").equals("1")) {
					// booked
					System.out.print("BOOKED	");
				} else {
					System.out.print("NOT BOOKED");
				}
				System.out.print(" calendar id: " + rs.getString("C.cid"));
				System.out.print(";");
				System.out.print(" listing id: " + rs.getString("C.listid"));
				System.out.print(";");
				System.out.print(" listing name: " + rs.getString("L.name"));
				System.out.print(";");
				System.out.print(" listing host: " + String.valueOf(hostid));
				System.out.print(";");
				System.out.print(" start date availble: " + rs.getString("C.start_date"));
				System.out.print(";");
				System.out.print(" end date availble: " + rs.getString("C.end_date"));
				System.out.print(";");
				System.out.print(" price per day: " + rs.getString("C.price_per_day"));
				System.out.print(";");
				System.out.println("\n");
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	
	
	public static int removeACalendar(int cid) throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		try {
			conn = getConnection();
			System.out.println("Removing a calendar...");
			create = conn.prepareStatement("DELETE FROM calendars "
					+ "WHERE cid='"+cid+"' and status=0");
			return create.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			create.close();
			conn.close();
		}
		return 0;
	}
	
	
	//
	public static int changePrice(int cid, int newPrice) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Changing the price for you...");
			insert = conn.prepareStatement("UPDATE calendars "
										+ "SET price_per_day='"+newPrice+"' "
										+ "WHERE cid='"+cid+"' AND status=0");
			int i = insert.executeUpdate();
			return i;
		} catch (SQLException e) {
			System.out.println(e);
		}	finally {
			insert.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	public static void changeStatus(int cid, int newstatus) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Updating...");
			insert = conn.prepareStatement("UPDATE calendars "
										+ "SET status='"+newstatus+"' "
										+ "WHERE cid='"+cid+"' ");
			int i = insert.executeUpdate();
			if(i > 0) {
				System.out.println("Update succcessful!");
			} else {
				System.out.println("Update failed, check your input info!");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}	finally {
			insert.close();
			conn.close();
		}
	}
	
	public static int changeAvailbility(int cid, int start_date, int end_date) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Updating...");
			insert = conn.prepareStatement("UPDATE calendars "
										+ "SET start_date='"+start_date+"', end_date='"+end_date+"' "
										+ "WHERE cid='"+cid+"' and status=0 ");
			int i = insert.executeUpdate();
			return i;
		} catch (SQLException e) {
			System.out.println(e);
		}	finally {
			insert.close();
			conn.close();
		}
		return 0;
	}

}
