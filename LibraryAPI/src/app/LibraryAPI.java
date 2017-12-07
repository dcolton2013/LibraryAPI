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
        System.out.println("\n\n\n\n");
        try {
		//	demo.main(args);
//        	Associate.scanOutBook("9161", "0440445450");
//            Associate.scanOutBook("9161", "9780345803481");
//            Associate.scanOutBook("9161", "9780849946158");
//            Associate.scanOutBook("9161", "0743233212");
//            Associate.scanOutBook("9161", "1594771537");
//            Associate.scanOutBook("9161", "0394900014");
//            Associate.scanOutBook("9161", "9780789331144");
//            Associate.scanOutBook("9161", "0439708184");
//            Associate.scanOutBook("9161", "0915957264");
            Associate.scanOutBook("9161", "0439064872");
//            Associate.scanOutBook("9161", "9780062645227");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}	
	}
	
	public static void testmethods(){
    }
	
	
	//initialize library
    private static void initLibrary(){
		new Library();
    }
}

//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main