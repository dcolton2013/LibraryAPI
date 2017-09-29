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
	    Manager.stmt = stmt;
	    Associate.stmt = stmt;
	}
	
	//login
	
	//functions to add tuples to the db	
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
	
	
	//logout
	public static void logoutManager() throws SQLException {
		String sql = "update managers "+
					 "set loggedIn = 0 "+
					 "where username = '"+ currentUser +"'";
		stmt.executeUpdate(sql);
		authorityLevel = 3;
	}
	
	//Search Functions
		//get ISBN by name
	
	//Queries
	//getISBN given name
	
	//getISBN by name
	public static String getISBN(String value) throws SQLException{
		String sql = 	"select distinct isbn "+
						"from books "+
						"where name LIKE '%"+value+"%'";
		rs = stmt.executeQuery(sql);
		rs.next();
		return rs.getString(1);				
	}
	
		//searches

	//searches
	
	//dispay info
	//Display info
	private static void printBooks(String isbn) throws SQLException{
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
				System.out.println();
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
