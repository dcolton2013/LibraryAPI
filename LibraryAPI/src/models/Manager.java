package models;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class Manager{
	static Scanner scan = new Scanner(System.in);
	static Statement stmt;
	
	public static void showManagerMenu() throws SQLException{
	        System.out.println("*****************************");
	        System.out.println("1. Create new manager");
	        System.out.println("2. Create new associate");
	        System.out.println("3. Create new member");
	        System.out.println("4. Remove manager");
	        System.out.println("5. Remove associate");
	        System.out.println("6. Remove member");
	        System.out.println("7. Inventory options");
	        System.out.println("8. Logout");
	        System.out.println("\t-1 return to main menu");
	        System.out.println("*****************************");
	        System.out.print("\tSelection: ");
	}
	
	public static void showInventoryMenu() throws SQLException{
        System.out.println("*****************************");
        System.out.println("1. Create new book");
        System.out.println("2. Remove book");
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
				case 3:
						break;
				case 4:
						break;
				case 5:
						break;
				case 6:
						break;
					
				case 7:	handleInventory();
						break;
				
				case 8: Library.logoutManager();
						selection = -1;
						break;
						
				case -1:break;
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
        	case 1:	createBook("0011","pablo,shamboni", "blah", "1996", 30, 19.66,"mystery");
        			break;
        	case 2: 
        			break;
        	default:System.out.println("invalid selection"); 
        			selection = -1;
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
		System.out.println("adding manager: " + uname);
		String sql = 	"insert ignore into managers values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	public static void createAssociate(String uname,String password) throws SQLException{
		System.out.println("adding associate: " + uname );
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
		if (isbn.length() < 10) return;
		if (authors.length() == 0) return;
		
		String sql = 	"insert ignore into books values( "+
						"'"+isbn	+"', "+ 
						"'"+name	+"', "+
						"'"+year	+"', "+
						"'"+avail	+"', "+ 
						"0				,"+
						price			  + " )";
		stmt.executeUpdate(sql);
		
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
		String sql = "delete from books "+
					 "where ISBN LIKE %'"+isbn+"'%";
		stmt.executeUpdate(sql);
		
		//remove from books_keywords
		sql = "delete from books_keywords "+
			  "where ISBN LIKE %'"+isbn+"'%";
		stmt.executeUpdate(sql);
		
		//remove from books_authors
		sql = "delete from books_keywords "+
			  "where ISBN LIKE %'"+isbn+"'%";
		stmt.executeUpdate(sql);
	}

		//remove by name
	
	public static void removeBookNAME(String name) throws SQLException{
		removeBookISBN(Library.getISBN(name));
	}
}