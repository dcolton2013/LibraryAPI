package models;
import config.dbconfig;
import config.dbinit;
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
	
	//currently logged in
	private static String currentUser = "";
	
	//authority levels
	//0: admin, managers
	//1: associates
	//2: members
	//else: nonmembers
	private static int authorityLevel = 3;
	  
	//create db connection  
	public Library() throws ClassNotFoundException, SQLException{
	    Class.forName("com.mysql.jdbc.Driver");
	    conn = DriverManager.getConnection(url, user, password);
	    stmt = conn.createStatement();
	    System.out.println("Database connected successfully");
	    dbinit.createDB(stmt);
	    printBooks();
	}
	
	//functions to add tuples to the db
	public static void createManager(String uname,String password) throws SQLException{
		if (authorityLevel != 0) return;
		
		System.out.println("adding manager: " + uname);
		String sql = 	"insert ignore into managers values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	public static void createAssociate(String uname,String password) throws SQLException{
		if (authorityLevel != 0) return;
		System.out.println("adding associate: " + uname );
		String sql = 	"insert ignore into associates values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	
	public static void createBook(String isbn, String[] authors,String name, String year,int avail, double price, String[] keywords  ) throws SQLException{
		if (authorityLevel != 0) return;
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
		if (authorityLevel != 0) return;
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
		removeBookISBN(getISBN(name));
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
	    		currentUser = uname;
	    		authorityLevel = 0;
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
	    		currentUser = uname;
	    		authorityLevel = 1;
	    		Manager.handleMain();
			}
	}
	
	//logout functions
		//manager
	
	public static void logoutManager() throws SQLException {
		String sql = "update managers "+
					 "set loggedIn = 0 "+
					 "where username = '"+ currentUser +"'";
		stmt.executeUpdate(sql);
	}
	
	//Search Functions
		//get ISBN by name
	
	//Queries
	private static String getISBN(String value) throws SQLException{
		String sql = 	"select distinct isbn "+
						"from books "+
						"where name LIKE '%"+value+"%'";
		rs = stmt.executeQuery(sql);
		rs.next();
		return rs.getString(1);				
	}
	
	
	//Display info
	private static void printBooks() throws SQLException{
		String sql =	"select distinct b.isbn, b.name,b.year, a.author,b.availableCopies "+
					 	"from books b, books_authors a "+
						"inner join books_authors "+
					 	"where b.isbn = a.isbn";
		rs = stmt.executeQuery(sql);
		
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.printf("|%-15s|%-33s|%-5s|%-22s\n","ISBN","Title (available)","Year","Author(s)");
		System.out.println("----------------------------------------------------------------------------------------");
		
		String previsbn =null;
		while (rs.next()){
			if (rs.getString(1).equals(previsbn)){
				System.out.print(", "+ rs.getString(4));
				continue;
			}else if (previsbn != null){
				System.out.println("");
			}
			previsbn = rs.getString(1);
			
			System.out.printf("|%-15s|%-30s%3s|%-5s|"	,rs.getString(1)
	   													,rs.getString(2).substring(0, Math.min(rs.getString(2).length(), 27))
	   													,"("+rs.getString(5)+")"
	   													,rs.getString(3));
			System.out.print(rs.getString(4));
		}
		System.out.println("\n----------------------------------------------------------------------------------------");
	}				 	
}
