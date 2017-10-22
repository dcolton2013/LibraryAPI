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
	
	public static void reportLost(String isbn,String code){
		String sql = "update members_checkouts "+
				     "set status = 'lost',bookfees = ?"+
				     "where (isbn = ? and code = ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, Library.getBookCost(isbn));
			pstmt.setString(2, isbn);
			pstmt.setString(3, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void requestHold(String isbn,String code){
		String sql = "insert into members_holds values(?,?,?,?)";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, isbn);
			pstmt.setInt(3, Library.getNumHolds(isbn)+1);
			pstmt.setDate(4, null);
			pstmt.execute();
			Library.increaseHolds(isbn);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void requestRenewal(String isbn, String code){
		if (!Library.bookExists(isbn)){
			System.out.println("no matching isbn");
			return;
		}
		if (!Library.userExists(code)){
			System.out.println("invalid library code");
			return;
		}
		Associate.renewBook(isbn,code);
	}
	
	//returns balance after payment
	public static double makePayment(double amount,String code){
		if (amount <= 0) return 0;
		
		//do fees exist?
		double bookfees = getBookFees(code);
		double latefees = getLateFees(code);
		if (bookfees == 0 && latefees == 0) return amount;
		
		//all book fees must be paid in full before late fees
		amount = payBookFees(code,amount,bookfees);
		if (amount > 0)
			amount = payLateFees(code,amount,bookfees);
		
		return amount;
	}

	private static double getLateFees(String code) {
		String sql = "select sum(latefees) "+
					 "from members_checkouts "+
					 "where code = '"+code+"'";
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

	private static double getBookFees(String code) {
		String sql = "select sum(bookfees) "+
				 	 "from members_checkouts "+
				 	 "where code = '"+code+"'";
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
	private static double payBookFees(String code,double amount, double bookfees){
		String sql = "select bookfees, isbn "+
			 	 	 "from members_checkouts "+
			 	     "where (code = '"+code+"')";
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()){
				//lost book fees receive first priority
				if (bookfees > 0){
					double bookfee = rs.getDouble(1);
					if (bookfee == 0) continue;
					if (amount <= bookfee){
						updateBookFees(rs.getString(2), amount, code);
						return 0;
					}else{
						updateBookFees(rs.getString(2), bookfee, code);
						amount -= bookfee;
					}
					bookfees = getBookFees(code);
				}
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return amount;
	}
	
	private static double payLateFees(String code,double amount, double latefees){
		String sql = "select latefees, isbn "+
			 	 	 "from members_checkouts "+
			 	 	 "where (code = '"+code+"')";
		try{
			rs = stmt.executeQuery(sql);
			//handle late fees after all late books fees are paid
			while (rs.next()){
				if (latefees > 0){
					double latefee = rs.getDouble(1);
					if(latefee == 0) continue;
					if (amount <= latefee){
						updateLateFees(rs.getString(2), amount, code);
						return 0;
					}else{
						updateLateFees(rs.getString(2), latefee, code);
						amount -= latefee;
					}
					latefees = getLateFees(code);
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
	private static void updateBookFees(String isbn, double amount,String code){
		String sql = "update members_checkouts "+
					 "set bookfees = bookfees - ? "+
					 "where (isbn = ? and code = ?)";
		PreparedStatement pstmt;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.setString(2, isbn);
			pstmt.setString(3, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void updateLateFees(String isbn, double amount, String code) {
		String sql = "update members_checkouts "+
				 	 "set latefees = latefees - ? "+
				 	 "where (isbn = ? and code = ?)";
		PreparedStatement pstmt;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.setString(2, isbn);
			pstmt.setString(3, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
