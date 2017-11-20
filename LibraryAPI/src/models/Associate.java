package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
//may need to import book.java
import java.util.*;

public class Associate{

	private static Scanner scan = new Scanner(System.in);
	static Statement stmt,stmt2,stmt3;
	static Connection conn;
	static ResultSet rs;
	
	public static void scanInBook(String memberUsername, String bookISBN){

	    String memberQuery = "select * from members m where m.username = '" + memberUsername + "' limit 1;";

	    String bookQuery = "select * from books b where b.isbn = '" + bookISBN + "' limit 1;";

	    String updateBookQuery = "UPDATE books b SET b.availableCopies = (b.availableCopies + 1) WHERE b.isbn= '"+ bookISBN+ "' ;" ;

	    String updateMemberQuery = "UPDATE members m SET m.numBooksCheckedOut = (m.numBooksCheckedOut - 1) WHERE m.username= '"+ memberUsername+ "' ;" ;



	    try {

	        stmt = conn.createStatement();

	        stmt2 = conn.createStatement();

	       

	        ResultSet rs = stmt.executeQuery(memberQuery);

	       

	        int remainingRentals =0;

	       

	        while (rs.next()) {

	            String memberName = rs.getString("fname") + " " + rs.getString("lname");

	            System.out.printf("%s has been successfully retrieved.%n", memberName);

	        }

	       

	        rs = stmt2.executeQuery(bookQuery);

	        int copies=0;

	        while (rs.next()) {

	            String bookName = rs.getString("name");

	            copies = rs.getInt("availableCopies");

	            System.out.printf("%s has been successfully scanned in.%n", bookName);

	        }

	        int response = stmt.executeUpdate(updateBookQuery);



	          if(response == 1){

	System.out.println("Book copies available: " + (copies+1));

	          }

	           

	           

	          int responseMember = stmt2.executeUpdate(updateMemberQuery);

	          String updateMemberCheckinQuery = "UPDATE members_checkouts m SET m.status = 'checked in' WHERE m.isbn= '"+ bookISBN+ "' ;" ;  

	 	      int memCheckins = stmt2.executeUpdate(updateMemberCheckinQuery);

	       

	 	      if(memCheckins == 1) {

	 	   	  System.out.println("checkin success alert.");

	 	      }

	           

	          rs = stmt.executeQuery(memberQuery);

	          while (rs.next()) {

	            int booksCheckedOut = rs.getInt("numBooksCheckedOut");

	            System.out.printf("You have %d reamaining rentals.\n", 10-booksCheckedOut);

	        }

	          if(responseMember==1) {

	       	  System.out.println("Okay thanks alot.");

	       	  System.out.println("Come again.");

	          }

	           

	        stmt.close();

	        stmt2.close();

	       

	    } catch (SQLException e ) {

	        e.printStackTrace();

	    } 

	   

	}



	@SuppressWarnings("resource")

