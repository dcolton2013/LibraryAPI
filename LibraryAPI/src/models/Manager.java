package models;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;


public class Manager{
	static Scanner scan = new Scanner(System.in);
	static Statement stmt;
	static Connection conn;
	
	public static void showManagerMenu() throws SQLException{
	        System.out.println("*****************************");
	        System.out.println("1. Create new manager");
	        System.out.println("2. Create new associate");
	        System.out.println("3. Create new member");
	        System.out.println("4. Remove manager (NYI)");
	        System.out.println("5. Remove associate (NYI)");
	        System.out.println("6. Remove member (NYI)");
	        System.out.println("7. Suspend member (NYI)");
	        System.out.println("8. Inventory options");
	        System.out.println("9. Logout");
	        System.out.println("*****************************");
	        System.out.print("\tSelection: ");
	}
	public static void showInventoryMenu() throws SQLException{
        System.out.println("*****************************");
        System.out.println("1. Create new book");
        System.out.println("2. Remove book");
        System.out.println("3. Edit book (NYI)");
        System.out.println("\t-1 return to manager options");
        System.out.println("*****************************");
        System.out.print("\tSelection: ");
	}
	
	//handles manager menu
	public static void handleMain() throws SQLException {
		int selection = 0;
    	showManagerMenu();
    	while (true){
			selection = scan.nextInt();
			scan.nextLine();
			switch(selection){
				case 1: addManager();
						break;
				case 2: addAssociate();
						break;
				case 3: Associate.promptUserInfo();
						break;
				case 4:
						break;
				case 5:
						break;
				case 6:
						break;
				case 7:
						break;	
				case 8:	handleInventory();
						break;
				
				case 9: Library.logoutManager();
						selection = -1;
						break;
				default: System.out.println("invalid selection");
			}
			if (selection == -1) break;
			showManagerMenu();
    	}
	}
	
	//handles inventory menu
	public static void handleInventory() throws SQLException{
        showInventoryMenu();
        while(true){
        	int selection = scan.nextInt();
            scan.nextLine();
        	switch (selection){
        	case 1:	createBook("0011000000333","pablo,shamboni", "blah", "2009", 30, 19.66,"mystery");
        			break;
        	case 2: System.out.print("\tEnter ISBN: ");
        			String isbn = scan.nextLine();
        			removeBookISBN(isbn);
        			break;
        	case 3: 
    				break;
        	case -1: selection = -1;
    				break;
        	default:System.out.println("invalid selection"); 
        			break;
        	}
        	if (selection == -1) break;
        	showInventoryMenu();
        }
	}
	
	//prompt for info
	private static void addAssociate() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		createAssociate(username,password);
		
	}

	private static void addManager() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		createManager(username,password);
	}
	
	//functions to add tuples to the db
	public static void createManager(String uname,String password) throws SQLException{
		System.out.println("\tadding manager: " + uname);
		String sql = 	"insert ignore into managers values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	public static void createAssociate(String uname,String password) throws SQLException{
		System.out.println("\tadding associate: " + uname );
		String sql = 	"insert ignore into associates values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	//creaate books
	//accepts authors and keywords as a string
	//seperate each author/keyword with a comma
	public static void createBook(String isbn, String authors,String name, String year,int avail, double price, String keywords  ) throws SQLException{
		if (authors.length() == 0) return;
		
		if (Integer.parseInt(year) < 2007){
			if(isbn.length() != 10) return;
		}else{
			if (isbn.length() != 13) return;
		}
		String sql = "insert ignore into books values(?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,isbn);
		pstmt.setString(2,name);
		pstmt.setString(3,year);
		pstmt.setInt(4,avail);
		pstmt.setInt(5,0);
		pstmt.setDouble(6,price);
		
		pstmt.execute();
//		String sql = 	"insert ignore into books values( "+
//						"'"+isbn	+"', "+ 
//						"'"+name	+"', "+
//						"'"+year	+"', "+
//						"'"+avail	+"', "+ 
//						"0				,"+
//						price			  + " )";
//		stmt.executeUpdate(sql);
		
		for (String a: authors.split(",( |)")){
			sql = 	"insert ignore into books_authors values( "+
					"'"+isbn	+"', "+ 
					"'"+a	+"')";
			stmt.executeUpdate(sql);
		}
		
		if (keywords.length() == 0) return;
		
		for (String k: keywords.split(",( |)")){
			sql = 	"insert ignore into books_keywords values( "+
					"'"+isbn	+"', "+ 
					"'"+k		+"')";
			stmt.executeUpdate(sql);
		}
		
	}
	
	//remove books
	//Remove Functions
		//remove books by isbn
	public static void removeBookISBN(String isbn) throws SQLException{
		//remove from books table if isbn is of required length
		if(isbn.length() < 7) return;
		
		String title = Library.getTitle(isbn);
		
		String sql = "delete from books "+
					 "where ISBN LIKE '%"+isbn+"%'";
		stmt.executeUpdate(sql);
		
		//remove from books_keywords
		sql = "delete from books_keywords "+
			  "where ISBN LIKE '%"+isbn+"%'";
		stmt.executeUpdate(sql);
		
		//remove from books_authors
		sql = "delete from books_keywords "+
			  "where ISBN LIKE '%"+isbn+"%'";
		stmt.executeUpdate(sql);
		
		System.out.println("Book: "+isbn+" "+title+" removed");
	}

		//remove by name
	
	public static void removeBookNAME(String name) throws SQLException{
		removeBookISBN(Library.getISBN(name));
	}
}