package config;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
/*
 * this class creates the initial pre-populated db
 * data files can be found under src/config/
 * 			books.txt
 * 			books_keywords.txt
 * 			books_authors.txt
 */

public class dbinit {
	private static Statement stmt;
	//create tables
	public static void createDB(Statement s) throws SQLException{
	    stmt = s;
		try {
	        stmt.executeUpdate("create schema if not exists Library;");
	    } catch (SQLException ex) {
	    }
		createManagersTable();
    	createAssociatesTable();
    	//createMembersTable();
    	createBooksTable();
    	createBookAuthorsTable();
    	createBookKeywordsTable();
    	//createMemberHoldsTable();
    	//createMemberCheckoutsTable();
	}
	
	private static void createManagersTable() {
		System.out.println("Creating Table: managers...");
	    //init table
	    String managersTable = "create table if not exists managers (" +
	    					 	//"fname		varchar(15)		not null,"+
	    					 	//"lname		varchar(15)		not null,"+
	    					 	"username	varchar(15)		not null,"+
	    					 	"password	varchar(15)		not null,"+
	    					 	"loggedIn	boolean					,"+
	    					 	"primary key(username));";
	    try {
	        stmt.executeUpdate(managersTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.toString());
	    }
	}
	
	private static void createAssociatesTable() {
		System.out.println("Creating Table: associates...");
	    //init table
	    String associatesTable = "create table if not exists associates (" +
	    					 	//"fname		varchar(15)		not null,"+
	    					 	//"lname		varchar(15)		not null,"+
	    					 	"username	varchar(15)		not null,"+
	    					 	"password	varchar(15)		not null,"+
	    					 	"loggedIn	boolean					,"+
	    					 	"primary key(username));";
	    try {
	        stmt.executeUpdate(associatesTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.toString());
	    }
	}
	
	private static void createMembersTable() {
		System.out.println("Creating Table: members...");
	    //init table
	    String membersTable = "create table if not exists members (" +
	    					 	"fname						varchar(15)		not null,"+
	    					 	"lname						varchar(15)		not null,"+
	    					 	"address					varchar(50)		not null,"+
	    					 	"phone						varchar(10)		not null,"+
	    					 	"username					varchar(15)		not null,"+
	    					 	"password					varchar(15)		not null,"+
	    					 	"code						varchar(4)		not null,"+
	    					 	"numBooksCheckedOut			int						,"+
	    					 	"suspended					boolean					,"+
	    					 	"loggedIn					boolean					,"+
	    					 	"primary key(username));";
	    try {
	        stmt.executeUpdate(membersTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.toString());
	    }
	}
	
	private static void createBooksTable() throws SQLException {
		System.out.println("Creating Table: books...");
		String booksTable = "create table if not exists books( "+
							 "isbn				varchar(15)		not null, "+
							 "name				varchar(250)	not null, "+
							 "year				varchar(4)		not null, "+
							 "availableCopies	int				not null, "+
							 "holds				int				not null, "+
							 "price				double			not null, "+
				 			 "primary key(isbn))";
		stmt.executeUpdate(booksTable);
		loadBooks();
	}
	
	private static void createBookKeywordsTable() throws SQLException {
		System.out.println("Creating Table: books_keywords...");
		String bookKeywords = 	"create table if not exists books_keywords( "+
								"isbn		varchar(15)		not null, 		"+
								"keyword	varchar(25)				,		"+
								"primary key(isbn,keyword))";
		stmt.executeUpdate(bookKeywords);
		loadBookKeywords();
				
	}
	
	private static void createBookAuthorsTable() throws SQLException{
		System.out.println("Creating Table: books_authors...");
		String bookAuthors = 	"create table if not exists books_authors( "+
								"isbn		varchar(15)		not null, 		"+
								"author		varchar(25)				,		"+
								"primary key(isbn,author))";
		stmt.executeUpdate(bookAuthors);
		loadBookAuthors();
	}
	
	private static void createMemberHoldsTable() throws SQLException{
		System.out.println("Creating table: member_holds...");
		String userHolds = 	"create table if not exists member_holds( "+
							"username	varchar(15)		notnull,  "+
							"isbn		varchar(15)		not null, "+ 
							"holdpos	int						, "+ 
							"primary key(username))";
		stmt.executeUpdate(userHolds);
	}
	
	private static void createMemberCheckoutsTable() throws SQLException{
		//uname isbn fees status(returned,checkedout,late,lost) checkoutdate/time returndate/time
		//sql insert ex. 1,NOW(),DATE_ADD(NOW(), INTERVAL 2 WEEK)
		System.out.println("Creating table: member_checkouts...");
	}
	
	//load prepopulated data
	private static void loadBookKeywords() throws SQLException {
		System.out.println("populating table: books_keywords...");
		File f = new File("src/config/books_keywords");
		String path =f.getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			path = path.replace("\\", "/");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+path+".txt' "+
					 "INTO TABLE books_keywords "+
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}

	private static void loadBooks() throws SQLException {
		System.out.println("populating table: books...");
		File f = new File("src/config/books");
		String path = f.getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			path = path.replace("\\", "/");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+path+".txt' "+
					 "INTO TABLE books "+ 
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}
	
	private static void loadBookAuthors() throws SQLException {
		System.out.println("populating table: books_authors...");
		File f = new File("src/config/books_authors");
		String path = f.getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			path = path.replace("\\", "//");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+path+".txt' "+
					 "INTO TABLE books_authors "+ 
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}
}
