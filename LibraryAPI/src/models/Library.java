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
<<<<<<< HEAD
	private static String manager = ""; 
	private static String associate = "";
	private static String member = "";
=======
	private static String currentUser = "";
	
	//authority levels
	//0: admin, managers
	//1: associates
	//2: members
	//else: nonmembers
	private static int authorityLevel = 3;
	private static String username="";
>>>>>>> 896a00ca4acce6353b486634db007f380bcab446
	  
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
