package Database;

public class tables {
	
	// assumption: 
	// 1. price is counted by the number of days staying; type of price is int;
	// 2. if a host want to rent then he needs to create a new account for renting;
	// 3. to log in the user need to provide the username and password
	// 4. whenever a booking is successful, system will return a bookingid to the user for future reference
	// or in other words, the id is the only way to find that booking.
	// 5. postal code is stored in the format of 6-digits.
	
	
	
	
	// schemas:
	
	// mysql -u root -p
	// show databases
	
	
	
	
	
	// calenders(.cid, listid, start_date, end_date, price_per_day);
	// listings(.listid, name, hostid, room_type, country, city, latitude, longitude, 
	// address, postal_code, room, amenity);
	// users(.uid, name, password, user_type, date_of_birth, address, occupation, SIN_no, payment_info);
	// bookings(.bid, cid, renter_id, check_in_date, check_out_date, is_cancelled,
	// ratingOnHost, ratingOnRenter, commentOnHost, commentOnRenter, status);
	
	// amenities(aid, listid, amenity);
	// message(mid, userid, msg);
	

}
