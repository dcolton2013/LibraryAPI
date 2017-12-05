package models;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


public class Manager extends Associate{
	private static Scanner scan = new Scanner(System.in);
	static Statement stmt;
	static Connection conn;
	private static ResultSet rs;
	
	public static void applyCharges(){
		String sql = "update members_checkouts "+
					 "set latefees = latefees+.1, status = 'late'"+
					 "where returndate < NOW() AND status <> 'lost'";
		try {
			stmt.executeUpdate(sql);
			modifyMemberStatus();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void modifyMemberStatus(){
		//suspends users where applicable
		suspendMembers();
		
		//reactivates users who have paid the minimum payment
		reactivateMembers();
	}
	
	public static void suspendMembers(){
		String sql = "update members 			"+
					 "set suspended = '1' 		"+
					 "where code in ( 			"+
					 "				select code "+
					 "				from members_checkouts "+
					 "				where (latefees > 25 or status = 'lost') "+
					 "				)";
		try {
			stmt.executeUpdate(sql);
			calculateMinimunPayments();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void calculateMinimunPayments() {
		String sql = "select code "+
					 "from members "+
					 "where suspended = 1";
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				double latefees = Member.getLateFees(rs.getString(1));
				double bookfees = Member.getBookFees(rs.getString(1));
				double amount = 0;
				if (latefees>25){
					if(latefees>30)
						amount += latefees - 25;
					else
						amount += 5;
				}
				amount += bookfees;
				insertMinimumPayment(rs.getString(1),amount);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void insertMinimumPayment(String code, double amount) {
		String sql = "update members "+
					 "set minPayment = ? "+
					 "where code = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, amount);
			pstmt.setString(2, code);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
	}

	public static void reactivateMembers(){
		String sql = "update members "+
					 "set suspended = 0 "+
					 "where suspended = 1 and minPayment = 0";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void suspendMember(String code){
		String sql = "update members "+
					 "set suspended = 1 "+
					 "where code = ?";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void reactivateMember(String code){
		String sql = "update members "+
					 "set suspended = 0 "+
					 "where code = ?";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//functions to add tuples to the db
	public static void createManager(String uname,String password){
		String sql = 	"insert ignore into managers values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		try {
			stmt.executeUpdate(sql);
			System.out.println("\tadding manager: " + uname );
		} catch (SQLException e) {
			String s = e.getMessage();
			if (s.contains("PRIMARY"))
				System.out.println("Username " + uname + " already exists");
		}	
	}
	
	public static void createAssociate(String uname,String password){
		//System.out.println("\tadding associate: " + uname );
		String sql = 	"insert into associates values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		try {
			stmt.executeUpdate(sql);
			System.out.println("\tadding associate: " + uname );
		} catch (SQLException e) {
			String s = e.getMessage();
			if (s.contains("PRIMARY"))
				System.out.println("Username " + uname + " already exists");
		}
	}
	
	//create books
	//accepts authors and keywords as a string
	//separate each author/keyword with a comma
	//each book must contain at least one author and keyword
	public static int createBook(String isbn, String authors,String name, String year,int avail, double price, String keywords){
		//no author
		if (authors.length() == 0) return 1;
		
		//improper isbn length
		if (Integer.parseInt(year) < 2007){
			if(isbn.length() != 10) return 2;
		}else{
			if (isbn.length() != 13) return 3;
		}
		
		//no title
		if (name == null || name == "") return 4;
		
		if (keywords == null || keywords.length() == 0) return 5;
		
		insertIntoBooks(isbn,name,year,avail,price);
			
		insertIntoBookAuthors(isbn,authors);
			
		insertIntoBookKeywords(isbn, keywords);
		
		return 0;
	}
	
	private static void insertIntoBooks(String isbn, String name, String year, int avail, double price){
		String sql = "insert ignore into books values(?,?,?,?,?,?,?,?)";
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,isbn);
			pstmt.setString(2,name);
			pstmt.setString(3,year);
			pstmt.setInt(4,avail);
			pstmt.setInt(5,avail);
			pstmt.setInt(6,0);
			pstmt.setDouble(7,price);
			pstmt.setInt(8,isNewRelease(year));
			pstmt.execute();
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	private static void insertIntoBookKeywords(String isbn, String keywords) {
		try{
			for (String k: keywords.split(",( |)")){
				String sql = 	"insert ignore into books_keywords values( "+
								"'"+isbn	+"', "+ 
								"'"+k		+"')";
				stmt.executeUpdate(sql);
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}

	private static void insertIntoBookAuthors(String isbn,String authors) {
		try{
			for (String a: authors.split(",( |)")){
				String sql = 	"insert ignore into books_authors values( "+
								"'"+isbn	+"', "+ 
								"'"+a	+"')";
				stmt.executeUpdate(sql);
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void editBookName(String isbn, String title){
		if (!Library.bookExists(isbn))
			System.out.println("book not found");
		
		String sql = "update books "+
					 "set name = ? "+
					 "where isbn = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, isbn);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int isNewRelease(String year){
		//Determine new release
		//in 2017 year (2016-2017) will be new release
        Calendar cal = Calendar.getInstance();
        int currentyear = cal.get(Calendar.YEAR);
        if (Integer.parseInt(year) >= (currentyear- 1))
        	return 1;
        return 0;
	}
	
	public static void updateNewReleases(){
		Calendar cal = Calendar.getInstance();
        int currentyear = cal.get(Calendar.YEAR);
		String sql = "update books "+
					 "set newrelease = 0 "+
					 "where year < "+(currentyear-1);
		
		
		try{
			stmt.executeUpdate(sql);
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void editBookPrice(String isbn, double price){
		if (!Library.bookExists(isbn))
			System.out.println("book not found");
		
		String sql = "update books "+
					 "set price = ? "+
					 "where isbn = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDouble(1, price);
			ps.setString(2, isbn);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//accepts isbn an a positive or negative num (copies) to increase total copies by
	public static void editTotalCopies(String isbn, int copies){
			if (!Library.bookExists(isbn))
				System.out.println("book not found");
			
			if (copies == 0) return;
			
			//wont allow totalCopies to go below zero
			int currentCopies = Library.getTotalCopies(isbn);
			if (copies<0){
				if (currentCopies < Math.abs(copies))
					copies = -currentCopies;
			}
			
			String sql = "update books "+
						 "set totalCopies = totalCopies + ?, availableCopies = availableCopies + ? "+
						 "where isbn = ?";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setDouble(1, copies);
				ps.setDouble(2, copies);
				ps.setString(3, isbn);
				ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	public static void editNewRelease(String isbn, int b){
			if (!Library.bookExists(isbn))
				System.out.println("book not found");
			
			if (!(b == 1 || b==0))return;
			
			String sql = "update books "+
						 "set newRelease = ? "+
						 "where isbn = ?";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, b);
				ps.setString(2, isbn);
				ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	public static void dropKeyword(String isbn, String keyword){
			if (!Library.bookExists(isbn))
				System.out.println("book doesnt exist");
			
			String sql = "delete from books_keywords "+
						 "where isbn = ? and keyword = ?";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, isbn);
				ps.setString(2, keyword);
				ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
	//remove books by isbn
	public static void removeBookISBN(String isbn){
		//remove from books table if isbn is of required length
		if(isbn.length() < 7) return;
		
		String title = Library.getTitle(isbn);
		deleteFromBooksKeywords(isbn);
		deleteFromBooksAuthors(isbn);
		deleteFromBooks(isbn);
		System.out.println("Book: "+isbn+" "+title+" removed");
	}

	//remove by name
	public static void removeBookName(String name){
		removeBookISBN(Library.getISBN(name));
	}
	
	public static void deleteFromBooks(String isbn){
		String sql = "delete from books "+
				 "where ISBN LIKE '%"+isbn+"%'";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void deleteFromBooksAuthors(String isbn){
		String sql = "delete from books_authors "+
				 	 "where ISBN LIKE '%"+isbn+"%'";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void deleteFromBooksKeywords(String isbn){
		String sql = "delete from books_keywords "+
				 	 "where ISBN LIKE '%"+isbn+"%'";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
}