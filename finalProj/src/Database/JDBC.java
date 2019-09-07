package Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC {

	public static void main(String[] args) throws Exception {
		// initialize database
//		initialization();

		
		// main
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Welcome!\n"
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit MyBnB;\n"
				+ "a. Perform a regular operation as user;\n"
				+ "b. Perfrom a report opertaion as admin;\n");
		op = br.readLine();
		// do op
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				performUserOpertion();
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "b":
				performReportOperations();
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			default:
				System.out.println("Invalid operation! Please try again!");
				op = br.readLine();
				break;
			}
		}
	}
		
	// 
	public static void performUserOpertion() throws Exception {
		int userid;
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("q. Exit to upper;\n"
				+ "a. Create / delete account;\n"
				+ "b. Login to a hosting account;\n"
				+ "c. Login to a renting account;\n"
				+ "d. Search for listings;\n");
		op = br.readLine();
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				System.out.println("Create / delete account...");
				performAccountOperations();
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "b":
				System.out.println("Login to a hosting account...");
				// login for the user
				userid = Users.userLogin(0);
				if (userid == 0) {
					System.out.println("Login failed, wrong user name or password please check!");
					System.out.println("Perform another or press 'q' to quit.");
					op = br.readLine();
					break;
				} else {
					System.out.println("Login successful!");
					
					// check message for the user after login
					Messages.printAllRelatedMessages(userid);
					performHostAccountLevelOperations(userid);
					
					System.out.println("Perform another or press 'q' to quit.");
					op = br.readLine();
					break;
				}
				
			case "c":
				System.out.println("Login to a rentering account...");
				
				// login for the user
				userid = Users.userLogin(1);
				if (userid == 0) {
					System.out.println("Login failed, wrong user name or password please check!");
					break;
				} else {
					System.out.println("Login successful!");
					// check message for the user after login
					Messages.printAllRelatedMessages(userid);
					performRenterAccountLevelOperations(userid);
					System.out.println("Perform another or press 'q' to quit.");
					
					op = br.readLine();
					break;
				}
				
			case "d":
				System.out.println("Search for listings...");
				performSearchQuery();
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			default:
				System.out.println("Invalid operation! Please try again!");
				op = br.readLine();
				break;
			}
		}
		System.out.println("Welcome! "
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit MyBnB;\n"
				+ "a. Perform a regular operation as user;\n"
				+ "b. Perfrom a report opertaion as admin;\n");
	}

	// 
	public static void performAccountOperations() throws Exception {
		int uid;
		
		// ask for op
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Welcome to MyBnB!\n"
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit to upper;\n"
				+ "a. Create a host user acount;\n"
				+ "b. Create a renter user accout;\n"
				+ "c. Delete a host user account;\n"
				+ "d. Delete a renter user account;\n");
		op = br.readLine();
		// do op
		while(! op.equalsIgnoreCase("q")) {
			int user_type;
			switch(op) {
			case "a":
				System.out.println("Create a host user acount...");
				
				user_type = 0;
				
				if (Users.createNewUser(user_type) == 0) {
					// failure
					System.out.println("Creation failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			case "b":
				System.out.println("Create a renter user accout...");
				
				user_type = 1;
				
				if (Users.createNewUser(user_type) == 0) {
					// failure
					System.out.println("Creation failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			case "c":
				System.out.println("Delete a host user account...");
				
				user_type = 0;
				
				uid = Users.userLogin(user_type);
				
				if (Users.removeAUser(uid) == 0) {
					// failure
					System.out.println("Deletion failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
			
				op = br.readLine();
				break;
				
			case "d":
				System.out.println("Delete a renting user account");
				
				user_type = 1;
				
				uid = Users.userLogin(user_type);
				
				if (Users.removeAUser(uid) == 0) {
					// failure
					System.out.println("Deletion failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
			
				op = br.readLine();
				break;
				
			default:
				System.out.println("Invalid operation! Please try again!");
				op = br.readLine();
				break;
			}
		}
		System.out.println("q. Exit to upper;\n"
				+ "a. Create / delete account;\n"
				+ "b. Login to a hosting account;\n"
				+ "c. Login to a renting account;\n"
				+ "d. Search for listings;\n");
	}

	// perform operation from user: userid
	public static void performHostAccountLevelOperations(int userid) throws Exception {
		String comment;
		int touserid, bookingid, listid, cid, rating;
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Welcome to your hosting account!\n"
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit to upper;\n"
				+ "a. Create a new listing;\n"
				+ "b. Remove a listing;\n"
				+ "c. Add a calendar to a listing;\n"
				+ "d. Remove a calendar from a listing;\n"
				+ "e. Cancell a booking;\n"
				+ "f. update price of a calendar;\n"
				+ "g. change availability of a listing;\n"
				+ "h. add rating to the renter of a booking;\n"
				+ "i. add comment to the renter of a booking;\n");
		op = br.readLine();
		// do op
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				float sp;
				
				System.out.println("Create a new listing...");
				float latitude;
				System.out.println("Enter listing latitude (e.g. = '43.651')");
				latitude = Float.parseFloat(br.readLine());
				
				float longitude;
				System.out.println("Enter listing longitude (e.g. = '79.383')");
				longitude = Float.parseFloat(br.readLine());
				
				listid = Listings.createNewListing(userid, latitude, longitude);
				System.out.print(listid);
				if (listid == 0) {
					// failure
					System.out.println("Create new listing failed, check your info!");
				} else {
					System.out.println("Please add the default availability calendars to your listing.");
					
					// suggest a price
					System.out.println("Suggesting a price for you...");
					try {
						sp = ToolKits.suggestPrice(latitude, longitude);
						System.out.println("\nSuggestion successful! Suggested price would be around: " + String.valueOf(sp));
					} catch (Exception e) {
						System.out.println("\nOops, too less listings around this area, suggestion failed!");
					}
					
					Calendars.createNewCalender(listid);
					
					System.out.println("Please add an amenity to your listing.");
					
					// suggest amenities
					System.out.println("Suggesting amenities for you...");
					ArrayList<String> sa = ToolKits.suggestAmenities(latitude, longitude);
					for (int i=0; i<sa.size(); i++) {
						System.out.println("amenity#" + String.valueOf(i+1) + " " + sa.get(i));
					}
					
					System.out.println("\nEnter the amenity for your listing.");
					String content = br.readLine();
					Amenities.insertAnAmenity(listid, content);
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				
			case "b":
				System.out.println("Remove a listing...");
				
				// print all listings associated with the user
				Listings.showAllListingsAssociatedWithAHost(userid);
				System.out.println("\nEnter the ID of the listing that you want to remove.");
				listid = Integer.parseInt(br.readLine());

				if (Listings.removeAListing(listid, userid) == 0) {
					// failure
					System.out.println("Remove listing failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			case "c":
				System.out.println("Add a calendar...");
				
				// print all listings associated with the user
				Listings.showAllListingsAssociatedWithAHost(userid);
				System.out.println("\n\nEnter the ID of the listing that you want to add a calendar to.");
				listid = Integer.parseInt(br.readLine());
				
				if (Calendars.createNewCalender(listid) == 0) {
					// failure
					System.out.println("Add new calendar failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			case "d":
				System.out.println("Remove a calendar...");
				
				// print all listings associated with the user
				Listings.showAllListingsAssociatedWithAHost(userid);
				System.out.println("\nEnter the ID of the listing that you want to remove a calendar from.");
				listid = Integer.parseInt(br.readLine());
				
				// print all calendars associated with the listing
				Calendars.showAllAssociatedCalendarsOfAListing(listid);
				System.out.println("\nEnter the ID of the calendar that you want to remove.");
				cid = Integer.parseInt(br.readLine());
				
				if (Calendars.removeACalendar(cid) == 0) {
					// failure
					System.out.println("Remove old calendar failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				
				op = br.readLine();
				break;
				
			case "e":
				System.out.println("Cancell a booking...");
				
				// show all bookings associated with that user
				Bookings.showAllBookingsAssociatedWithAHost(userid);
				
				// cancel specified booking
				System.out.println("\nEnter the ID of the booking that you want to cancel.");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findRenterIdOfABooking(bookingid);

				if (Bookings.cancellABooking(bookingid, userid, touserid) == 0) {
					// failure
					System.out.println("Cancellation failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("\nPerform another or press 'q' to quit\n");
			
				op = br.readLine();
				break;
				
			case "f":
				System.out.println("Update price of a calendar...");
				// show all calendars associated with that host
				Calendars.showAllAssociatedCalendarsOfAHost(userid);
				
				// change the specified calendar to specified price
				System.out.println("\nEnter the calendar ID (not booked) that you want to modify price.");
				cid = Integer.parseInt(br.readLine());
				System.out.println("\nEnter the new price that you want to update.");
				int newPrice = Integer.parseInt(br.readLine());
				
				if (Calendars.changePrice(cid, newPrice) == 0) {
					// failure
					System.out.println("Price modification failed, please enter a not booked calendar!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "g":
				System.out.println("Change availability of a listing...");
				
				// show all listings associated with the host
				System.out.println("Showing all listings associated with your account...");
				Listings.showAllListingsAssociatedWithAHost(userid);
				System.out.println("\nEnter the listing ID that you want to change availability.");
				listid = Integer.parseInt(br.readLine());
				
				// show all calendars associated with the listing
				System.out.println("Showing all calendars associated with the listing...");
				Calendars.showAllAssociatedCalendarsOfAListing(listid);
				System.out.println("\nEnter the calendar ID that you want to change availability.");
				cid = Integer.parseInt(br.readLine());
				
				// change the availability of the specified calendar if not booked
				System.out.println("\nEnter the new start date available");
				int start_date = Integer.parseInt(br.readLine());
				System.out.println("\nEnter the new end date available");
				int end_date = Integer.parseInt(br.readLine());
				
				if (Calendars.changeAvailbility(cid, start_date, end_date) == 0) {
					// failure
					System.out.println("Availability modification failed, please enter a not booked calendar!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "h":
				System.out.println("Add rating to the renter of a booking...");
				
				// show all bookings associated with account
				Bookings.showAllBookingsAssociatedWithAHost(userid);
				System.out.println("\nEnter the booking id that you want to rate on: ");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findRenterIdOfABooking(bookingid);
				
				// ask for comment content
				System.out.println("\nEnter the rating from 1, 2, 3, 4, 5");
				rating = Integer.parseInt(br.readLine());

				if (Bookings.insertARatingOnRenter(userid, touserid, bookingid, rating) == 0) {
					// failure
					System.out.println("Add rating failed, please enter a finished booking!");
				} else {
					System.out.println("Success!");
					
					// insert message into the message table
					String msg = "You got a new rating from host: " + String.valueOf(userid) 
								+ " on booking: " + String.valueOf(bookingid) + "\n";
					Messages.insertAMessage(touserid, msg);
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "i":
				System.out.println("Add comment to the renter of a booking...");
				
				// show all bookings associated with account
				Bookings.showAllBookingsAssociatedWithAHost(userid);
				System.out.println("\nEnter the booking id that you want to comment on: ");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findRenterIdOfABooking(bookingid);
				// ask for rating value
				System.out.println("\nEnter the comment: ");
				comment = br.readLine();
				
				if (Bookings.insertACommentOnRenter(userid, touserid, bookingid, comment) == 0) {
					// failure
					System.out.println("Add comment failed, please enter a finished booking!");
				} else {
					System.out.println("Success!");
					
					// insert message into the message table
					String msg = "You got a new comment from host: " + String.valueOf(userid) 
								+ " on booking: " + String.valueOf(bookingid) + "\n";
					Messages.insertAMessage(touserid, msg);
				}
				System.out.println("Perform another or press 'q' to quit.");
			
				op = br.readLine();
				break;
				
			default:
				System.out.println("Invalid operation! Please try again!");
				op = br.readLine();
				break;
			}
		}
		System.out.println("q. Exit to upper;\n"
				+ "a. Create / delete account;\n"
				+ "b. Login to a hosting account;\n"
				+ "c. Login to a renting account;\n");
	}
	
	//	
	public static void performRenterAccountLevelOperations(int userid) throws Exception {
		String comment;
		int touserid, bookingid, listid, cid, rating;
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Welcome to your renting account!\n"
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit to upper;\n"
				+ "a. Book a listing availble in the calendar\n"
				+ "b. Cancel a booking\n"
				+ "c. add a rating to the host of a booking\n"
				+ "d. add a comment to the host of a booking\n"
				+ "e. add a comment to the listing of a booking\n");
		op = br.readLine();
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				// show all calendars associated with the specified listing
				System.out.println("\nEnter the listing id that you want to check calendar on: ");
				listid = Integer.parseInt(br.readLine());
				System.out.println("Here's all calendars associated with the listing chosen...");
				Calendars.showAllAssociatedCalendarsOfAListing(listid);
				
				
				// show all bookings associated with that user
				System.out.println("\nHere's all your bookings...");
				Bookings.showAllBookingsAssociatedWithARenter(userid);
				
				// book the specified calendar if the date is suitable
				System.out.println("\nEnter the calendar id that you want to book on: ");
				cid = Integer.parseInt(br.readLine());
				if (Bookings.createABooking(cid, userid) == 0) {
					// failure
					System.out.println("Book listing failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "b":
				System.out.println("Cancell a booking...");
				
				// show all bookings associated with that user
				System.out.println("\nHere's all your bookings...");
				Bookings.showAllBookingsAssociatedWithARenter(userid);
				
				// cancel specified booking
				System.out.println("Enter the ID of the booking that you want to cancel.");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findRenterIdOfABooking(bookingid);

				if (Bookings.cancellABooking(bookingid, userid, touserid) == 0) {
					// failure
					System.out.println("Remove listing failed, check your info!");
				} else {
					System.out.println("Success!");
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "c":
				System.out.println("Add rating to the host of a booking...");
				
				// show all bookings associated with account
				Bookings.showAllBookingsAssociatedWithARenter(userid);
				System.out.println("Enter the booking id that you want to rate on: ");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findHostIdOfABooking(bookingid);
				
				System.out.print(touserid);
				
				// ask for comment content
				System.out.println("Enter the rating from 1, 2, 3, 4, 5");
				rating = Integer.parseInt(br.readLine());

				if (Bookings.insertARatingOnHost(userid, touserid, bookingid, rating) == 0) {
					// failure
					System.out.println("Add rating failed, please enter a finished booking!");
				} else {
					System.out.println("Success!");
					
					// insert message into the message table
					String msg = "You got a new rating from renter: " + String.valueOf(userid) 
								+ " on booking: " + String.valueOf(bookingid) + "\n";
					Messages.insertAMessage(touserid, msg);
					
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "d":
				System.out.println("Add comment to the renter of a booking...");
				
				// show all bookings associated with account
				Bookings.showAllBookingsAssociatedWithARenter(userid);
				System.out.println("Enter the booking id that you want to comment on: ");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findHostIdOfABooking(bookingid);
				// ask for rating value
				System.out.println("Enter the comment: ");
				comment = br.readLine();
				
				if (Bookings.insertACommentOnHost(userid, touserid, bookingid, comment) == 0) {
					// failure
					System.out.println("Add comment failed, please enter a finished booking!");
				} else {
					System.out.println("Success!");
					
					// insert message into the message table
					String msg = "You got a new comment from renter: " + String.valueOf(userid) 
								+ " on booking: " + String.valueOf(bookingid) + "\n";
					Messages.insertAMessage(touserid, msg);
			
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			case "e":
				System.out.println("Add comment to the listing of a booking...");
				
				// show all bookings associated with account
				Bookings.showAllBookingsAssociatedWithARenter(userid);
				System.out.println("Enter the booking id that you want to comment on: ");
				bookingid = Integer.parseInt(br.readLine());
				touserid = Bookings.findHostIdOfABooking(bookingid);
				
				// ask for rating value
				System.out.println("Enter the comment: ");
				comment = br.readLine();
				
				if (Bookings.insertACommentOnListing(userid, touserid, bookingid, comment) == 0) {
					// failure
					System.out.println("Add comment failed, please enter a finished booking!");
				} else {
					System.out.println("Success!");
					
					// insert message into the message table
					String msg = "You got a new comment from renter: " + String.valueOf(userid) 
								+ " on listing: " + String.valueOf(bookingid) + "\n";
					Messages.insertAMessage(touserid, msg);
				}
				System.out.println("Perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
		}
			System.out.println("q. Exit to upper;\n"
					+ "a. Create / delete account;\n"
					+ "b. Login to a hosting account;\n"
					+ "c. Login to a renting account;\n");
		}
	}
	
	public static void performReportOperations() throws Exception {
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Welcome back, admin! "
				+ "Choose the report operation that you want to perform!:\n"
				+ "q. Exit to upper;\n"
				+ "a. report number of bookings by city on specified date;\n"
				+ "b. report number of bookings by postal code in specified city;\n"
				+ "c. report number of listings by country and city;\n"
				+ "d. report number of listings by country, city and postal code;\n"
				+ "e. report host list ranked on their number of listings per country;\n"
				+ "f. report host list ranked on their number of listings per country and city;\n"
				+ "g. report potential commercial hosts by country and city;\n"
				+ "h. report renters by number of bookings within a specified date;\n"
				+ "i. report renters by number of bookings within a specified date grouped by city;\n"
				+ "j. report hosts with the largest number of cancellation within a specified year;\n"
				+ "k. report word clouds for listings;\n");
		op = br.readLine();
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				int startDate;
				int endDate;
				System.out.println("Enter the startDate to report on: \n");
				startDate = Integer.parseInt(br.readLine());
				System.out.println("Enter the endDate to report on: \n");
				endDate = Integer.parseInt(br.readLine());
				Report.reportNumOfBookingsByCity(startDate, endDate);
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "b":
				int startDate1;
				int endDate1;
				String city;
				System.out.println("Enter the startDate to report on: \n");
				startDate1 = br.read();
				System.out.println("Enter the endDate to report on: \n");
				endDate1 = br.read();
				System.out.println("Enter the city to report on: \n");
				city = br.readLine();
				Report.reportNumOfBookingsByPostalCodeInCity(startDate1,endDate1,city);
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "c":
				Report.reportNumOfListingsByCountryCity();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "d":
				Report.reportNumOfListingsByCountryCityPostalCode();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "e":
				Report.reportRankListOnHostWithTheirListingsPerCountry();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "f":
				Report.reportRankListOnHostWithTheirListingsPerCountryCity();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "g":
				Report.reportPotentialCommercialHostsByCountryCity();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "h":
				int startDate2;
				int endDate2;
				System.out.println("Enter the start date to report on: \n");
				startDate2 = Integer.parseInt(br.readLine());
				System.out.println("Enter the end date to report on: \n");
				endDate2 = Integer.parseInt(br.readLine());
				Report.reportRentersByNumOfBookingsByPeriod(startDate2, endDate2);
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "i":
				int startDate3;
				int endDate3;
				System.out.println("Enter the start date to report on: \n");
				startDate3 = Integer.parseInt(br.readLine());
				System.out.println("Enter the end date to report on: \n");
				endDate3 = Integer.parseInt(br.readLine());
				Report.reportRentersByNumOfBookingsByPeriodCity(startDate3, endDate3);
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "j":
				String year;
				System.out.println("Enter the year to report on: \n");
				year = br.readLine();
				Report.reportHostsWithLargestNumOfCancellationWithinYear(year);
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
			case "k":
				Report.rentersWordCloudOnListings();
				System.out.println("Success! perform another or press 'q' to quit.");
				op = br.readLine();
				break;
				
			default:
				System.out.println("Invalid report! Please try again!");
				op = br.readLine();
				break;
			}
		}
		System.out.println("Welcome! "
				+ "Choose the operation that you want to perform!:\n"
				+ "q. Exit MyBnB;\n"
				+ "a. Perform a regular operation as user;\n"
				+ "b. Perfrom a report opertaion as admin;\n");
	}
	
	public static void performSearchQuery() throws Exception{
		String answer, address, choice;
		float latitude, longitude;
		int postalCode, similarity;
		SearchQuery s;
		// ask for inputs
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String op;
		System.out.println("Search for your listings!\n"
				+ "Choose the operation that you want to perform!:"
				+ "q. Exit to upper;\n"
				+ "a. Search by longitude and latitude\n"
				+ "b. Search by postal code\n"
				+ "c. Search by address\n"
				+ "d. add a comment to the host of a booking\n");
		op = br.readLine();
		while(! op.equalsIgnoreCase("q")) {
			switch(op) {
			case "a":
				// show all calendars associated with the specified listing
				System.out.println("Enter a longitude");
				longitude = Float.parseFloat(br.readLine());
				System.out.print(longitude);
				System.out.println("Enter a latitude" );
				latitude = Float.parseFloat(br.readLine());
				System.out.println(latitude);
				System.out.println("Would you like to set a distance value?(y/n)" );
				answer = br.readLine();
				s = null;
				float distanceVal = 0;
				
				if (answer.equals("n")) {
					distanceVal = 10;
					s = SearchQuery.searchByLocation2(longitude, latitude, distanceVal);
				}
				
				else if(answer.equals("y")){
					
					System.out.println("Enter a distance value" );
					distanceVal = Float.parseFloat(br.readLine());
					System.out.println(distanceVal);
					s = SearchQuery.searchByLocation2(longitude, latitude, distanceVal);
					
				}
				// ask user for filter
				System.out.println("Would you like to add a filter? (y/q)");
				answer = br.readLine();
				// keep adding filters if the user says yes
				
				while (answer.equals("y")) {
					System.out.println("Search for your listings!\n"
							+ "Choose the operation that you want to perform!:"
							+ "q. Exit to upper;\n"
							+ "a. filter by date range\n"
							+ "b. filter by price range\n"
							+ "c. filter by amenities\n");
					
					choice = br.readLine();
					s = PerformFilterForSearch(s,choice);
					System.out.println(s.getSqlScript());
					// check if the user wants to filter again
					System.out.println("Would you like to add a filter again? (y/q)");
					answer = br.readLine();
				}
				// ask whether 
				System.out.println("Would you like to rank by ... ? (y/n)");
				answer = br.readLine();
				// rank by price
				if (answer.equals("y")) {
					//
					System.out.println("Rank by ...\n"
							+ "Choose the operation that you want to perform!:"
							+ "a. Rank by price\n"
							+ "b. Rank by distance \n");
					
					choice = br.readLine();
					if (choice.equals("a")){
						s = PerformRankingForSearch(s, choice);
					}
					else {
						
						System.out.println("type a for ascending order, d for descending order");
						String option = br.readLine();
						// type a asc or desc
						s.rankByDistance(option, distanceVal, longitude, latitude);
					}
				}
				
				SearchQuery.executeQuery(s);
				
				System.out.println("Would like to perform another search (y/q)");
				op = br.readLine();
				break;
				
			case "b":
				System.out.println("Enter a postal code");
				postalCode = Integer.parseInt(br.readLine());
				System.out.println("Enter a similarity level(number 0-6, 0 for no similarity required and 6 for completely identical postal code)");
				similarity = Integer.parseInt(br.readLine());
				s = SearchQuery.searchByPostalCode2(postalCode, similarity);
				System.out.println("first make postal search "+ s.getSqlScript());
				System.out.println("Would you like to add a filter? (y/n)");
				answer = br.readLine();
				// keep adding filters if the user says yes
				
				while (answer.equals("y")) {
					System.out.println("Search for your listings!\n"
							+ "Choose the operation that you want to perform!:"
							+ "q. Exit to upper;\n"
							+ "a. filter by date range\n"
							+ "b. filter by price range\n"
							+ "c. filter by amenities\n"
							+ "d. filter by a window of availability\n");
					
					choice = br.readLine();
					s = PerformFilterForSearch(s,choice);
					System.out.println("2" + s.getSqlScript());
					// check if the user wants to filter again
					System.out.println("Would you like to add a filter again? (y/n)");
					answer = br.readLine();
				}
				System.out.println("Would you like to rank by ... ? (y/n)");
				answer = br.readLine();
				// rank by price
				if (answer.equals("y")) {
					//
					System.out.println("Rank by ...\n"
							+ "Choose the operation that you want to perform!:"
							+ "a. Rank by price\n"
							+ "b. Rank by distance \n");
					
					choice = br.readLine();
					s = PerformRankingForSearch(s, choice);
				}
				
				SearchQuery.executeQuery(s);
				
				System.out.println("Would like to perform another search (y/p)");
				op = br.readLine();
				break;

			case "c":
				// show all bookings associated with account
				System.out.println("Enter an address");
				address = br.readLine();
				s = SearchQuery.searchByAddress2(address);
				System.out.println("Would you like to add a filter? (y/n)");
				answer = br.readLine();
				// keep adding filters if the user says yes
				
				while (answer.equals("y")) {
					System.out.println("Search for your listings!\n"
							+ "Choose the operation that you want to perform!:"
							+ "q. Exit to upper;\n"
							+ "a. filter by date range\n"
							+ "b. filter by price range\n"
							+ "c. filter by amenities\n");
					
					choice = br.readLine();
					s = PerformFilterForSearch(s,choice);
					System.out.println(s.getSqlScript());
					// check if the user wants to filter again
					
					System.out.println("Would you like to add a filter again? (y/n)");
					answer = br.readLine();
				}
				
				System.out.println("Would you like to rank by ... ? (y/n)");
				answer = br.readLine();
				// rank by price
				if (answer.equals("y")) {
					//
					System.out.println("Rank by ...\n"
							+ "Choose the operation that you want to perform!:"
							+ "a. Rank by price\n"
							+ "b. Rank by distance \n");
					
					choice = br.readLine();
					s = PerformRankingForSearch(s, choice);
				}
				SearchQuery.executeQuery(s);
				System.out.println("Would like to perform another search (y/p)");
				op = br.readLine();
				break;
				
			default:
				System.out.println("Invalid operation! Please try again!");
				op = br.readLine();
				break;
			}
			
		}
		
		System.out.println("Search for your listings!\n"
				+ "Choose the operation that you want to perform!:"
				+ "q. Exit to upper;\n"
				+ "a. Search by longitude and latitude\n"
				+ "b. Search by address\n"
				+ "c. Search by postal code\n"
				+ "d. add a comment to the host of a booking\n");
	}
	
	public static SearchQuery PerformFilterForSearch(SearchQuery s, String choice) throws Exception {
		System.out.println(s.getSqlScript());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// user wants to filter by date range
		if (choice.equals("a")) {
			System.out.println("enter the start date...");
			int startDate = Integer.parseInt(br.readLine());
			System.out.println("enter the end date...");
			int endDate = Integer.parseInt(br.readLine());
			return s.filterByDateRange(startDate, endDate);
		}
		else if (choice.equals("b")) {
			System.out.println("enter a lower bound price...");
			int lowerBound = Integer.parseInt(br.readLine());
			System.out.println("enter a upper bound price...");
			int upperBound = Integer.parseInt(br.readLine());
			return s.filterByPriceRange(lowerBound, upperBound);
			
			
		}
		else if (choice.equals("c")){
			s.filterByAmenities();
			System.out.println("enter an amenity");
			String amenity = br.readLine();
			s.filterByAmenities2(amenity);
			System.out.println("would you like to enter another amenity (y/n)");
			String answer = br.readLine();
			while (answer.equals("y")) {
				System.out.println("enter an amenity");
				amenity = br.readLine();
				s.filterByAmenities2(amenity);
				System.out.println("would you like to enter another amenity (y/n)");
				answer = br.readLine();
			}
			s = s.filterByAmenitiesFinalise();
			return s;
		}
		else {
			return s;
		}
	}
	
	public static SearchQuery PerformRankingForSearch(SearchQuery s, String choice) throws Exception {
		System.out.println(s.getSqlScript());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// rank by price
		if (choice.equals("a")) {
			
			// 
			System.out.println("type a for ascending order, d for descending order");
			String option = br.readLine();
			// type a asc or desc
			return s.rankByPrice(option);
			
		}
		else if (choice.equals("b")) {
			System.out.println("enter a lower bound price...");
			int lowerBound = Integer.parseInt(br.readLine());
			System.out.println("enter a upper bound price...");
			int upperBound = Integer.parseInt(br.readLine());
			return s.filterByPriceRange(lowerBound, upperBound);
			
			
		}
		else if (choice.equals("c")){
			s.filterByAmenities();
			System.out.println("enter an amenity");
			String amenity = br.readLine();
			s.filterByAmenities2(amenity);
			System.out.println("would you like to enter another amenity (y/n)");
			String answer = br.readLine();
			while (answer.equals("y")) {
				System.out.println("enter an amenity");
				amenity = br.readLine();
				s.filterByAmenities2(amenity);
				System.out.println("would you like to enter another amenity (y/n)");
				answer = br.readLine();
			}
			s = s.filterByAmenitiesFinalise();
			return s;
		}
		else {
			return s;
		}
	}
	
//	// TABLES AND OPERATIONS
	public static void initialization() throws Exception{
		System.out.println("Initializing...");
		Users.createUsersTable();
		Listings.createListingsTable();
		Amenities.createAmenitiesTable();
		Calendars.createCalendarsTable();
		Bookings.createBookingsTable();
		Messages.createMessagesTable();
		
		Users.insertAUser("host1", "pw1", 0, 19890101, "addr1", "professor", "000001", "1234123412341234");//1
		Users.insertAUser("renter1", "pw2", 1, 19760526, "addr2", "lawyer", "000002", "1234123412341234");//2
		Users.insertAUser("host2", "pw3", 0, 19950101, "addr3", "programmer", "000003", "1234123412341234");//3
		Users.insertAUser("renter2", "pw4", 1, 19990921, "addr4", "Student", "000004", "1234123412341234");//4
		Users.insertAUser("renter3", "pw5", 1, 00000101, "heaven", "god", "000005", "9999999999999999");//5
		
		// one user with multi listings
		Listings.insertAListing("l1", 1, "house", "Canada", "Toronto", 43.653f, 79.383f, "addr1", 123456, "n/a");//1
		Listings.insertAListing("l2", 1, "apartment", "American", "New York", 40.71f, 74.006f, "addr2", 123456, "802");//2
		Listings.insertAListing("l3", 1, "room", "China", "Beijing", 39.90f, 116.40f, "addr3", 129356, "n/a");//3
		// listing medium close to 1
		Listings.insertAListing("l4", 3, "house", "Canada", "Vancouver", 49.282f,123.120f, "addr4", 533456, "n/a");//4
		// listing super close to 1
		Listings.insertAListing("l5", 3, "apartment", "Canada", "Toronto", 43.853f, 79.923f, "addr5", 122236, "404");//5
		
		Amenities.insertAnAmenity(1, "a");//1
		Amenities.insertAnAmenity(1, "b");//2
		Amenities.insertAnAmenity(1, "c");//3
		
		// one listing with multiple calendars
		Calendars.insertACalendar(1, 20190101, 20190201, 85, 0);//1
		Calendars.insertACalendar(1, 20190301, 20190401, 90, 0);//2
		Calendars.insertACalendar(1, 20191001, 20191101, 95, 0);//3
		// diff listings with same period with other listing
		Calendars.insertACalendar(2, 20190101, 20190201, 135, 0);//4
		// listing with a super long period
		Calendars.insertACalendar(3, 20190101, 20200101, 155, 0);//5
		// listing with a super short period
		Calendars.insertACalendar(4, 20190101, 20190121, 99, 0);//6
		// booked calendar for list5
		Calendars.insertACalendar(5, 20190101, 20190111, 25, 0);//7
		Calendars.changeStatus(7, 1);
		Calendars.insertACalendar(5, 20190201, 20190311, 40, 0);//8
		Calendars.changeStatus(8, 1);
		Calendars.insertACalendar(5, 20191201, 20191224, 55, 0);//9
		Calendars.changeStatus(9, 1);
		
		// success ones
		// mid to mid
		Bookings.insertABooking(5, 2, 20190103, 20190107);//1
		// start to mid
		Bookings.insertABooking(5, 4, 20190201, 20190301);//2
		// mid to end
		Bookings.insertABooking(5, 5, 20191210, 20191224);//3
		// 
		Bookings.insertABooking(5, 5, 20190101, 20190103);//4
		
		Bookings.insertABooking(1, 5, 20190211, 20190213);//5
		Bookings.updateStatusOfABooking(5, 1);
		Bookings.insertABooking(1, 5, 20190215, 20190217);//6
		Bookings.updateStatusOfABooking(6, 2);
		
		Bookings.insertABooking(1, 2, 20190219, 20190221);//7
		Bookings.updateStatusOfABooking(7, 1);
		Bookings.insertABooking(1, 2, 20190213, 20190225);//8
		Bookings.updateStatusOfABooking(8, 2);
		
		Bookings.insertABooking(4, 4, 20190210, 20190301);//9
		Bookings.updateStatusOfABooking(9, 2);
		
		Bookings.insertABooking(4, 2, 20191001, 20191005);//10
		Bookings.updateStatusOfABooking(10, 2);
		
		System.out.println("Initializing completed!\n\n");
	}	


}