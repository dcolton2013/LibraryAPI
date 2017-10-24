package app;
import models.Associate;
import models.Library;
import models.Manager;
import models.Member;
import tests.*;

import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryAPI{	
	public static Library lib;
	private static Scanner scan = new Scanner(System.in);
	
    //Main class allow login (manager,associate,member) and nonmembers to search db
	public static void main(String[] args) throws InterruptedException, ParseException{
        System.out.println("Library API");
        initLibrary();
     //Manager.applyCharges();
     //TimeUnit.SECONDS.sleep(15);
     Member.makePayment(45, "4986");
      
        //startApp();
    }
	
    //Main menu interface
    private static void showMainMenu(){
        System.out.println("*****************************");
        System.out.println("1. Login Library Manager");
        System.out.println("2. Login Library Associate");
        System.out.println("3. Login Library Member(NYI)");
        System.out.println("4. Search DB");
        System.out.println("\t-1 to exit");
        System.out.println("*****************************");
        System.out.print("\tSelection:  ");
	}
    
    public static void showSearchMenu(){
        System.out.println("*****************************");
        System.out.println("1. Search by ISBN");
        System.out.println("2. Search by Keyword");
        System.out.println("3. Search by Author");
        System.out.println("4. Search by Title (NYI)");
        System.out.println("5. Show all books available for checkout (NYI)");
        System.out.println("\t-1 to go back");
        System.out.println("*****************************");
        System.out.print("\tSelection:  ");
    }
    
	//Handle main menu options
    public static void startApp(){
    	int selection = 0;
    	showMainMenu();
    	while (true){
    			selection = scan.nextInt();
    			scan.nextLine();
    			switch(selection){
	                case 1:	System.out.print("\tusername: ");
	                 		String username = scan.nextLine();
	                 		 
	                 		System.out.print("\tpassword: ");
	                 		String password = scan.nextLine();
	                 		 
	                 		Library.loginManager(username,password);
	                        break;
	                case 2: System.out.print("\tusername: ");
            		 		username = scan.nextLine();
             		 
            		 		System.out.print("\tpassword: ");
            		 		password = scan.nextLine();
            		 
            		 		Library.loginAssociate(username,password);
	                        break;
	             	case 3: //System.out.print("\tusername: ");
    		 				//username = scan.nextLine();
            		 
    		 				//System.out.print("\tpassword: ");
    		 				//password = scan.nextLine();
	             			//Library.loginMember
	                        break;
	                case 4: handleSearch();
	                 		break;
	                default: System.out.println("Invalid selection.");
	             }
	         showMainMenu();
    	}
     }
    
    public static void handleSearch(){
        showSearchMenu();
        while(true){
        	int selection = scan.nextInt();
            scan.nextLine();
        	switch (selection){
	        	case 1:	System.out.println(Library.searchISBN("0060854936"));
	        			break;
	        	case 2: System.out.println(Library.searchKeyword("Biography"));
	        			break;
	        	case 3: System.out.println(Library.searchAuthor("Radric Davis"));
        				break;
	        	case 4: 
        				break;
	        	case 5: 
    					break;
    					
	        	case -1:selection = -1;
        				break;
	        	default:System.out.println("invalid selection"); 
	        			break;
        	}
        	if (selection == -1) break;
        	showSearchMenu();
        }
    }
    
    //initialize library
    public static void initLibrary(){
		lib = new Library();
    }
}

//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main