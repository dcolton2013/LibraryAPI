package models;
import java.sql.SQLException;
import java.util.*;
public class Manager{
	public static void showManagerMenu() throws SQLException{
		Scanner scan = new Scanner(System.in);
		int selection = 0;
		while (selection != -1){
	        System.out.println("*****************************");
	        System.out.println("1. Create new manager");
	        System.out.println("2. Create new member");
	        System.out.println("3. Create new associate");
	        System.out.println("4. Logout");
	        System.out.println("\t-1 return to main menu");
	        System.out.println("*****************************");
	        System.out.print("\tSelection: ");
	        selection = scan.nextInt();
	        start(selection);
		}
	}

	private static void start(int selection) throws SQLException {
		switch(selection){
			case 1: getManagerInfo();
					break;
			case 2:
					break;
			case 3:
					break;
			case 4:
					break;
			case -1:break;
		}
	}

	private static void getManagerInfo() {
		//prompt for manager info
		//Library.addEmployee("managers", "Kobe", "Bryant", "kbryant", "manpass0");
	}
}