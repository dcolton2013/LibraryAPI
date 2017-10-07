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
	
	static Statement stmt,stmt2;
	static Connection conn;

	//DS to keep up with Associate activity
	private ArrayList<String> activity;

	Associate(String name, int id){
		this.name=name;
		this.id=id;
		this.activity = new ArrayList<String>();
	}
	
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
	//	 String updateMemberQuery = "UPDATE members m SET m.numBooksCheckedOut = (m.numBooksCheckedOut + 1) WHERE m.username= "+ member+ " ;" ;	
		//	book = stmt.executeQuery(updateBookQuery);
	    try {
	        stmt = conn.createStatement();
	        stmt2 = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(memberQuery);
	        
	        while (rs.next()) {
	            String memberName = rs.getString("fname") + " " + rs.getString("lname");
	            int booksCheckedOut = rs.getInt("numBooksCheckedOut");
	            
	            if(booksCheckedOut >= 10) {
	            		System.out.printf("Unfortunately this member has reached maxed checkouts.\n");
	            		System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);
	            		System.out.println("|--------------------------------------------|");
	            		System.out.printf(" Will list books the member has checked out. ");
	            		System.out.println("|--------------------------------------------|");
	            }
	            else {
	            	   rs = stmt2.executeQuery(bookQuery); //book query
		    	        while (rs.next()) {
		    	            String bookName = rs.getString("name");

		    	            if(rs.getInt("availableCopies") >= 1) {
		    	            		System.out.printf("%s has been successfully scanned out to %s.%n",bookName, memberName);
		    	            		System.out.println("----------------------------------");
		    	            		System.out.printf("%S - Membership Summary %n",memberName);
		    	            		System.out.printf("Number of books checked out as of %s: %d%n", new Date().toString(), booksCheckedOut);
			 	            System.out.printf("Remaining rentals: %d%n", (10-booksCheckedOut)); //update in db via stmt3 to avoid set closing
			 	           int response = stmt.executeUpdate(updateBookQuery);
			 	           int copies=rs.getInt("availableCopies");
			 	           if(response == 1){
								System.out.println("Book copies available: " + (copies-1));
			 	           }
		    	            }
		    	            else {
		    	            		System.out.println(bookName+ " is out of stock. Sorry.");
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
	
	private static void updateBookInfo(ResultSet book, String method) {
		try {
			if(book.isClosed()) {
				System.out.println("closed");
			}
			else {
				System.out.println("opended");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String updateBookQuery;
		if(method.equalsIgnoreCase("out")) {
			try {
				System.out.println("testing:"+book.getInt("availableCopies"));
				updateBookQuery = "UPDATE books b SET b.availableCopies = " + (book.getInt("availableCopies")-1) + 
						" WHERE b.isbn= "+ book.getInt("isbn")+ " ;" ;
				
				book = stmt.executeQuery(updateBookQuery);
				while(book.next()){
					int copies=book.getInt("availableCopies");
					System.out.println("Book copies available: " + copies);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(method.equalsIgnoreCase("in")) {
			try {
				updateBookQuery = "UPDATE books b SET b.availableCopies = " + (book.getInt("availableCopies")+1) + 
						" WHERE b.isbn= '"+ book.getString("isbn")+ "' ;" ;
				
				stmt.executeUpdate(updateBookQuery);
				
				while(book.next()){
					int copies=book.getInt("availableCopies");
					System.out.println("Book copies available: " + copies);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		int returncode = addMember(fname,lname,addr,phone,username,code);
		
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
			returncode = addMember(fname,lname,addr,phone,username,code);
		}	
		System.out.println("\tuser added.");
		System.out.println("\tCredentials: ");
		System.out.println("\tUsername: " + username);
		System.out.println("\tLibrary Code: " + code);
	}
	
	public static int addMember(String fname, String lname,
							 	String addr, String phone,
							 	String username,String code) throws SQLException{
		
		String sql = "insert into members values(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,fname);
		pstmt.setString(2,lname);
		pstmt.setString(3,addr);
		pstmt.setString(4,phone);
		pstmt.setString(5,username);
		pstmt.setString(6,Member.generatePassword());
		pstmt.setString(7,Member.generateLibrarycode());
		pstmt.setInt(8,0);
		pstmt.setBoolean(9,false);
		pstmt.setBoolean(10,false);
		
		try {
			pstmt.execute();
		} catch (SQLException e) {
			String s = e.getLocalizedMessage();
			System.out.println(s);
			if (s.contains("uname") || s.contains("PRIMARY")){
				//duplicate username
				return 1;
			}else if (s.contains("code")){
				//duplicate code;
				return 2;
			}
		}
		return 0;
	}

}
