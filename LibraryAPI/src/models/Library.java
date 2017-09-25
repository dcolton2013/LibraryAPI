package models;
import config.dbconfig;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library{
	//db configuration*******************
	private static String url= dbconfig.URL;  
	private static String user = dbconfig.USER;
	private static String password= dbconfig.PASSWORD;
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
	
	//create tables
	private static void createDB() throws SQLException{
	    try {
	        stmt.executeUpdate("create schema if not exists Library;");
	    } catch (SQLException ex) {
	    }
		createManagersTable();
    	createAssociatesTable();
	//    	createMembersTable();
    	createBooksTable();
    	createBookAuthorsTable();
    	createBookKeywordsTable();
    	
    	System.out.println(getISBN("books_authors","Radric "));
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
							 "price				double				not null, "+
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
		String bookKeywords = 	"create table if not exists books_authors( "+
								"isbn		varchar(15)		not null, 		"+
								"author		varchar(25)				,		"+
								"primary key(isbn,author))";
		stmt.executeUpdate(bookKeywords);
		loadBookAuthors();
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
	
	private static void loadBookKeywords() throws SQLException {
		System.out.println("populating table: books_keywords...");
		File f = new File("src/config/books_keywords");
		String sql = "LOAD DATA LOCAL INFILE '"+f.getAbsolutePath()+".txt' "+
					 "INTO TABLE books_keywords "+
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}

	private static void loadBooks() throws SQLException {
		System.out.println("populating table: books...");
		File f = new File("src/config/books");
		String sql = "LOAD DATA LOCAL INFILE '"+f.getAbsolutePath()+".txt' "+
					 "INTO TABLE books "+ 
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}
	
	private static void loadBookAuthors() throws SQLException {
		System.out.println("populating table: books_authors...");
		File f = new File("src/config/books_authors");
		String sql = "LOAD DATA LOCAL INFILE '"+f.getAbsolutePath()+".txt' "+
					 "INTO TABLE books_authors "+ 
					 "COLUMNS TERMINATED BY ',' "+
					 "LINES STARTING BY '.'";
		stmt.executeUpdate(sql);
	}
	
	public static void createBook(String isbn, String[] authors,String name, String year,int avail, double price, String[] keywords  ) throws SQLException{
		if (authors.length == 0)
			return;
		String sql = 	"insert ignore into books values( "+
						"'"+isbn	+"', "+ 
						"'"+name	+"', "+
						"'"+year	+"', "+
						"'"+avail	+"', "+ 
						"0				,"+
						price			  + " )";
		stmt.executeUpdate(sql);
		
		for (String a: authors){
			sql = 	"insert ignore into books_authors values( "+
					"'"+isbn	+"', "+ 
					"'"+a	+"')";
			stmt.executeUpdate(sql);
		}
		
		if (keywords.length > 0){
			for (String k: keywords){
				sql = 	"insert ignore into books_keywords values( "+
						"'"+isbn	+"', "+ 
						"'"+k		+"')";
				stmt.executeUpdate(sql);
			}
		}
	}
	
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
	public static void removeBookNAME(String name){
		
	}
		//remove by author
	public static void removeBookAUTHOR(String name){
		
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
	    		Manager.handleMain();
			}
	}
	
		//associate
	public static void loginAssociate(String uname, String password) throws SQLException{
	String sql =  	"SELECT a.username, a.password " +
	          		"FROM associates a " +
	          		"WHERE m.username = '"+uname+"' AND m.password = '"+password+"'"; 
	
		ResultSet rs = stmt.executeQuery(sql);
		if (!rs.next())
			//empty result
			System.out.println("\t"+ uname + " not authenticated");
		else{
			System.out.println("\t"+uname+" authentication successful");
		    sql = 	"update associates "+
					"set loggedIn = 1 " +
					"where username = '"+uname+"'";
	    		stmt.executeUpdate(sql);
	    		manager = uname;
	    		Manager.handleMain();
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
	private static String getISBN(String table,String value) throws SQLException{
		String sql=null;
		if (table.equals("books_authors")){
			sql = 	"select distinct isbn "+
					"from "+table+" "+
					"where author LIKE '%"+value+"%'";
		}else{
			sql = 	"select distinct isbn "+
					"from "+table+" "+
					"where name LIKE '%"+value+"%'";
		}
		
		rs = stmt.executeQuery(sql);
		rs.next();
		return rs.getString(1);				
	}
	
}