	public static void scanOutBook(String member, String bookISBN){

	String memberQuery = "select * from members m where m.username = '" + member + "' limit 1;";

	String bookQuery = "select * from books b where b.isbn = '" + bookISBN + "' limit 1;";

	String updateBookQuery = "UPDATE books b SET b.availableCopies = (b.availableCopies - 1) WHERE b.isbn= '"+ bookISBN+ "' ;" ;

	String updateMemberQuery = "UPDATE members m SET m.numBooksCheckedOut = (m.numBooksCheckedOut + 1) WHERE m.username= '"+ member+ "' ;" ;

	 

	    try {

	        stmt = conn.createStatement();

	        stmt2 = conn.createStatement();

	        stmt3 = conn.createStatement();

	        ResultSet rs = stmt.executeQuery(memberQuery);

	       

	        String code;

	        while (rs.next()) {

	            String memberName = rs.getString("fname") + " " + rs.getString("lname");

	            int booksCheckedOut = rs.getInt("numBooksCheckedOut");

	            boolean isSuspended = rs.getBoolean("suspended");

	            int remainingRentals = (10 - booksCheckedOut);

	            code=rs.getString("code");

	           

	            if(booksCheckedOut > 9 || isSuspended) {

	            System.out.printf("Unfortunately this member has reached maxed checkouts or is suspended.\n");

	            System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);

	            System.out.println("|--------------------------------------------|");

	            System.out.println(" Will list books the member has checked out. ");

	            System.out.println("|--------------------------------------------|");

	            }



	            else {

	                rs = stmt2.executeQuery(bookQuery); //book query

	            while (rs.next()) {

	                String bookName = rs.getString("name");

	                int availableCopies = rs.getInt("availableCopies");

	   	           

	                if(availableCopies > 0) {

	                System.out.printf("%s has been successfully scanned out to %s.%n",bookName, memberName);

	                System.out.println("----------------------------------");

	                System.out.printf("%S - Membership Summary %n",memberName);

	   	           

	 	          int response = stmt.executeUpdate(updateBookQuery);

	 	          int copies=rs.getInt("availableCopies"); // new value --

	           

	 	          if(response == 1){

	System.out.printf("Copies of [%S] available: %d", bookName,copies); // a successful U-query

	 	          }

	           

	 	          int response2 = stmt3.executeUpdate(updateMemberQuery); //update members checkouts



	 	        rs = stmt.executeQuery(memberQuery);

	 	        while (rs.next()) {

	 	            remainingRentals = (10 - rs.getInt("numBooksCheckedOut"));

	 	        }

	 	        System.out.println();

	 	          if(response2==1) {

	 	       	      System.out.printf("\nNumber of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut+1);

	            System.out.printf("Remaining rentals: %d%n", remainingRentals); //update in db via stmt3 to avoid set closing

	 	          }

	           

	 	          //2008-11-11 13:23:44

	          SimpleDateFormat dateFormatter = new SimpleDateFormat("y-M-d hh:mm:s");
	 	        int noOfDays = 14; //i.e two weeks
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(new Date());            
	            calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
	            Date date = calendar.getTime();

	          String updateMemberCheckoutsQuery = String.format("INSERT INTO members_checkouts (code,isbn,status,checkoutdate, returndate, renewals, latefees, bookfees) VALUES ('%s','%s','%s','%s','%s','%s','%s', '%s');", code, bookISBN, "checked out", dateFormatter.format(new Date()), dateFormatter.format(date) , 0,0,0);  
	          boolean memCheckouts = stmt2.execute(updateMemberCheckoutsQuery);

	                }

	                else {

	                System.out.println(bookName+ " is out of stock. Sorry."); // its out of stock playa

	                }

	            }

	            }

	        }

	       

	        stmt.close(); stmt2.close();

	       

	    } catch (SQLException e ) {

	        e.printStackTrace();

	    } 

	}


	
	public static int addMember(String fname, String lname,
							 	String addr, String phone,
							 	String username){
		String password = Member.generatePassword();
		String code = Member.generateLibrarycode();
		return addMember(fname,lname,addr,phone,username,password,code);
	}
	
	public static int addMember(String fname, String lname,
		 						String addr, String phone,
		 						String username,String password,
		 						String code){
		String sql = "insert into members values(?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,fname);
			pstmt.setString(2,lname);
			pstmt.setString(3,addr);
			pstmt.setString(4,phone);
			pstmt.setString(5,username);
			pstmt.setString(6,password);
			pstmt.setString(7,code);
			pstmt.setInt(8,0);
			pstmt.setBoolean(9,false);
			pstmt.setBoolean(10,false);
			pstmt.setDouble(11,0);
			pstmt.execute();
			System.out.println("\tuser added.");
			System.out.println("\tcredentials: ");
			System.out.println("\tusername: " + username);
			System.out.println("\tpassword: " + password);
			System.out.println("\tlibrary code: " + code);
		} catch (SQLException e) {
			String s = e.getLocalizedMessage();
			System.out.println(s);
			if (s.contains("uname") || s.contains("PRIMARY")){
				//duplicate username
				System.out.println("duplicate username");
				return 1;
			}else if (s.contains("code")){
				//duplicate library code;
				code = Member.generateLibrarycode();
				addMember(fname,lname,addr,phone,username,password,code);
			}
		}
		return 0;
	}

	public static void renewBook(String isbn, String code){
		int returncode = getRenewals(isbn,code);
		//num of renewals on book
		if (returncode >= 2){ 
			System.out.println("renewal limit for "+isbn+" reached");
			return;
		}
		
		//user doesnt have book checked out
		if (returncode < 0) {
			System.out.println(code+" doesnt have this book checked out");
			return;
		}
		
		if (Library.getNumHolds(isbn) != 0){
			System.out.println("active holds, renew not available");
			return;
		}
		
		if (Library.checkNewRelease(isbn)){
			System.out.println("new release, renew not available");
			return;
		}
		
		String sql = "update members_checkouts "+
					 "set status = 'renewed', checkoutdate = NOW(), returndate = DATE_ADD(NOW(),INTERVAL 2 WEEK), renewals = renewals + 1 "+
					 "where (isbn = ? and code = ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, isbn);
			pstmt.setString(2, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}	
	}
	
	//get amount of renewals for a given isbn and library code
	public static int getRenewals(String isbn, String code){
		String sql =  "select renewals "+
					  "from members_checkouts "+
					  "where (isbn = ? and code = ?)";
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, isbn);
			ps.setString(2, code);
			rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return -1;
	}
}
