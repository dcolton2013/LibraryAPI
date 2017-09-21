package models;
import java.sql.SQLException;
import java.util.*;

public class Manager{
	static Scanner scan = new Scanner(System.in);
	
	public static void showManagerMenu() throws SQLException{
	        System.out.println("*****************************");
	        System.out.println("1. Create new manager");
	        System.out.println("2. Create new associate");
	        System.out.println("3. Create new member");
	        System.out.println("4. Logout");
	        System.out.println("\t-1 return to main menu");
	        System.out.println("*****************************");
	        System.out.print("\tSelection: ");
	}

<<<<<<< HEAD
	private static void start(int selection) throws SQLException {
		switch(selection){
			case 1: //createEmployee("managers");
					break;
			case 2: //createEmployee("associates");
					break;
			case 3:
					break;
			case 4:
					break;
			case -1:break;
		}
=======
	public static void start() throws SQLException {
		int selection = 0;
    	showManagerMenu();
    	while (true){
			selection = scan.nextInt();
			scan.nextLine();
			switch(selection){
				case 1: addManager();
						break;
				case 2: addAssociate();
						break;
				
				//case 3: break;
				
				case 4: Library.logoutManager();
						selection = -1;
						break;
						
				case -1:break;
				default: System.out.println("invalid selection");
			}
			if (selection == -1) break;
			showManagerMenu();
    	}
	}

	private static void addAssociate() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		
		Library.createAssociate(username,password);
		
>>>>>>> 895cc570e047f67b35184f5f3e6c261eeb786142
	}

	private static void addManager() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		Library.createManager(username,password);
	}
}