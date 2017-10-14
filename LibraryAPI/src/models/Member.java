package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Member {
	static Statement stmt;
	static Connection conn;
	
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
}
