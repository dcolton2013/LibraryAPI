package app;
import models.Associate;
import models.Library;
import models.Manager;
import models.Member;
//import tests.*;

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
	
	public static void main(String[] args){
        System.out.println("Library API");
        initLibrary();
        testMethod();
    }
<<<<<<< HEAD
	
    private static void testMethod() {
    	
	}

	//initialize library
=======
   
    //initialize library
>>>>>>> 267e64c4e5064fb790e890483edcf0aba7cad3fd
    public static void initLibrary(){
		new Library();
    }
}


//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main