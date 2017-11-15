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
<<<<<<< HEAD
        System.out.println();
        //startApp();
=======
        testMethodsHere();
>>>>>>> d0e2550effde51c0ec86cbbe7bf4b8920c0fcd92
    }
	
    private static void testMethodsHere() {
	}

	//initialize library
    public static void initLibrary(){
		new Library();
    }
}

//compile all neccessary files from root directory:
//  javac $(find . -name "*.java")
//Run program from main:
//  java app.main