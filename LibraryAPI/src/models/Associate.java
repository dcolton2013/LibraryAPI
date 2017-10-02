package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
//may need to import book.java
import java.util.*;

public class Associate{
	private String password;
	private String username;
	private String name;
	private int id;
	private static Scanner scan = new Scanner(System.in);
	static Statement stmt;
	static Connection conn;

	//DS to keep up with Associate activity
	private ArrayList<String> activity;

	Associate(String name, int id){
		this.name=name;
		this.id=id;
		this.activity = new ArrayList<String>();
	}

	/*getters/setters of data field*/

	/* Validate book, update availabilty*/
	public void scanInBook(String book){
		
	}

	public void scanOutBook(String book){
		
	}
	/**/

	public String toString(){ 
		return " ";
	}
	
	public static void promptUserInfo() throws SQLException{
		System.out.println("First Name:");
		String fname = scan.nextLine();
		
		System.out.println("Last Name:");
		String lname = scan.nextLine();
		
		System.out.println("Address:");
		String addr = scan.nextLine();
		
		System.out.println("Phone:");
		String phone = scan.nextLine();
		
		System.out.println("Username:");
		String username = scan.nextLine();
		
		String code = Member.generateLibrarycode();
		int returncode = addMember(fname,lname,addr,phone,username,code);
		
		while(returncode != 0){
			if (returncode == 1){
				System.out.println("uname in use");
				System.out.println("Username:");
				username = scan.nextLine();
			}
			if (returncode == 2){
				code = Member.generateLibrarycode();
			}
			returncode = addMember(fname,lname,addr,phone,username,code);
		}	
		System.out.println("user added.");
	}
	
	public static int addMember(String fname, String lname,
							 	String addr, String phone,
							 	String username,String code) throws SQLException{
		
		String sql = "insert into members values(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,fname);
		pstmt.setString(2,lname);
		pstmt.setString(3,addr);
		pstmt.setString(4,phone);
		pstmt.setString(5,username);
		pstmt.setString(6,Member.generatePassword());
		pstmt.setString(7,Member.generateLibrarycode());
		pstmt.setInt(8,0);
		pstmt.setBoolean(9,false);
		pstmt.setBoolean(10,false);
		
		try {
			pstmt.execute();
		} catch (SQLException e) {
			String s = e.getLocalizedMessage();
			System.out.println(s);
			if (s.contains("uname") || s.contains("PRIMARY")){
				//duplicate username
				return 1;
			}else if (s.contains("code")){
				//duplicate code;
				return 2;
			}
		}
		return 0;
	}
}
