package app;
import java.io.IOException;
import java.util.*;

import models.Associate;
import models.Library;
import models.Manager;
import models.Member;

import tests.*;


public class demo {
	public static void main(String [] args) {
		///////Search Functions
		booksearch.test(new String[] {"J.K. Rowling","Biography","0439708184","Them: Adventures with Extremists"});
		while (go() == false) {}
		
		///////Login-Logout
		System.out.println("loginManager(\"man1\",\"mpass1\"): ");
		login_logout.testManager( new String[]{"man1","mpass1"} );
		
		System.out.println("loginAssociate(\"assoc1\",\"apass1\"): ");
		login_logout.testAssociate( new String[]{"assoc1","apass1"} );
		
		System.out.println("loginMemmber(\"sbloobob\",\"9610739a\"): ");
		login_logout.testMember( new String[]{"sbloobob","9610739a"} );
		while (go() == false) {}
		login_logout.logout();
		while (go() == false) {}
				
		
	}
	
	static boolean go() {
		String input = " ";
		Scanner scan = new Scanner(System.in);
		while (!input.isEmpty()) {
			input = scan.nextLine();
		}
		return true;
	}
}
