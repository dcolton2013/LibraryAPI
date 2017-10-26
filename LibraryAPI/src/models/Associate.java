package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//may need to import book.java
import java.util.*;

public class Associate{
	static Statement stmt;
	static Statement stmt2;
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
