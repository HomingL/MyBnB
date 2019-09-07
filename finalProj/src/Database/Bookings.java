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

public class Bookings {
	
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
	
	
	
	public static void createBookingsTable() throws Exception {
		Connection conn = null;
		PreparedStatement create = null;
		PreparedStatement constraint = null;
		try {
			conn = getConnection();
			System.out.println("Creating table: bookings...");
			create = conn
					.prepareStatement("CREATE TABLE IF NOT EXISTS "
							+ "bookings(bid int NOT NULL AUTO_INCREMENT, "
				+ "cid int not null, "
				+ "renter_id int not null, "
				+ "check_in_date int not null, "
				+ "check_out_date int not null, "
				+ "is_cancelled int not null, "  // 0 for not cancelled, 1 for cancelled by host, 2 for cancelled by renter
				+ "ratingOnHost int, "
				+ "ratingOnRenter int, "
				+ "commentOnHost varchar(255), "
				+ "commentOnRenter varchar(255), "
				+ "commentOnListing varchar(255), "
				+ "status int not null, "  // 0 for not_started; 1 for staying; 2 for finished;
				+ "UNIQUE(cid, renter_id, check_in_date, is_cancelled), " 
				+ "PRIMARY KEY (bid))");
			create.executeUpdate();
			
			constraint = conn.prepareStatement("alter table bookings "
					+ "add constraint fk6 "
					+ "foreign key (cid) "
					+ "references calendars (cid) "
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
	
	
	
	public static void insertABooking(int cid, int renter_id, int check_in_date, int check_out_date) throws Exception {
		Connection conn = null;
		PreparedStatement insert = null;
		try {
			conn = getConnection();
			System.out.println("Inserting a booking...");
			insert = conn.prepareStatement("INSERT INTO bookings (cid, renter_id, "
					+ "check_in_date, check_out_date, is_cancelled, ratingOnHost, ratingOnRenter, commentOnHost, commentOnRenter, commentOnListing, status)"
					+ " VALUES ('"+cid+"', '" + renter_id + "', '" + check_in_date + "', "
							+ "'" + check_out_date + "', 0, NULL, NULL, NULL, NULL, NULL, 0)");
			int i = insert.executeUpdate();
			if(i > 0) {
				System.out.println("Insert succcessful!");
			} else {
				System.out.println("Insert failed, check your input info!");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}	finally {
			insert.close();
			conn.close();
		}
	}
	
	
	// undone
	public static int createABooking(int cid, int userid) throws Exception {
		String temp;
		int year, month, day, check_in_date, check_out_date = 0;
		
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter the date that you want to check in.");
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
			check_in_date = Integer.parseInt(temp);
		}
		
		System.out.println("Enter the date that you want to check out.");
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
			check_out_date = Integer.parseInt(temp);
		}
		
		// check validity of inputs
		if (check_in_date > check_out_date) {
			System.out.println("Sorry the date chosen is not valid, please check!");
			return 0;
		} 
		
		// set up connection and query
		Connection conn = null;
		PreparedStatement query1 = null;
		ResultSet rs1 = null;
		try {
			conn = getConnection();
			// see if the user has selected a valid calendar to book
			query1 = conn.prepareStatement("SELECT * "
					+ "FROM calendars "
					+ "WHERE cid='"+cid+"' AND start_date <= '"+check_in_date+"' "
							+ "AND '"+check_out_date+"' <= end_date AND status != 1 ");
			rs1 = query1.executeQuery();
			
			if (rs1.next()) {
				// check if the period of staying wanted is achievable
				int listid = Integer.parseInt(rs1.getString("listid"));
				int start_date = Integer.parseInt(rs1.getString("start_date"));
				int end_date = Integer.parseInt(rs1.getString("end_date"));
				int price_per_day = Integer.parseInt(rs1.getString("price_per_day"));
				
				if (start_date == check_in_date && end_date == check_out_date) {
					// the entire calendar tuple is now booked
					Calendars.changeStatus(cid, 1);
					
					insertABooking(cid, userid, check_in_date, check_out_date);
				} else if (start_date != check_in_date && end_date == check_out_date) {
					// the calendar tuple is partially booked
					Calendars.changeStatus(cid, 2);
					
					Calendars.insertACalendar(listid, start_date, check_in_date, price_per_day, 0);
					Calendars.insertACalendar(listid, check_in_date, end_date, price_per_day, 1);
					
					// book the new calendar
					int newcid = Calendars.findCalendarByStartAndEnd(listid, check_in_date, check_out_date);
					insertABooking(newcid, userid, check_in_date, check_out_date);
				} else if (start_date == check_in_date && end_date != check_out_date) {
					// the calendar tuple is partially booked
					Calendars.changeStatus(cid, 2);
					
					Calendars.insertACalendar(listid, start_date, check_out_date, price_per_day, 1);
					Calendars.insertACalendar(listid, check_out_date, end_date, price_per_day, 0);
					
					// book the new calendar
					int newcid = Calendars.findCalendarByStartAndEnd(listid, check_in_date, check_out_date);
					insertABooking(newcid, userid, check_in_date, check_out_date);
				} else {
					// the calendar tuple is partially booked
					Calendars.changeStatus(cid, 2);
					
					Calendars.insertACalendar(listid, start_date, check_in_date, price_per_day, 0);
					Calendars.insertACalendar(listid, check_in_date, check_out_date, price_per_day, 1);
					Calendars.insertACalendar(listid, check_out_date, end_date, price_per_day, 0);
					
					// book the new calendar
					int newcid = Calendars.findCalendarByStartAndEnd(listid, check_in_date, check_out_date);
					insertABooking(newcid, userid, check_in_date, check_out_date);
					
					return 1;
				}
			} else {
				System.out.println("Sorry the date chosen is not valid for booking, please check or book another listing!");
			}

		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			query1.close();
			conn.close();
		}
		return 0;
		
	}
	
	
	
	// find the renter id of the booking
	public static int findRenterIdOfABooking(int bid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("SELECT renter_id "
					+ "FROM bookings "
					+ "WHERE bid='"+bid+"'");
			rs = query.executeQuery();
			if(rs.next()) {
				return Integer.valueOf(rs.getString("renter_id"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			rs.close();
			query.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	// find the renter id of the booking
	public static int findHostIdOfABooking(int bid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("SELECT l.hostid "
					+ "FROM bookings b, calendars c, listings l "
					+ "WHERE b.cid=c.cid and c.listid=l.listid and b.bid='"+bid+"'");
			rs = query.executeQuery();
			if(rs.next()) {
				return Integer.valueOf(rs.getString("l.hostid"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			rs.close();
			query.close();
			conn.close();
		}
		return 0;
	}
	
	
	
	// 
	public static void showAllBookingsAssociatedWithAHost(int hostid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement(""
					+ "SELECT * "
					+ "FROM bookings b, calendars c, listings l "
					+ "WHERE b.cid=c.cid and c.listid=l.listid and b.is_cancelled=0 AND l.hostid='"+hostid+"' ");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()) {
				System.out.print("STATUS: ");
				if (rs.getString("b.status").equals("0")) {
					// not started
					System.out.print("NOT STARTED");
				} else if (rs.getString("b.status").equals("1")) {
					// staying
					System.out.print("STAYING    ");
				} else {
					// finished
					System.out.print("FINISHED   ");
				}
				System.out.print(" booking id: " + rs.getString("b.bid"));
				System.out.print(";");
				System.out.print(" booked on listing: " + rs.getString("c.listid"));
				System.out.print(";");
				System.out.print(" rented by: " + rs.getString("b.renter_id"));
				System.out.print(";");
				System.out.print(" check in on date: " + rs.getString("b.check_in_date"));
				System.out.print(";");
				System.out.print(" check out on date: " + rs.getString("b.check_out_date"));
				System.out.print(";");
				if (rs.getString("ratingOnHost") != null) {
					System.out.print(" rating on you: " + rs.getString("b.ratingOnHost"));
					System.out.print(";");
				}
				if (rs.getString("ratingOnRenter") != null) {
					System.out.print(" you rated: " + rs.getString("b.ratingOnRenter"));
					System.out.print(";");
				}	
				if (rs.getString("commentOnHost") != null) {
					System.out.print(" comments on you: " + rs.getString("b.commentOnHost"));
					System.out.print(";");
				}
				if (rs.getString("commentOnRenter") != null) {
					System.out.print(" you commented: " + rs.getString("b.commentOnRenter"));
					System.out.print(";");
				}
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
	public static void showAllBookingsAssociatedWithARenter(int renterid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement(""
					+ "SELECT * "
					+ "FROM bookings b, calendars c, listings l "
					+ "WHERE b.cid=c.cid and c.listid=l.listid and is_cancelled=0 and renter_id='"+renterid+"' ");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()) {
				System.out.print("STATUS: ");
				if (rs.getString("b.status").equals("0")) {
					// not started
					System.out.print("NOT STARTED");
				} else if (rs.getString("b.status").equals("1")) {
					// staying
					System.out.print("STAYING    ");
				} else {
					// finished
					System.out.print("FINISHED   ");
				}
				System.out.print(" booking id: " + rs.getString("b.bid"));
				System.out.print(";");
				System.out.print(" booked on listing: " + rs.getString("c.listid"));
				System.out.print(";");
				System.out.print(" own by host: " + rs.getString("l.hostid"));
				System.out.print(";");
				System.out.print(" check in on date: " + rs.getString("b.check_in_date"));
				System.out.print(";");
				System.out.print(" check out on date: " + rs.getString("b.check_out_date"));
				System.out.print(";");
				if (rs.getString("ratingOnHost") != null) {
					System.out.print(" rating on you: " + rs.getString("b.ratingOnHost"));
					System.out.print(";");
				}
				if (rs.getString("ratingOnRenter") != null) {
					System.out.print(" you rated: " + rs.getString("b.ratingOnRenter"));
					System.out.print(";");
				}	
				if (rs.getString("commentOnHost") != null) {
					System.out.print(" comments on you: " + rs.getString("b.commentOnHost"));
					System.out.print(";");
				}
				if (rs.getString("commentOnRenter") != null) {
					System.out.print(" you commented: " + rs.getString("b.commentOnRenter"));
					System.out.print(";");
				}
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
	
	
	
	// cancel a booking
	public static int cancellABooking(int bid, int fromuserid, int touserid) throws Exception {
		int cid = 0;
		
		Connection conn = null;
		PreparedStatement update = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		
		// find the renter id of the booking
		int renterid = findRenterIdOfABooking(bid);
		// find the host id of the booking
		int hostid = findHostIdOfABooking(bid);
		
		// check if the user is either the host or the renter of the booking
		if (fromuserid == renterid) {
			// update is_cancelled field in the table to be 2
			try {
				conn = getConnection();
				
				update = conn.prepareStatement("UPDATE bookings "
						+ "SET is_cancelled = 2 "
						+ "WHERE bid='"+bid+"'");
				update.executeUpdate();
				
				// update the booked calendar to be available again
				query = conn.prepareStatement("select cid "
						+ "from bookings "
						+ "where bid='"+bid+"'");
				rs = query.executeQuery();
				if (rs.next()) {
					cid = Integer.parseInt(rs.getString("cid"));
				}
				Calendars.changeStatus(cid, 0);
				
				// insert message into the message table
				String msg = "One of your bookings was cancelled by renter:" + String.valueOf(fromuserid) 
							+ " on booking: " + String.valueOf(bid) + "\n";
				Messages.insertAMessage(touserid, msg);
				
				return 1;
			} catch (SQLException e) {
				System.out.println(e);
			} finally {
				conn.close();
			}
		// else the user has no right to cancel the specified booking
		} else if (fromuserid == hostid) {
			// update is_cancelled field in the table to be 1
			try {
				conn = getConnection();
				
				update = conn.prepareStatement("UPDATE bookings "
						+ "SET is_cancelled = 1 "
						+ "WHERE bid='"+bid+"'");
				update.executeUpdate();
				
				// insert message into the message table
				String msg = "One of your bookings was cancelled by host:" + String.valueOf(fromuserid) 
							+ " on booking: " + String.valueOf(bid) + "\n";
				Messages.insertAMessage(touserid, msg);
			} catch (SQLException e) {
				System.out.println(e);
			} finally {
				conn.close();
			}
			
			return 1;
		} else {
			return 0;
		}
	return 1;
	}
	
	
	public static int getStatus(int bid) throws Exception {
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			query = conn.prepareStatement("select status "
					+ "from bookings "
					+ "where bid='"+bid+"'");
			rs = query.executeQuery();
			if (rs.next()) {
				return Integer.parseInt(rs.getString("status"));
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}	finally {
			query.close();
			conn.close();
		}
		return -1;
	}
	
	
	public static int insertACommentOnHost(int fromuserid, int touserid, int bookingid, String comment) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		int status = getStatus(bookingid);
		
		if (status == 2) {
		// if the status is finished
			try {
				// insert comment into the table
				conn = getConnection();
				update = conn.prepareStatement("UPDATE bookings "
								+ "SET commentOnHost='"+comment+"' "
								+ "WHERE bid='"+bookingid+"'");
				int i = update.executeUpdate();
				
				return i;
			} catch (SQLException e) {
				System.out.println(e);
			}	finally {
				update.close();
				conn.close();
			}
		} else {
		// else the booking is not ready for comment
			return 0;
		}

		return 0;
	}
	
	
	public static int insertACommentOnListing(int fromuserid, int touserid, int bookingid, String comment) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		int status = getStatus(bookingid);
		
		if (status == 2) {
		// if the status is finished
			try {
				// insert comment into the table
				conn = getConnection();
				update = conn.prepareStatement("UPDATE bookings "
								+ "SET commentOnListing='"+comment+"' "
								+ "WHERE bid='"+bookingid+"'");
				int i = update.executeUpdate();
				
				return i;
			} catch (SQLException e) {
				System.out.println(e);
			}	finally {
				update.close();
				conn.close();
			}
		} else {
		// else the booking is not ready for comment
			return 0;
		}

		return 0;
	}
	
	
	
	public static int insertACommentOnRenter(int fromuserid, int touserid, int bookingid, String comment) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		int status = getStatus(bookingid);
		
		if (status == 2) {
		// if the status is finished
			try {
				// insert comment into the table
				conn = getConnection();
				update = conn.prepareStatement("UPDATE bookings "
								+ "SET commentOnRenter='"+comment+"' "
								+ "WHERE bid='"+bookingid+"'");
				int i = update.executeUpdate();
				
				return i;
			} catch (SQLException e) {
				System.out.println(e);
			}	finally {
				update.close();
				conn.close();
			}
		} else {
		// else the booking is not ready for comment
			return 0;
		}

		return 0;
	}
	
	
	
	public static int insertARatingOnHost(int fromuserid, int touserid, int bookingid, int rating) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		int status = getStatus(bookingid);
		
		if (status == 2) {
		// if the status is finished
			try {
				// insert comment into the table
				conn = getConnection();
				update = conn.prepareStatement("UPDATE bookings "
								+ "SET ratingOnHost='"+rating+"' "
								+ "WHERE bid='"+bookingid+"'");
				int i = update.executeUpdate();
				
				return i;
			} catch (SQLException e) {
				System.out.println(e);
			}	finally {
				update.close();
				conn.close();
			}
		} else {
		// else the booking is not ready for comment
			return 0;
		}

		return 0;
	}
	
	
	
	public static int insertARatingOnRenter(int fromuserid, int touserid, int bookingid, int rating) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		int status = getStatus(bookingid);
		
		if (status == 2) {
		// if the status is finished
			try {
				// insert comment into the table
				conn = getConnection();
				update = conn.prepareStatement("UPDATE bookings "
								+ "SET ratingOnRenter='"+rating+"' "
								+ "WHERE bid='"+bookingid+"'");
				int i = update.executeUpdate();
				
				return i;
			} catch (SQLException e) {
				System.out.println(e);
			}	finally {
				update.close();
				conn.close();
			}
		} else {
		// else the booking is not ready for comment
			return 0;
		}

		return 0;
	}
	
	
	
	public static void updateStatusOfABooking(int bid, int newstatus) throws Exception {
		Connection conn = null;
		PreparedStatement update = null;
		try {
			conn = getConnection();
			System.out.println("Rating on renter...");
			update = conn.prepareStatement("UPDATE bookings "
							+ "SET status = '"+newstatus+"' "
							+ "WHERE bid = '"+bid+"'");
			int i = update.executeUpdate();
			if(i > 0) {
				System.out.println("Update succcessful!");
			} else {
				System.out.println("Update failed, check your input info!");
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			update.close();
			conn.close();
		}
	}

}
