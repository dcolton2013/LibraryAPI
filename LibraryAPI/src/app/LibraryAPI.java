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
    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        System.out.println("Library API");
        showMainMenu();
       
    }

    private static void showMainMenu() throws ClassNotFoundException, SQLException {
    	int selection = 0;
        System.out.println("*****************************");
        System.out.println("1. Initialize Library");
        System.out.println("2. Login Library Manager");
        System.out.println("3. Login Library Associate");
        System.out.println("4. Login Library Member");
        System.out.println("\t-1 to exit");
        System.out.println("*****************************");
        System.out.print("\tSelection:  ");
        selection = scan.nextInt();
        startApp(selection);
	}

	public static void startApp(int selection) throws ClassNotFoundException, SQLException{
		//while(selection != -1){
	         if (selection>0 && selection<5){
	             switch(selection){
	                 case 1: initLibrary();
	                         break;
	                 case 2: System.out.println("\tusername: lbjames");
	                 		 System.out.println("\tusername: manpass1");
	                	 	 lib.loginManager("lbjames","manpass1");
	                         break;
	//                 case 3: lib.loginAssociate;
	//                         break;
	//                 case 4: lib.loginMember;
	//                         break;
	                 case -1: System.exit(0);
	             }
	         }
	         showMainMenu();
		//}
     }
    
    public static void initLibrary() throws ClassNotFoundException, SQLException{
    	lib = new Library();
    }
}

//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main
