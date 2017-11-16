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
	
	public static void returnToDropbox(String code, String isbn){
		if (!Library.userExists(code)){
			System.out.println("User doesnt exist");
			return;
		}
		if (!Library.bookExists(isbn)){
			System.out.println("Book doesnt exist");
			return;
		}
		
		if (!bookCheckedOut(code,isbn)){
			System.out.println("User doesnt have book checked out");
			return;
		}
		
		String sql = "insert into members_returns values "+
					 "(?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, code);
			ps.setString(2, isbn);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static boolean bookCheckedOut(String code, String isbn){
		String sql = "select * "+
				 	 "from members_checkout "+
					 "where isbn = ? and code = ?";
		try {
			PreparedStatement ps= conn.prepareStatement(sql);
			ps.setString(1, isbn);
			ps.setString(2, code);
			rs = ps.executeQuery();
			if (!rs.next())
				return false;
			else 
				return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	//returns balance after payment
	public static double makePayment(double amount,String code){
		if (!Library.userExists(code)) return -1;
		
		if (amount <= 0) return 0;
		
		//do fees exist?
		double bookfees = getBookFees(code);
		double latefees = getLateFees(code);
		
		if (bookfees == 0 && latefees == 0) return amount;
		
		double minPayment = getMinimumPayment(code);
		updateMinimumPayment(code,amount,minPayment);
		
		//all book fees must be paid in full before late fees
		if (bookfees>0)
			amount = payBookFees(code,amount,bookfees);
		if (amount > 0 && latefees>0)
			amount = payLateFees(code,amount,latefees);
		
		return amount;
	}

	public static double getLateFees(String code) {
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

	public static double getBookFees(String code) {
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
	
	public static double getMinimumPayment(String code){
		String sql = "select minPayment "+
					 "from members "+
					 "where code = '"+code+"'";
		try{
			rs=stmt.executeQuery(sql);
			if(rs.next());
				return rs.getDouble(1);
		}catch(SQLException ex){
			//System.out.println(ex.getMessage());
			return -1;
		}
		
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
	//if amount >= fee (fee - fee)
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
  
	private static void updateMinimumPayment(String code, double amount,double minimumPayment){
		if (amount >= minimumPayment)
			amount = minimumPayment;
		
		String sql = "update members "+
					 "set minPayment = minPayment - ? "+
					 "where code = ?";
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDouble(1, amount);
			ps.setString(2, code);
			ps.executeUpdate();
		}catch(SQLException ex){
			System.out.println(ex.getMessage());
		}
	}     
	public static void displayHolds(String code){
		if (!Library.userExists(code))
			System.out.println("user not found");
		
		String sql = "select b.name, m.holdExpiration, m.holdPos "+
					 "from members_holds m, books b "+
					 "where code = ? and m.isbn = b.isbn";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, code);
			rs = ps.executeQuery();
			
			if (!rs.next()){
				System.out.println("User has no books checked out");
			}
			else{
				System.out.printf("%-10s%-30s%-10s\n","Hold Pos.","Title","Expiration");
				do{
					System.out.printf("%-10s%-30s",rs.getString(3),rs.getString(1).substring(0, Math.min(rs.getString(1).length(), 27)));
					if (rs.getString(2) == null)
						System.out.printf("%-10s\n","no current expiration date");
					else
						System.out.printf("%-10s\n",rs.getString(2));
				}while(rs.next());
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void displayCheckoutInfo(String code){
		if (!Library.userExists(code))
			System.out.println("user not found");
		
		String sql = "select b.name, m.returndate, m.latefees, m.bookfees "+
					 "from members_checkouts m, books b "+
					 "where code = ? and m.isbn = b.isbn and m.status = 'checkedout'";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, code);
			rs = ps.executeQuery();
			if (!rs.next()){
				System.out.println("User has no books checkout");
			}
			else{
				System.out.printf("%-30s%-25s%-10s%-10s%-5s\n","Title","Return Date","Late Fees", "Book Fees", "Total");
				do{
					System.out.printf("%-30s%-25s%-10.2f%-10.2f%-5.2f\n",rs.getString(1).substring(0, Math.min(rs.getString(1).length(), 27))
															,rs.getString(2),rs.getDouble(3),rs.getDouble(4),(rs.getDouble(3) + rs.getDouble(4)));
				}while(rs.next());
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
