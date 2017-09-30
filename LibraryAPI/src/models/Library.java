package models;
import config.dbconfig;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library{

	//db configuration*******************
	private static String url= dbconfig.URL;;    
	private static String user = dbconfig.USER;;
	private static String password= dbconfig.PASSWORD;;
    //***********************************

	private static Connection conn;
	private static Statement stmt;
	private static ResultSet rs;
	private static String query;
	
	//currently logged in
	private static String manager = ""; 
	private static String associate = "";
	private static String member = "";
	  
	//create db connection  
	public Library() throws ClassNotFoundException, SQLException{
	    Class.forName("com.mysql.jdbc.Driver");
	    conn = DriverManager.getConnection(url, user, password);
	    stmt = conn.createStatement();
	    System.out.println("Database connected successfully");
	    createDB();
	}
	
	//initialize db/create tables
	//*******************************************
	private static void createDB() throws SQLException{
	    try {
	        stmt.executeUpdate("create schema if not exists Library;");
	    } catch (SQLException ex) {
	    }
		createManagersTable();
    	createAssociatesTable();
	//    	createMembersTable();
    	createBooksTable();
    	//createBookKeywordsTable();
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
							 "author			varchar(20)		not null, "+
							 "name				varchar(250)	not null, "+
							 "year				varchar(4)		not null, "+
							 "availableCopies	int				not null, "+
							 "holds				int				not null, "+
							 "price				double				not null, "+
				 			 "primary key(isbn,author))";
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
	//********************************************
	//functions to add tuples to the db
	//loggedIn initially set to false
	public static void createManager(String uname,String password) throws SQLException{
		System.out.println("adding manager: " + uname + " to managers");
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
	
	private static void loadBookKeywords() throws SQLException {
		System.out.println("populating table: books_keywords...");
		File f = new File("src/config/books_keywords");
		String s = f.getAbsolutePath();
		//put system here
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			System.out.println("windows");
			s = s.replaceAll("/", "\\");
		}
		String sql = "LOAD DATA LOCAL INFILE '"+s+ ".txt' "+
					 "INTO TABLE books_keywords "+
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}

	private static void loadBooks() throws SQLException {
		System.out.println("populating table: books...");
		File f = new File("src/config/books");
		String s = f.getAbsolutePath();
		System.out.println(f.getAbsolutePath());
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			System.out.println("windows");
			s = s.replace("\\", "//");
			System.out.println(s);
		}
			String sql = "LOAD DATA LOCAL INFILE '"+s+".txt' "+
					 	 "INTO TABLE books "+ 
						 "COLUMNS TERMINATED BY ',' "+
						 "LINES STARTING BY '.'";
		
			stmt.executeUpdate(sql);
		
	}

	//login functions
	//manager
	public static void loginManager(String uname, String password) throws SQLException{
	String sql =  	"SELECT m.username, m.password " +
	          		"FROM managers m " +
	          		"WHERE m.username = '"+uname+"' AND m.password = '"+password+"'"; 
	
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			//empty result
			System.out.println("\t"+ uname + " not authenticated");
		else{
			System.out.println("\t"+uname+" authentication successful");
		    sql = 	"update managers "+
					"set loggedIn = 1 " +
					"where username = '"+uname+"'";
	    		stmt.executeUpdate(sql);
	    		manager = uname;
	    		Manager.start();
			}
		
	}
	
	//logout functions
	//manager
	public static void logoutManager() throws SQLException {
		String sql = "update managers "+
					"set loggedIn = 0 "+
					"where username = '"+ manager +"'";
		stmt.executeUpdate(sql);
	}
	
	
	//Search Functions
}
