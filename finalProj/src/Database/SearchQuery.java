package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SearchQuery {
	
		// this constructor is for building queries and cooperates with filters
		private String sqlScript;


		public SearchQuery() {
			sqlScript = "";
		}
		
		// getter
		public String getSqlScript() {
		    return sqlScript;
		  }

		 // Setter
		public void setSqlScript(String newScript) {
		   this.sqlScript = newScript;
		}
		
		
		// this method executes all searchQueries
		public static void executeQuery(SearchQuery s) throws ClassNotFoundException, SQLException {
			
			String result = s.getSqlScript();
			result = String.format(result, " ", " ");
			System.out.println(result);
			s.setSqlScript(result);
			
			
			Connection conn = null;
			Statement query = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				System.out.println("Searching listings by the distance specified...");
				query = conn.createStatement();
				
				// query returns all listings within dist with respect to the specified location
				String sql = result;
				
				rs = query.executeQuery(sql);
				System.out.println(sql);
				SearchQuery.displayResultSet(rs);
				
				// print all listings to the user
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				conn.close();
				query.close();
				rs.close();
			}
		}
		
		

		public static void displayResultSet(ResultSet rs) throws SQLException {
				
				ResultSetMetaData rsmd = rs.getMetaData();
				int colNumber = rsmd.getColumnCount();
				
				String heading = "";
				for (int i = 1; i<= colNumber; i++) {
					
					//heading += rsmd.getColumnName(i) + "  ";
					System.out.print(String.format("|%-15s", rsmd.getColumnName(i)));
					
				}
				
				System.out.println(heading);
				while (rs.next()) {
					String msg = "";
					for (int i=1; i <= colNumber; i ++) {
						System.out.print(String.format("|%-15s", rs.getString(i)));
					}
					System.out.println("");
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
		
		// range is square
		public static float[] calculateRange(float longitude, float latitude, float distance) {
			float[] range = {longitude - distance , longitude + distance, latitude - distance, latitude + distance};
			
			return range;
		}
		
		
		public static SearchQuery searchByLocation2(float longitude, float latitude, float distanceVal) {
			SearchQuery search = new SearchQuery();
			float[] range = SearchQuery.calculateRange(longitude, latitude, distanceVal);
			
			// query returns all listings within dist with respect to the specified location
			String sql = "SELECT * FROM %1$s c43proj.listings "
					+ "WHERE %2$s longitude >= " + range[0] + " "
					+ "AND longitude <= " + range[1] + " "
					+ "AND latitude >= " + range[2] + " "
					+ "AND latitude <= " + + range[3] +  ";";
			
			search.setSqlScript(sql);
			
			return search;
			
		}
		
		
		public static SearchQuery searchByPostalCode2(int postalCode, int similarityLevel) throws ClassNotFoundException, SQLException {
			
			SearchQuery search = new SearchQuery();
			
			String postal_head = Integer.toString(postalCode);
			postal_head = postal_head.substring(0, similarityLevel) + "%%%%";
			System.out.println(postal_head);
			
			String sql = "SELECT * "
					+ "FROM %1s c43proj.listings "
					+ "WHERE %2s postal_code "
					+ "LIKE '" + postal_head  + "';";
			
			search.setSqlScript(sql);
			
			return search;
				
		}
		
		public static SearchQuery searchByAddress2(String address) throws ClassNotFoundException, SQLException {
			SearchQuery search = new SearchQuery();
			// query returns all listings within dist with respect to the specified location
			String sql = "SELECT * "
						+ "FROM %1$s c43proj.listings "
						+ "WHERE %2$s address = '" + address  + "';";
			
			search.setSqlScript(sql);
			
			return search;
				
		}
		
		public SearchQuery filterByDateRange(int startDate, int endDate) {
			String edition1;
			String edition2;
			String result = this.getSqlScript();
			
			// if it has been filterd by price range
			if (result.contains("c43proj.calendars")){
				edition1 = "%1s ";
				edition2 = "%2s start_date >= " + startDate + " "
						+ "AND end_date <= " + endDate + " "
						+ "AND status = 0 AND ";
			}
			else {
				edition1 = "%1s c43proj.calendars,";
				edition2 = "%2s listings.listid = calendars.listid "
						+ "AND start_date >= " + startDate + " "
						+ "AND end_date <= " + endDate + " "
						+ "AND status = 0 AND ";
				
			}
			
			
			result = String.format(result, edition1, edition2);
			System.out.println("inside filterBYdateRange" + result);
			
			System.out.println("inside filterBYdateRange2" + result);
			this.setSqlScript(result);
			
			return this;
		}
		
		//
		public SearchQuery filterByPriceRange(int lowerbound, int upperbound) {
			// check if the result already joins calendar
			String edition1;
			String edition2;
			String result = this.getSqlScript();
			// if it has been filtered by date range before
			if (result.contains("c43proj.calendars")){
				edition1 = "%1s ";
				edition2 = "%2s price_per_day >= " + lowerbound + " "
						+ "AND price_per_day <= " + upperbound + " AND ";
			}
			else {
				edition1 = "%1s c43proj.calendars,";
				edition2 = "%2s listings.listid = calendars.listid "
						+ "AND price_per_day >= " + lowerbound + " "
						+ "AND price_per_day <= " + upperbound + " AND ";
			}
			
			System.out.println("inside filterByPriceRange" + result);
			result = String.format(result, edition1, edition2);
			System.out.println("inside filterByPriceRange2" + result);
			this.setSqlScript(result);
			
			
			return this;
		}
		
		public SearchQuery filterByAmenities() {
			String edition1 = "%1s c43proj.amenities, ";
			String edition2 = "%2s amenities.listid = listings.listid AND ";
			String result = this.getSqlScript();
			System.out.println("inside filterBYAmenities" + result);
			result = String.format(result, edition1, edition2);
			result = result.replace("%%'", "%%%%'");
			System.out.println("inside filterBYAmenitiesafterformat" + result);
			this.setSqlScript(result);
			
			return this;
		}
		
		public SearchQuery filterByAmenities2(String amenity) {
			String edition1 = "%1s ";
			String edition2 = "%2s amenities.amenity = '" + amenity + "' OR";
			String result = this.getSqlScript();
			System.out.println("inside filterBYAmenities2" + result);
			result = String.format(result, edition1, edition2);
			result = result.replace("%%'", "%%%%'");
			System.out.println("inside filterBYAmenities2after format" + result);
			this.setSqlScript(result);
			
			return this;
		}
		
		public SearchQuery filterByAmenitiesFinalise() {
			String sqlScript = this.getSqlScript();
			String toReplace = "OR";
			String replacement = "AND";
			int start = sqlScript.lastIndexOf(toReplace);
			
			
			StringBuilder builder = new StringBuilder();
			builder.append(sqlScript.substring(0,start));
			builder.append(replacement);
			builder.append(sqlScript.substring(start + toReplace.length()));
			this.setSqlScript(builder.toString());

			return this;
		}
		
		
		public SearchQuery rankByPrice(String option) {
			String sqlScript = this.getSqlScript();
			String edition1 = "%1s";
			String edition2 = "%2s";
			// doesn't contain c43proj.calendars
			if (!(sqlScript.contains("c43proj.calendars"))){
				edition1 = "%1s c43proj.calendars, ";
				edition2 = "%2s calendars.listid = listings.listid AND "; 
			}
			sqlScript = String.format(sqlScript, edition1, edition2);
			
			if (option.equals("d")) {
				//replace ";" with "ORDER BY price_per_day DESC;"
				sqlScript = sqlScript.replaceFirst(";", "ORDER BY price_per_day DESC;");
			}
			else {
				sqlScript = sqlScript.replaceFirst(";", "ORDER BY price_per_day ASC;");
			}
			this.setSqlScript(sqlScript);
			return this;
		}
		
		public SearchQuery rankByDistance(String option, float distance, float longitude, float latitude) {
			String sqlScript = this.getSqlScript();
			// doesn't contain c43proj.calendars
			distance = distance*distance;
			
			String edition1 = "%1s ";
			String edition2 = "%2s (listings.latitude -"+ latitude + ") * (listings.latitude - " + latitude + ") + (listings.longitude -" + longitude +  ") * (listings.longitude -"+ longitude + ") <= " + distance + " AND "; 
			sqlScript = String.format(sqlScript, edition1, edition2);
			System.out.println(sqlScript);
			
			if (option.equals("d")) {
				//replace ";" with "ORDER BY price_per_day DESC;"
				sqlScript = sqlScript.replaceFirst(";", " ORDER BY (listings.latitude -"+ latitude + ") * (listings.latitude - " + latitude + ") + (listings.longitude -" + longitude +  ") * (listings.longitude -"+ longitude + ") <= " + distance + " DESC;");
			}
			else {
				sqlScript = sqlScript.replaceFirst(";", " ORDER BY (listings.latitude -"+ latitude + ") * (listings.latitude - " + latitude + ") + (listings.longitude -" + longitude +  ") * (listings.longitude -"+ longitude + ") ASC;");
			}
			this.setSqlScript(sqlScript);
			return this;
		}
		
		
}