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
			Library.increaseHolds(isbn);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//returns balance after payment
	public static double makePayment(double amount,String username){
		if (amount <= 0) return 0;
		
		//do fees exist?
		double bookfees = getBookFees(username);
		double latefees = getLateFees(username);
		if (bookfees == 0 && latefees == 0) return amount;
		
		//all book fees must be paid in full before late fees
		amount = payBookFees(username,amount,bookfees);
		if (amount > 0)
			amount = payLateFees(username,amount,bookfees);
		
		return amount;
	}

	private static double getLateFees(String username) {
		String sql = "select sum(latefees) "+
					 "from member_checkouts "+
					 "where username = '"+username+"'";
		Connection conn2 = conn;
		try{
			Statement stmt2 = conn2.createStatement();
			ResultSet rs2 = stmt2.executeQuery(sql);
			if (rs2.next())
				return rs2.getDouble(1);
		}catch(SQLException ex){
			System.out.println(ex.getMessage());
		}
		return 0;
	}

	private static double getBookFees(String username) {
		String sql = "select sum(bookfees) "+
				 	 "from member_checkouts "+
				 	 "where username = '"+username+"'";
		Connection conn2 = conn;
		try{
			Statement stmt2 = conn2.createStatement();
			ResultSet rs2 = stmt2.executeQuery(sql);
			if (rs2.next())
				return rs2.getDouble(1);
		}catch(SQLException ex){
			System.out.println(ex.getMessage());
		}
		return 0;
	}
	
	//pay methods find rows where fees need to be paid
	private static double payBookFees(String username,double amount, double bookfees){
		String sql = "select bookfees, isbn "+
			 	 	 "from member_checkouts "+
			 	     "where (username = '"+username+"')";
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()){
				//lost book fees receive first priority
				if (bookfees > 0){
					double bookfee = rs.getDouble(1);
					if (bookfee == 0) continue;
					if (amount <= bookfee){
						updateBookFees(rs.getString(2), amount, username);
						return 0;
					}else{
						updateBookFees(rs.getString(2), bookfee, username);
						amount -= bookfee;
					}
					bookfees = getBookFees(username);
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return amount;
	}
	
	private static double payLateFees(String username,double amount, double latefees){
		String sql = "select latefees, isbn "+
			 	 	 "from member_checkouts "+
			 	 	 "where (username = '"+username+"')";
		try{
			rs = stmt.executeQuery(sql);
			//handle late fees after all late books fees are paid
			while (rs.next()){
				if (latefees > 0){
					double latefee = rs.getDouble(1);
					if(latefee == 0) continue;
					if (amount <= latefee){
						updateLateFees(rs.getString(2), amount, username);
						return 0;
					}else{
						updateLateFees(rs.getString(2), latefee, username);
						amount -= latefee;
					}
					latefees = getLateFees(username);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return amount;
	}
	
	//update methods update the fee column based on the amount
	//if amount >= fee (fee - book)
	//if amount < fee (fee - amount)
	private static void updateBookFees(String isbn, double amount,String username){
		String sql = "update member_checkouts "+
					 "set bookfees = bookfees - ? "+
					 "where (isbn = ? and username = ?)";
		PreparedStatement pstmt;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.setString(2, isbn);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void updateLateFees(String isbn, double amount, String username) {
		String sql = "update member_checkouts "+
				 	 "set latefees = latefees - ? "+
				 	 "where (isbn = ? and username = ?)";
		PreparedStatement pstmt;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.setString(2, isbn);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
