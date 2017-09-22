package models;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library{
	  private static final String url = "jdbc:mysql://localhost:3306/Library?useSSL=false";    
	  private static final String user = "root";
	  private static final String password = "faisal";
	  private static Connection conn;
	  private static Statement stmt;
	  private static ResultSet rs;
	  private static String query;
	  
	public Library() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, password);
        stmt = conn.createStatement();
        System.out.println("Database connected successfully");
        createDB();
	}
	public static void createDB(){
        try {
            stmt.executeUpdate("create schema if not exists Library;");
        } catch (SQLException ex) {
        }
    	createManagersTable();
//    	createAssociatesTable();
//    	createMembersTable();
//    	createBooksTable();
    }
	private static void createManagersTable() {
		System.out.println("Creating Table: managers...");
        //init table
        String managersTable = "create table if not exists managers (" +
        					 	"fname		varchar(15)		not null,"+
        					 	"lname		varchar(15)		not null,"+
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
	    		Manager.showManagerMenu();
			}
		
	}
	public static void addEmployee(String table,String fname,String lname,String uname, String password) throws SQLException{
		String sql = "insert ignore into "+ table +" values (" +
					 	"'"+fname+"',"+
					 	"'"+lname+"',"+
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	private static void createAssociatesTable() {
		System.out.println("Creating Table: associates...");
        //init table
        String associatesTable = "create table if not exists associates (" +
        					 	"fname		varchar(15)		not null,"+
        					 	"lname		varchar(15)		not null,"+
        					 	"username	varchar(15)		not null,"+
        					 	"password	varchar(15)		not null,"+
        					 	"loggedIn	boolean					,"+
        					 	"primarykey(username));";
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
        					 	"fname			varchar(15)		not null,"+
        					 	"lname			varchar(15)		not null,"+
        					 	"address		varchar(50)		not null,"+
        					 	"phone			varchar(10)		not null,"+
        					 	"username		varchar(15)		not null,"+
        					 	"password		varchar(15)		not null,"+
        					 	"code			varchar(4)		not null,"+
        					 	"loggedIn		boolean					,"+
        					 	"primarykey(username));";
        try {
            stmt.executeUpdate(membersTable);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
	}
	private static void createBooksTable() {
	}
}
