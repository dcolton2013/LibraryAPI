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
        showMainMenu();
    }
	
    //Main menu interface
    private static void showMainMenu() throws ClassNotFoundException, SQLException {
    	int selection = 0;
        System.out.println("*****************************");
        System.out.println("1. Initialize Library");
        System.out.println("2. Login Library Manager");
        System.out.println("3. Login Library Associate");
        System.out.println("4. Login Library Member");
        System.out.println("5. Search DB");
        System.out.println("\t-1 to exit");
        System.out.println("*****************************");
        System.out.print("\tSelection:  ");
        selection = scan.nextInt();
        startApp(selection);
	}
    
	//Handle main menu options
    public static void startApp(int selection) throws ClassNotFoundException, SQLException{
		while(selection != -1){
	         if (selection>0 && selection<5){
	             switch(selection){
	                 case 1: initLibrary();
	                         break;
	                 case 2: Library.loginManager("admin","admin");
	                         break;
	//                 case 3: lib.loginAssociate;
	//                         break;
	//                 case 4: lib.loginMember;
	//                         break;
	                 case -1: System.exit(0);
	             }
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