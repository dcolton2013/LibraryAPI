package models;
import config.dbconfig;
import config.dbinit;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library{

	//db configuration*******************
	private static String url= dbconfig.URL;  
	private static String user = dbconfig.USER;
	private static String password= dbconfig.PASSWORD;
    //***********************************
	public static Connection conn;
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
	public Library(){
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	    try {
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
		    System.out.println("Database connected successfully");
		    dbinit.createDB(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	    
	    Manager.stmt = stmt;
	    Associate.stmt = stmt;
	    Member.stmt = stmt;
	    
	    Manager.conn = conn;
	    Associate.conn = conn;
	    Member.conn = conn;
	}
	
	public static void loginManager(String uname, String password){
	String sql =  	"SELECT m.username, m.password " +
	          		"FROM managers m " +
	          		"WHERE m.username = '"+uname+"' AND m.password = '"+password+"'"; 
		try{
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
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
		
	public static void loginAssociate(String uname, String password){
	String sql =  	"SELECT a.username, a.password " +
	          		"FROM associates a " +
	          		"WHERE a.username = '"+uname+"' AND a.password = '"+password+"'"; 
		try{
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
		    		//Associate.handleMain();
				}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
		
	public static void loginMember(String uname, String password){
		String sql =  	"SELECT m.username, m.password " +
          				"FROM members m " +
          				"WHERE m.username = '"+uname+"' AND m.password = '"+password+"'"; 
		try{
			ResultSet rs = stmt.executeQuery(sql);
			if (!rs.next())
				//empty result
				System.out.println("\t"+ uname + " not authenticated");
			else{
				System.out.println("\t"+uname+" authentication successful");
			    sql = 	"update members "+
						"set loggedIn = 1 " +
						"where username = '"+uname+"'";
		    		stmt.executeUpdate(sql);
		    		currentUser = uname;
		    		authorityLevel = 2;
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}

	public static void logoutManager() {
		String sql = "update managers "+
					 "set loggedIn = 0 "+
					 "where username = '"+ currentUser +"'";
		try {
			stmt.executeUpdate(sql);
			System.out.println("\t"+currentUser + " succesfully logged out");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		authorityLevel = 3;
	}
	
	public static void logoutAssociate() {
		String sql = "update associates "+
					 "set loggedIn = 0 "+
					 "where username = '"+ currentUser +"'";
		try {
			stmt.executeUpdate(sql);
			System.out.println("\t" + currentUser + " succesfully logged out");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		authorityLevel = 3;
	}
	
	public static void logoutMember() {
		String sql = "update members "+
					 "set loggedIn = 0 "+
					 "where username = '"+ currentUser +"'";
		try {
			stmt.executeUpdate(sql);
			System.out.println("\t" + currentUser + " succesfully logged out");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		authorityLevel = 3;
	}
	
	//Queries
		//getISBN by name
	public static String getISBN(String value){
		String sql = 	"select distinct isbn "+
						"from books "+
						"where name LIKE '%"+value+"%'";
		try {
			rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getString(1);	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}			
	}
	
		//getTitle by ISBN
	public static String getTitle(String value){
		String sql = 	"select distinct name "+
						"from books "+
						"where isbn LIKE '%"+value+"%'";
		try {
			rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getString(1);	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		
	}
	
	public static Double getBookCost(String isbn){
		String sql = "select b.price "+
					 "from books b "+
					 "where b.isbn ="+isbn;
		try {
			rs = stmt.executeQuery(sql);
			rs.next();
			return Double.parseDouble(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	//Searches
		//by ISBN - search by isbn or partial isbn must be of length 6 or more
	public static String searchISBN(String isbn){
		if (isbn.length()<6) return "";
		
		String sql =	"select b.isbn, b.name "
					+ 	"from books b "
					+ 	"where b.isbn like '%"+isbn+"%'";
		try{
			rs = stmt.executeQuery(sql);
			
			if(!rs.next())
				return "no matches";
			else{
				String output = String.format("%-15s%-50s","isbn","title" );
				output += String.format("\n%-15s%-50s", rs.getString(1),rs.getString(2));
				while (rs.next()){
					output += String.format("\n%-15s%-50s", rs.getString(1),rs.getString(2));
				}
				output += "\n";
				return output;
			}
		}catch (SQLException e){
			return e.getMessage();
		}
	}
		
		//by author
	public static String searchAuthor(String authors){
		if (authors.length() < 3) return "";
		String output = "";
			
		for(String a: authors.split(",( |)")){
			String sql =	"select distinct b.isbn, b.name,a.author "
						+ 	"from books b, books_authors a "
						+	"inner join books_authors on a.author like '%"+a+"%' "
						+ 	"where b.isbn = a.isbn";
			try {
				rs = stmt.executeQuery(sql);

			//add results to output
				output += String.format("%-15s%-50s%-50s","isbn","title","author" );
				while (rs.next())
					output += String.format("\n%-15s%-50s%-25s", rs.getString(1),rs.getString(2),rs.getString(3));
			}catch (SQLException e) {
				return e.getMessage();
			}
		
		}
		output += "\n";
		return output;
	}
		
		//by Keyword
	public static String searchKeyword(String keywords){		
		if (keywords.length()<3) return "";
		String output = "";
		for(String k: keywords.split(",( |)")){
			String sql =	"select distinct b.isbn, b.name, k.keyword "
						+ 	"from books b, books_keywords k "
						+	"inner join books_keywords on k.keyword like '%"+k+"%' "
						+ 	"where b.isbn = k.isbn";
			try{
				rs = stmt.executeQuery(sql);
				
				//add results to output
				output += String.format("%-15s%-40s%-25s","isbn","title","keywords" );
				while (rs.next()){
					output += String.format("\n%-15s%-40s%-25s", rs.getString(1),rs.getString(2),rs.getString(3));
			}
			}catch (SQLException e){
				return e.getMessage();
			}
		}
		output += "\n";
		return output;
	}
	
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
