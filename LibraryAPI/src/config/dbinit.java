package config;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import models.*;
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
	public static void createDB(Statement s){
	    stmt = s;
		try {
	        stmt.executeUpdate("create schema if not exists Library;");
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
	    }
		createManagersTable();
	    	createAssociatesTable();
	    	createMembersTable();
	    	createBooksTable();
	    	createBookAuthorsTable();
	    	createBookKeywordsTable();
	    	createMemberHoldsTable();
	    	createMemberCheckoutsTable();
	    	createMemberReturnsTable();
	}
	
	private static void createManagersTable() {
		System.out.println("Creating Table: managers...");
	    //init table
	    String managersTable =  "create table if not exists managers (" +
	    					 	"username	varchar(15)		not null,"+
	    					 	"password	varchar(15)		not null,"+
	    					 	"loggedIn	boolean			default 0,"+
	    					 	"primary key(username));";
	    try {
	        stmt.executeUpdate(managersTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.getMessage());
	    }
	    loadManagers();
	}
	
	private static void createAssociatesTable() {
		System.out.println("Creating Table: associates...");
	    //init table
	    String associatesTable = "create table if not exists associates (" +
	    					 	 "username	varchar(15)		not null,"+
	    					 	 "password	varchar(15)		not null,"+
	    					 	 "loggedIn	boolean			default 0,"+
	    					 	 "primary key(username));";
	    try {
	        stmt.executeUpdate(associatesTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.toString());
	    }
	    loadAssociates();
	}
	
	private static void createMembersTable(){
		System.out.println("Creating Table: members...");
	    //init table
	    String membersTable =  "create table if not exists members (" +
	    					 	"fname						varchar(15)		not null,"+
	    					 	"lname						varchar(15)		not null,"+
	    					 	"address						varchar(50)		not null,"+
	    					 	"phone						varchar(10)		not null,"+
	    					 	"username					varchar(15)		not null,"+
	    					 	"password					varchar(15)		not null,"+
	    					 	"code						varchar(4)		not null,"+
	    					 	"numBooksCheckedOut			int				default 0,"+
	    					 	"suspended					boolean			default 0,"+
	    					 	"loggedIn					boolean			default 0,"+
	    					 	"minPayment					numeric(10,2)	default 0,"+
	    					 	"primary key(username));";
	    try {
	        stmt.executeUpdate(membersTable);
	    } catch (SQLException ex) {
	        System.out.println(ex.toString());
	    }
	    addMemberConstraints();
	    loadMembers();
	}
	
	private static void createBooksTable() {
		System.out.println("Creating Table: books...");
		String booksTable =  "create table if not exists books( "+
							 "isbn				varchar(15)		not null, "+
							 "name				varchar(250)	not null, "+
							 "year				varchar(4)		not null, "+
							 "totalCopies		int				not null, "+
							 "availableCopies	int				not null, "+
							 "holds				int				not null		default 0, "+
							 "price				double			not null, "+
							 "newrelease		boolean				not null		default 0, "+
				 			 "primary key(isbn))";
		try{
			stmt.executeUpdate(booksTable);
			loadBooks();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	private static void createBookKeywordsTable() {
		System.out.println("Creating Table: books_keywords...");
		String bookKeywords = 	"create table if not exists books_keywords( "+
								"isbn		varchar(15)		not null, 		"+
								"keyword	varchar(25)				,		"+
								"primary key(isbn,keyword)			,		"+
								"foreign key(isbn) references books(isbn))";
		try {
			stmt.executeUpdate(bookKeywords);
			loadBookKeywords();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
				
	}
	
	private static void createBookAuthorsTable(){
		System.out.println("Creating Table: books_authors...");
		String bookAuthors = 	"create table if not exists books_authors( 	"+
								"isbn		varchar(15)		not null, 		"+
								"author		varchar(25)				,		"+
								"primary key(isbn,author)			, 		"+
								"foreign key(isbn) references books(isbn))";
		try {
			stmt.executeUpdate(bookAuthors);
			loadBookAuthors();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void createMemberHoldsTable(){
		System.out.println("Creating table: members_holds...");
		String userHolds = 	"create table if not exists members_holds( 	"+
							"code			varchar(4)		not null,	"+
							"isbn			varchar(15)		not null,	"+ 
							"holdpos		int,						"+
							"holdexpiration	DATETIME,					"+ 
							"primary key(code,isbn),					"+
							"foreign key(code) references members(code),"+
							"foreign key(isbn) references books(isbn))	";
		try {
			stmt.executeUpdate(userHolds);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void createMemberCheckoutsTable(){
		//uname isbn fees status(returned,checkedout,late,lost) checkoutdate/time returndate/time
		//sql insert ex. 1,NOW(),DATE_ADD(NOW(), INTERVAL 2 WEEK)
		System.out.println("Creating table: members_checkouts...");
		String member_checkouts = "create table if not exists members_checkouts( "+
								  "code 			varchar(4)  		not null	,"+
								  "isbn			varchar(15)  	not null	,"+
								  "status 		varchar(15)	 	not null	,"+
								  "checkoutdate	DATETIME			not null	 	default NOW(),"+
								  "returndate	DATETIME			not null		,"+
								  "renewals 		int							default 0,"+
								  "latefees 		numeric(10,2)				default 0,"+
								  "bookfees 		numeric(10,2)				default 0,"+
								  "primary key(code, isbn)					,"+
								  "foreign key(code) references members(code),"+
								  "foreign key(isbn) references books(isbn))";
		try {
			stmt.executeUpdate(member_checkouts);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		loadMemberCheckouts();
	}
	
	private static void createMemberReturnsTable(){
		System.out.println("Creating table: members_returns...");
		String member_returns = "create table if not exists members_returns( "+
								  "code 	varchar(4)		not null,"+
								  "isbn		varchar(15)		not null,"+
								  "primary key(code,isbn) 			,"+
								  "foreign key (code) references members(code),"+
								  "foreign key (isbn) references books(isbn))";
		try {
			stmt.executeUpdate(member_returns);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void addMemberConstraints(){
		try{
			String sql = "ALTER TABLE members "+
					 	 "ADD CONSTRAINT code UNIQUE (code)";
		    stmt.executeUpdate(sql);
		}catch(Exception e){
			//System.out.println(e.getMessage());
		}
	}
	
	//load prepopulated data
	private static void loadBookKeywords() {
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
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void loadManagers() {
		System.out.println("populating table: managers...");
		File f = new File("src/config/managers");
		String path =f.getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			path = path.replace("\\", "/");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+path+".txt' "+
					 "INTO TABLE managers "+
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void loadAssociates() {
		System.out.println("populating table: associates...");
		File f = new File("src/config/associates");
		String path =f.getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			path = path.replace("\\", "/");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+path+".txt' "+
					 "INTO TABLE associates "+
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void loadBooks(){
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
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void loadMembers(){
		System.out.println("populating table: members...");
		Associate.addMember("Lebron", "James", "6022 Cleveland Ave", "5678453456", "ljames");
		Associate.addMember("Sammy", "Sharpsburg", "1101 Savory Street", "8882223333", "ssharpsburg");
		Associate.addMember("Kobe", "Bryant", "477 East Langford Drive", "5554321987", "kbryant");
		Associate.addMember("Sasha", "Smitherson", "1212 Twelfth Street", "2291212122", "ssmitherson");
		Associate.addMember("Sheldon", "Bloobob", "333 Mars Way", "4567239900", "sbloobob");
	}
	
	private static void loadBookAuthors(){
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
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void loadMemberCheckouts() {
		System.out.println("populating table:member_checkouts...");
		//Associate.scanOutBook("3330", "9781501175565");
	}
}
