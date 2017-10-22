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
	
    //Main class allow login (manager,associate,member) and nonmembers to search db
	public static void main(String[] args) throws InterruptedException, ParseException{
        System.out.println("Library API");
        initLibrary();
        System.out.println();
        //startApp();
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