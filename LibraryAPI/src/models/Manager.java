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
	        System.out.println("4. Remove manager");
	        System.out.println("5. Remove associate");
	        System.out.println("6. Remove member");
	        System.out.println("7. Inventory options");
	        System.out.println("8. Logout");
	        System.out.println("\t-1 return to main menu");
	        System.out.println("*****************************");
	        System.out.print("\tSelection: ");
	}
	
	public static void showInventoryMenu() throws SQLException{
        System.out.println("*****************************");
        System.out.println("1. Create new book");
        System.out.println("2. Remove book");
        System.out.println("\t-1 return to manager options");
        System.out.println("*****************************");
        System.out.print("\tSelection: ");
	}
	
	public static void handleMain() throws SQLException {
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
				case 3:
						break;
				case 4:
						break;
				case 5:
						break;
				case 6:
						break;
					
				case 7:	handleInventory();
						break;
				
				case 8: Library.logoutManager();
						selection = -1;
						break;
						
				case -1:break;
				default: System.out.println("invalid selection");
			}
			if (selection == -1) break;
			showManagerMenu();
    	}
	}

	public static void handleInventory() throws SQLException{
        showInventoryMenu();
        while(true){
        	int selection = scan.nextInt();
            scan.nextLine();
        	switch (selection){
        	case 1: //Library.createBook("11", "sean john", "blah", "1996", 30, 19.66);
        			//Library.createBook("0011",new String[] {"pablo","shamboni"}, "blah", "1996", 30, 19.66);
        			break;
        	case 2: 
        			break;
        	default:System.out.println("invalid selection"); 
        			selection = -1;
        			break;
        	}
        	if (selection == -1) break;
        	showInventoryMenu();
        }
	}
	//actions
	private static void addAssociate() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		
		Library.createAssociate(username,password);
		
	}

	private static void addManager() throws SQLException {
		System.out.print("\tusername: ");
		String username = scan.nextLine();
		System.out.print("\tpassword: ");
		String password = scan.nextLine();
		Library.createManager(username,password);
	}
}