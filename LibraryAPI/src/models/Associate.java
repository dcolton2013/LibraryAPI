package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//may need to import book.java
import java.util.*;

public class Associate{

	private String password;
	private String username;
	private String name;
	private int id;
	
	private static Scanner scan = new Scanner(System.in);
	
	static Statement stmt,stmt2,stmt3;


	static Connection conn;
	static ResultSet rs;
	/*getters/setters of data field*/
	/* Validate book, update availability*/
	public static void scanInBook(String memberUsername, String bookISBN){
	    String memberQuery = "select * from members m where m.username = '" + memberUsername + "' limit 1;";
	    String bookQuery = "select * from books b where b.isbn = '" + bookISBN + "' limit 1;";
	    String updateBookQuery = "UPDATE books b SET b.availableCopies = (b.availableCopies + 1) WHERE b.isbn= "+ bookISBN+ " ;" ;
	  //  String updateMemberQuery = "UPDATE members m SET m.numBooksCheckedOut = (m.numBooksCheckedOut - 1) WHERE m.username= '"+ memberUsername+ "' ;" ;	
	    try {
	        stmt = conn.createStatement();
	        stmt2 = conn.createStatement();
	        
	        ResultSet rs = stmt.executeQuery(memberQuery);
	
	        while (rs.next()) {
	            String memberName = rs.getString("fname") + " " + rs.getString("lname");
	        //    int booksCheckedOut = rs.getInt("numBooksCheckedOut");
	            System.out.printf("%s has been successfully retrieved.%n", memberName);
	        //    System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);
	        }
	    //    int responseMember = stmt2.executeUpdate(updateMemberQuery);
	        
	        rs = stmt2.executeQuery(bookQuery);
	        int copies=0;
	        while (rs.next()) {
	            String bookName = rs.getString("name");
	            copies = rs.getInt("availableCopies");
	            System.out.printf("%s has been successfully scanned in.%n", bookName);
		     //   updateBookInfo(bs, "in");
	        }
	        int response = stmt.executeUpdate(updateBookQuery);

	           if(response == 1){
					System.out.println("Book copies available: " + (copies+1));
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
		 String updateBookQuery = "UPDATE books b SET b.availableCopies = (b.availableCopies - 1) WHERE b.isbn= "+ bookISBN+ " ;" ;
		 String updateMemberQuery = "UPDATE members m SET m.numBooksCheckedOut = (m.numBooksCheckedOut + 1) WHERE m.username= "+ member+ " ;" ;
		 
		//	book = stmt.executeQuery(updateBookQuery);
	    try {
	        stmt = conn.createStatement();
	        stmt2 = conn.createStatement();
	        stmt3 = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(memberQuery);
	        
	        while (rs.next()) {
	            String memberName = rs.getString("fname") + " " + rs.getString("lname");
	            int booksCheckedOut = rs.getInt("numBooksCheckedOut");
	            boolean isSuspended = rs.getBoolean("suspended");
	            if(booksCheckedOut > 9 || isSuspended) {
	            		System.out.printf("Unfortunately this member has reached maxed checkouts or is suspended.\n");
	            		System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);
	            		System.out.println("|--------------------------------------------|");
	            		System.out.printf(" Will list books the member has checked out. ");
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
		    	            		System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);
			 	            System.out.printf("Remaining rentals: %d%n", (10-booksCheckedOut)); //update in db via stmt3 to avoid set closing
			 	            
			 	           int response = stmt.executeUpdate(updateBookQuery);
			 	           
			 	           stmt3.executeUpdate(updateMemberQuery); //update members checkouts
			 	           
			 	           int copies=rs.getInt("availableCopies"); // new value --
			 	           
			 	           if(response == 1){
								System.out.println("Book copies available: " + (copies-1)); // a successful U-query
			 	           }
		    	            }
		    	            else {
		    	            		System.out.println(bookName+ " is out of stock. Sorry."); // its out of stock playa
		    	            }
		    	        }
	            }
	            /*Can see a members book list if they are maxed out. 
	             * Might as well allow them to be visible regardless of 1 or 0.*/
	            //updateBookInfo(rs, "out");
	        }
	        
	        stmt.close(); stmt2.close();
	        
	    } catch (SQLException e ) {
	        e.printStackTrace();
	    } 
	}
	

	public static void showAssociateMenu() throws SQLException{
        System.out.println("*****************************");
        System.out.println("1. Create new member");
        System.out.println("2. Check out book");
        System.out.println("3. Return books");
        System.out.println("4. Logout");
        System.out.println("*****************************");
        System.out.print("\tSelection: ");
	}
	
	//handles Associate menu options
	public static void handleMain() throws SQLException {
		int selection = 0;
    	showAssociateMenu();
    	while (true){
			selection = scan.nextInt();
			scan.nextLine();
			switch(selection){
				case 1:	promptUserInfo();
						break;
				case 2: promptScanOut();
						break;
				case 3: promptScanIn(); 
						break;
				case 4:	selection = -1;
						break;
				default: System.out.println("invalid selection");
			}
			if (selection == -1) break;
				showAssociateMenu();
    	}
		
	}
	
	public static void promptScanIn() {
		System.out.print("What's the member's username? ");
		String memberUName = scan.nextLine();
		System.out.println();
		System.out.print("Enter the book's isbn#: ");
		String isbnBeingReturned = scan.nextLine();
		System.out.println();
		scanInBook(memberUName, isbnBeingReturned);
	}
	
	public static void promptScanOut() {
		System.out.print("Enter the book's ISBN# :  ");
		String bookISBN = scan.nextLine();
		System.out.println();
		System.out.print("Enter the member username: ");
		String memberCheckingOut = scan.nextLine();
		System.out.println();
		scanOutBook(memberCheckingOut, bookISBN);
	}
	
	public static void promptUserInfo() throws SQLException{
		System.out.print("\tFirst Name:");
		String fname = scan.nextLine();
		
		System.out.print("\tLast Name:");
		String lname = scan.nextLine();
		
		System.out.print("\tAddress:");
		String addr = scan.nextLine();

		System.out.print("\tPhone:");
		String phone = scan.nextLine();
		
		System.out.print("\tUsername:");
		String username = scan.nextLine();
		System.out.println();
		
		String code = Member.generateLibrarycode();
		int returncode = addMember(fname,lname,addr,phone,username);
		
		while(returncode != 0){
			if (returncode == 1){
				//duplicate username
				System.out.println("uname in use");
				System.out.println("Username:");
				username = scan.nextLine();
			}
			if (returncode == 2){
				//duplicatecode
				code = Member.generateLibrarycode();
			}
			returncode = addMember(fname,lname,addr,phone,username);
		}	
		System.out.println("\tuser added.");
		System.out.println("\tCredentials: ");
		System.out.println("\tUsername: " + username);
		System.out.println("\tLibrary Code: " + code);
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
