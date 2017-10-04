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
	
	public static void showAssociateMenu() throws SQLException{
        System.out.println("*****************************");
        System.out.println("1. Create new member");
        System.out.println("2. Check out book (NYI)");
        System.out.println("3. Return books (NYI)");
        System.out.println("4. Logout");
        System.out.println("*****************************");
        System.out.print("\tSelection: ");
	}
	
	//handles Associate menu options
	public static void handleMain() throws SQLException {
		int selection = 0;
    	showAssociateMenu();
    	while (true){
			selection = scan.nextInt();
			scan.nextLine();
			switch(selection){
				case 1:	promptUserInfo();
						break;
				case 2: 
						break;
				case 3:
						break;
				case 4:	selection = -1;
						break;
				default: System.out.println("invalid selection");
			}
			if (selection == -1) break;
				showAssociateMenu();
    	}
		
	}
	
	public static void promptUserInfo() throws SQLException{
		System.out.print("\tFirst Name:");
		String fname = scan.nextLine();
		
		System.out.print("\tLast Name:");
		String lname = scan.nextLine();
		
		System.out.print("\tAddress:");
		String addr = scan.nextLine();

		System.out.print("\tPhone:");
		String phone = scan.nextLine();
		
		System.out.print("\tUsername:");
		String username = scan.nextLine();
		System.out.println();
		
		String code = Member.generateLibrarycode();
		int returncode = addMember(fname,lname,addr,phone,username,code);
		
		while(returncode != 0){
			if (returncode == 1){
				//duplicate username
				System.out.println("uname in use");
				System.out.println("Username:");
				username = scan.nextLine();
			}
			if (returncode == 2){
				//duplicatecode
				code = Member.generateLibrarycode();
			}
			returncode = addMember(fname,lname,addr,phone,username,code);
		}	
		System.out.println("\tuser added.");
		System.out.println("\tCredentials: ");
		System.out.println("\tUsername: " + username);
		System.out.println("\tLibrary Code: " + code);
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
