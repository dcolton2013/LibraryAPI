package app;
import java.io.IOException;
import java.util.*;

import models.Associate;
import models.Library;
import models.Manager;
import models.Member;

import tests.*;

public class demo {
	public static void main(String [] args) throws InterruptedException {
		///////Search Functions
		booksearch.test(new String[] {"J.K. Rowling","Biography","0439708184","Them: Adventures with Extremists"});
		while (go() == false) {}
		
		
		
		///////edit book functions
//		Manager.editBookName("0060854936", "Edited Book Title");
//		Manager.editBookPrice("0060854936", 10);
//		Manager.editTotalCopies("0060854936", -1);
//		while (go() == false) {}
		
		
		
		//create members and associates
		System.out.println("Manager.createMember(\"Nemo\", \"Sacosa\", \"77 Goro Street\", \"2298889876\", \"nsacosa\")");
		Associate.addMember("Nemo", "Sacosa", "77 Goro Street", "2298889876", "nsacosa");
		System.out.println("Manager.createAssociate(\"assoc5\", \"apass5\")");
		Manager.createAssociate("assoc5", "apass5");
		while (go() == false) {}
		
		
		
		///////Login
		System.out.println("loginManager(\"man1\",\"mpass1\"): ");
		Manager man = login_logout.testManager( new String[]{"man1","mpass1"} );
		System.out.println("loginAssociate(\"assoc1\",\"apass1\"): ");
		Associate a = login_logout.testAssociate( new String[]{"assoc1","apass1"} );
		System.out.println("loginMemmber(\"ssmitherson\",\"f2694800\"): ");
		Member memb = login_logout.testMember( new String[]{"ssmitherson","f2694800"} );
		while (go() == false) {}

		
		
		
		///////member retrieving displaying info (checkouts + holds)
		memberInfo.main(new String[] {"6223"});
		
		
		
		///////Scan In/Scan out
		
		
		
		///////member - report lost - request hold - request renewal
		Member.reportLost("1400082773", "2110");
		Member.requestRenewal("1400082773", "2110");
		Member.requestHold("0397001517", "2110");
		Member.requestHold("0397001517", "3330");
		while (go() == false) {}
		
		
		
		///////Manger applying charges
//		System.out.println("Manager.applyCharges():");
//		do{
//			long t= System.currentTimeMillis();
//			long end = t+200;
//			while(System.currentTimeMillis() < end) {
//			  Manager.applyCharges();
//			  Thread.sleep(1);
//			}
//		}while (go() == false);

		
		
		///////Member make payment
//		System.out.println("Member.makePayment(code, amount)");
//		Member.makePayment(20,"2110");
		
		
		
		//logout
		
	}
	
	static boolean go() {
		String input;
		Scanner scan = new Scanner(System.in);
		input = scan.nextLine();
		if (input.isEmpty())
			return true;
		else
			return false;
	}
}
