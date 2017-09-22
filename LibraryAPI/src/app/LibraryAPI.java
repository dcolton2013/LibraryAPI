package app;
import models.Library;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class LibraryAPI{
	public static Library lib;
	private static Scanner scan = new Scanner(System.in);
    //Main class allow login (manager,associate,member) and nonmembers to search db
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
        System.out.println("Library API");
        startApp();
    }
	
    //Main menu interface
    private static void showMainMenu() throws ClassNotFoundException, SQLException {
	        System.out.println("*****************************");
	        System.out.println("1. Initialize Library");
	        System.out.println("2. Login Library Manager");
	        System.out.println("3. Login Library Associate");
	        System.out.println("4. Login Library Member");
	        System.out.println("5. Search DB");
	        System.out.println("\t-1 to exit");
	        System.out.println("*****************************");
	        System.out.print("\tSelection:  ");
	}
    
	//Handle main menu options
    public static void startApp() throws ClassNotFoundException, SQLException{
    	int selection = 0;
    	showMainMenu();
    	while (true){
    			selection = scan.nextInt();
    			scan.nextLine();
    			switch(selection){
	            	case 1: initLibrary();
	                        break;
	                case 2: System.out.print("\tusername: ");
	                 		 String username = scan.nextLine();
	                 		 
	                 		 System.out.print("\tpassword: ");
	                 		 String password = scan.nextLine();
	                 		 
	                 		 Library.loginManager(username,password);
	                         break;
	                //case 3: lib.loginAssociate;
	                //         break;
	             	//case 4: lib.loginMember;
	                //         break;
	                case -1: System.exit(0);
	                 		  break;
	                default: System.out.println("Invalid selection.");
	             }
	         showMainMenu();
    	}
     }
    
    //initialize library
    public static void initLibrary() throws ClassNotFoundException, SQLException{
    	lib = new Library();
    }
}

//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main
