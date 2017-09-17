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
	        System.out.println("2. Create new associate");
	        System.out.println("3. Create new member");
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
			case 1: createEmployee("managers");
					break;
			case 2: createEmployee("associates");
					break;
			case 3:
					break;
			case 4:
					break;
			case -1:break;
		}
	}

	private static void getEmployeeInfo(String table) {
		//prompt for manager info
		//Library.addEmployee(table, "Kobe", "Bryant", "kbryant", "manpass0");
	}
}