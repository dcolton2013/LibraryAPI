package models;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


public class Manager{
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
	
	private static void promptBookInfo() throws SQLException {
		System.out.println("Enter isbn: 9780439023528");
		System.out.println("Enter authors (seperated by comma): Suzanne Collins");
		System.out.println("Enter title: The Hunger Games (Book 1)");
		System.out.println("Enter year: 2010");
		System.out.println("Enter copies: 3");
		System.out.println("Enter price: 8.70");
		System.out.println("Enter keywords: Dystopian, Science-Fiction, Survival, Action");
		createBook("9780439023528","Suzanne Collins","The Hunger Games (Book 1)","2010", 3, 8.70, "Dystopian, Science-Fiction, Survival, Action" );
	}
	
	//prompt for info
	private static void addAssociate() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		createAssociate(username,password);
		
	}

	private static void addManager() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		createManager(username,password);
	}
	
	public static void suspendMember(String uname){
		String sql = "update members "+
					 "set suspended = 1 "+
					 "where username = ?";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uname);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void reactivateMember(String uname){
		String sql = "update members "+
					 "set suspended = 0 "+
					 "where username = ?";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uname);
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//functions to add tuples to the db
	public static void createManager(String uname,String password) throws SQLException{
		System.out.println("\tadding manager: " + uname);
		String sql = 	"insert ignore into managers values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	public static void createAssociate(String uname,String password) throws SQLException{
		System.out.println("\tadding associate: " + uname );
		String sql = 	"insert ignore into associates values (" +
					 	"'"+uname+"',"+
					 	"'"+password+"',"+
					 	"'"+0+"'"+
					 	")";
		stmt.executeUpdate(sql);	
	}
	
	//create books
	//accepts authors and keywords as a string
	//separate each author/keyword with a comma
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
			if (keywords == null || keywords.length() == 0) return;
			
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
	//Remove Functions
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