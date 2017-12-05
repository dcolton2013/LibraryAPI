package tests;

import models.Library;

public class booksearch {
	public static void test(String[]args){
		System.out.println("****searchAuthor(author name):");
		System.out.println(Library.searchAuthor(args[0]));
		System.out.println();

		System.out.println("****searchKeyword(Biography):");
		System.out.println(Library.searchKeyword(args[1]));
		System.out.println();
		
		System.out.println("****searchISBN(isbn):");
		System.out.println(Library.searchISBN(args[2]));
		
		System.out.println("****searchTitle(isbn):");
		System.out.println(Library.searchTitle(args[3]));
		
		System.out.println("****searchAvailability(isbn):");
		System.out.println(Library.searchAvailability());
	}
}
