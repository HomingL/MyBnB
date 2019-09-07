package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToolKits {
	
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
	
	public static float suggestPrice(float latitude, float longitude) throws Exception {
		float la1 = latitude-0.01f;
		float la2 = latitude+0.01f;
		float lo1 = longitude-0.01f;
		float lo2 = longitude+0.01f;
		
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("select AVG(c.price_per_day) "
					+ "from listings l, calendars c "
					+ "where l.listid=c.listid and l.latitude>='"+la1+"' and l.latitude<='"+la2+"' "
					+ "and l.longitude>='"+lo1+"' and l.longitude<='"+lo2+"'");
			// print all info to the user
			rs = query.executeQuery();
			if(rs.next()){
				return Float.parseFloat((rs.getString("avg(c.price_per_day)")));
				
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
	
	public static ArrayList<String> suggestAmenities(float latitude, float longitude) throws Exception {
		float la1 = latitude-0.01f;
		float la2 = latitude+0.01f;
		float lo1 = longitude-0.01f;
		float lo2 = longitude+0.01f;
		
		List<String> sugAmenities = new ArrayList<String>();
		
		Connection conn = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			query = conn.prepareStatement("select a.amenity "
					+ "from listings l, amenities a "
					+ "where l.listid=a.listid and l.latitude>='"+la1+"' and l.latitude<='"+la2+"' "
							+ "and l.longitude>='"+lo1+"' and l.longitude<='"+lo2+"'");
			// print all info to the user
			rs = query.executeQuery();
			while(rs.next()){
				String a = rs.getString("a.amenity");
				if (!sugAmenities.contains(a)) {
					sugAmenities.add(a);
				}
				
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			rs.close();
			query.close();
			conn.close();
		}
		return (ArrayList<String>) sugAmenities;
	}

}
