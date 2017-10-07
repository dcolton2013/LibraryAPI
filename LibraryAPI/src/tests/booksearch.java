package tests;

import models.Library;

public class booksearch {
	public static void main(String[]args){
		System.out.println("****Search by Author searchAuthor(author name)");
		System.out.println(Library.searchAuthor(args[0]));
		System.out.println();
		
		System.out.println("****Search by Keyword searchKeyword(Biography)");
		System.out.println(Library.searchKeyword(args[1]));
		System.out.println();
		
		System.out.println("****Search by ISBN searchISBN(isbn)");
		System.out.println(Library.searchISBN(args[2]));
	}
}
