package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Member {
	static Statement stmt;
	static Connection conn;
	private static ResultSet rs;
	
	public static String generatePassword(){
		String password = UUID.randomUUID().toString();
		password.replace("-","");
		password = password.substring(0,8);
		return password;
	}
	
	public static String generateLibrarycode(){
		Random random = new Random();
		int code = random.nextInt(9000) + 1000;
		return "" + code;
	}
	
	public static void reportLost(String isbn){
		String sql = "update member_checkouts "+
				     "set status = 'lost',bookfees = ?"+
				     "where isbn = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, Library.getBookCost(isbn));
			pstmt.setString(2, isbn);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void requestHold(String isbn){
		String sql = "insert into member_holds values(?,?,?,?)";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Library.currentUser);
			pstmt.setString(2, isbn);
			pstmt.setInt(3, Library.getNumHolds(isbn)+1);
			pstmt.setDate(4, null);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Library.increaseHolds(isbn);
	}
	
	public static void makePayment(double amount){
		
	}
}
