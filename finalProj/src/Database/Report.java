package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Report {
	
	public static void displayResultSet(ResultSet rs) throws SQLException {
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNumber = rsmd.getColumnCount();
		
		String heading = "";
		for (int i = 1; i<= colNumber; i++) {
			
			System.out.print(String.format("|%-20s", rsmd.getColumnName(i)));
			
		}
		
		System.out.println(heading);
		while (rs.next()) {
			String msg = "";
			for (int i=1; i <= colNumber; i ++) {
				System.out.print(String.format("|%-20s", rs.getString(i)));
			}
			
		}
	}
	
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
	// a
	public static void reportNumOfBookingsByCity(int startDate, int endDate) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			System.out.print(startDate);
			System.out.print(endDate);
			conn = getConnection();
			System.out.println(
					"reporting the total number of bookings in date specified by postal code within a city...");
			
			String sql = "SELECT city, count(*) AS NumBooking "
					+ "FROM c43proj.listings, c43proj.bookings, c43proj.calendars "
					+ "WHERE bookings.cid = calendars.cid AND calendars.listid = listings.listid "
					+ "AND check_in_date >= " + startDate + " "
					+ "AND check_out_date <= " + endDate + " "
					+ "GROUP BY city;";
			System.out.println(sql);
			
			query = conn.createStatement();
			// 1. select from booking_history |x| listings
			// 2. set date
			// 3. set city
			// 4. group by postal code
			
			rs = query.executeQuery(sql);
			
			Report.displayResultSet(rs);
			// find the number of histories in the result

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
   
	//b
	public static void reportNumOfBookingsByPostalCodeInCity(int startDate, int endDate, String city) throws ClassNotFoundException, SQLException{
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			System.out.println(
					"reporting the total number of bookings in date specified by postal code within a city...");
			
			String sql = "SELECT city, count(*) AS NumBooking "
					+ "FROM c43proj.listings, c43proj.bookings, c43proj.calendars "
					+ "WHERE bookings.cid = calendars.cid AND calendars.listid = listings.listid "
					+ "AND check_in_date >= " + startDate + " "
					+ "AND check_out_date <= " + endDate + " "
					+ "AND city = " + city + ";";
			query = conn.prepareStatement(sql);
			// 1. select from booking_history |x| listings
			// 2. set date
			// 3. set city
			// 4. group by postal code
			
			query = conn.prepareStatement(sql);
			query.setString(1, city);
			query.setInt(2, startDate);
			query.setInt(3, endDate);
			
			rs = query.executeQuery(sql);
			// find the number of histories in the result
			while (rs.next()){
				System.out.print(rs.getString(0) + rs.getString(1) + rs.getString(2));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	



	/* this one is not in perform report
	public static void reportNumOfListingsByCountry() throws ClassNotFoundException {
		try {
			Connection conn = getConnection();
			System.out.println("reporting the total number of listing per country per city...");
			Statement query = conn.createStatement();
			// 1. select from listings
			// 2. group by country + city
			String sql = "SELECT country, count(*)" + "FROM listings" + "GROUP BY country";
			ResultSet rs = query.executeQuery(sql);
			// find the number of listings in the result
			while (rs.next()){
				System.out.print(rs.getString(0) + rs.getString(1) + rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
		
	}
	*/
	
	// c
	public static void reportNumOfListingsByCountryCity() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			System.out.println("reporting the total number of listing per country per city...");
			query = conn.createStatement();
			// 1. select from listings
			// 2. group by country + city
			String sql = "SELECT city, country, count(*) AS numListings FROM c43proj.listings GROUP BY city, country;";
			System.out.println(sql);
			rs = query.executeQuery(sql);
			
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
		
	}
	// d 
	public static void reportNumOfListingsByCountryCityPostalCode() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			System.out.println("reporting the total number of listing per country per city per postal code...");
			query = conn.createStatement();
			// 1. select from listings
			// 2. group by country + city + postal code
			String sql = "SELECT city, country, postal_code, count(*) FROM c43proj.listings GROUP BY postal_code, city, country;";
			rs = query.executeQuery(sql);
			// find the number of listings in the result
			System.out.println(sql);
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}

	//e
	public static void reportRankListOnHostWithTheirListingsPerCountry() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// 1. select from listings + hosting_list
			// 2. group by country
			// 3. desc order
			System.out.println("reporting a ranked list on host with desc order of listings owned per country...");
			query = conn.createStatement();
			String sql = "SELECT hostid, users.name, country, count(*) AS numListings "  
						+ "FROM c43proj.listings, c43proj.users " 
						+ "WHERE hostid = uid " 
						+ "GROUP BY hostid, country " 
						+ "ORDER BY count(*) DESC;";
			System.out.println(sql);
			rs = query.executeQuery(sql);

			Report.displayResultSet(rs);
			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}

	//f
	public static void reportRankListOnHostWithTheirListingsPerCountryCity() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// 1. select from listings + hosting_list
			// 2. group by country + city
			// 3. desc order
			System.out.println(
					"reporting a ranked list on host with desc order of listings owned per country per city...");
			query = conn.createStatement();
			String sql = "SELECT hostid, users.name, city, country, count(*) AS numListings "  
					+ "FROM c43proj.listings, c43proj.users " 
					+ "WHERE hostid = uid " 
					+ "GROUP BY hostid, city,country " 
					+ "ORDER BY count(*) DESC;";
			
			System.out.println(sql);
			rs = query.executeQuery(sql);
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	//g  correct
	public static void reportPotentialCommercialHostsByCountryCity() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// find a ranked list
			// 1. select from listings + hosting_list
			// 2. group by country + city
			// 3. desc order
			System.out.println("reporting potential commercial hosts by country, city...");
			
			String subSql1 = "SELECT hostid, name, count(*)AS numListingsHosts FROM c43proj.listings GROUP BY hostid, city, country";
			String subSql2 = "SELECT city, country, count(*)AS numListingsCountries FROM c43proj.listings GROUP BY city, country";
			String sql = "SELECT hostid, city, country, numListingsCountries, numListingsHosts FROM ("+ subSql1 + ")sub1, (" + subSql2 + ")sub2 "
					+ "WHERE numListingsHosts > (numListingsCountries / 10);";
			
			System.out.println(sql);
			query = conn.createStatement();
			rs = query.executeQuery(sql);
			Report.displayResultSet(rs);
			// extract potential commercial hosts from the list
			

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	// h
	public static void reportRentersByNumOfBookingsByPeriod(int startDate, int endDate) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			//
			System.out.println("reporting renters by number of bookings made in a specified period of time...");
			query = conn.createStatement();
			String sql = "SELECT renter_id, count(*)AS numBookings FROM c43proj.bookings "
					+ "WHERE check_in_date >= " + startDate + " "
					+ "AND check_in_date <= " + endDate + " "
					+ "GROUP BY renter_id;";
			rs = query.executeQuery(sql);
			
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	// i
	public static void reportRentersByNumOfBookingsByPeriodCity(int startDate, int endDate) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// with constrain: made at least 2 bookings in the year
			System.out
					.println("reporting renters by number of bookings made in a specified period of time per city...");
			query = conn.createStatement();
			String sql = "SELECT renter_id, city, count(*)AS numBookings "
					+ "FROM c43proj.bookings, c43proj.calendars, c43proj.listings "
					+ "WHERE listings.listid = calendars.listid "
					+ "AND bookings.cid = calendars.cid "
					+ "AND check_in_date >= " + startDate + " "
					+ "AND check_in_date <= " + endDate + " "
					+ "GROUP BY city, renter_id;";
			
			
			rs = query.executeQuery(sql);
			Report.displayResultSet(rs);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
	
	// j
	public static void reportHostsWithLargestNumOfCancellationWithinYear(String year) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		
		try {
			int startDate = Integer.parseInt(year + "0101");  // matches date: year-xx-xx
			int endDate = Integer.parseInt(year + "1231");
			System.out.println(startDate);
			System.out.println(endDate);
			conn = getConnection();
			System.out.println("Reporting the hosts with the largest number of cancellation within year specified...");
			query = conn.createStatement();
			// subquery return all hostid with cancellation within the specified year
			String sql = "SELECT uid, count(DISTINCT bid)AS numCancelf from c43proj.users, c43proj.bookings, c43proj.calendars "
					+ "WHERE users.uid = bookings.renter_id "
					+ "AND bookings.is_cancelled = 0 "
					+ "AND check_in_date >= " + startDate + " "
					+ "AND check_out_date <= " + endDate + " "
					+ "GROUP BY uid "
					+ "HAVING numcancelf = "
						+ "(SELECT MAX(numCancel)AS numCancel "
						+ "FROM (SELECT uid, name, count(DISTINCT bid)AS numCancel "
							+ "FROM c43proj.users, c43proj.bookings, c43proj.calendars "
							+ "WHERE users.uid = bookings.renter_id "
							+ "AND bookings.is_cancelled = 0 "
							+ "AND check_in_date >= " + startDate + " "
							+ "AND check_out_date <= " + endDate + " "
							+ "GROUP BY uid) a);";
			
			System.out.println(sql);
			
			rs = query.executeQuery(sql);
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}

	// k  Renters comments on the listings
	public static void rentersWordCloudOnListings() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			System.out.println("Reporting the hosts with the largest number of cancellation within year specified...");
			query = conn.createStatement();
			// subquery return all hostid with cancellation within the specified year
			
			String sql = "SELECT listings.listid, commentOnListing, count(*)AS commentNum FROM c43proj.bookings, c43proj.listings WHERE commentOnListing is not NULL GROUP BY listid, commentOnListing ORDER BY commentNum DESC;";
			System.out.println(sql);
			rs = query.executeQuery(sql);
			
			Report.displayResultSet(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			conn.close();
			query.close();
			rs.close();
		}
	}
  

}