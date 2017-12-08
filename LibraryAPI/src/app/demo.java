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
		System.out.println("Search Functions: ");
		while (go() == false) {}
		booksearch.test(new String[] {"J.K. Rowling","Biography","0439708184","Them: Adventures with Extremists"});
		while (go() == false) {}
		
		
		
		//create book
		System.out.println("\n\n\nDB rejects books w/ incorrect isbn length");
		System.out.println("Manager.createBook(\"97814496\", \"Duncan A. Buell\", \"Data Structures Using Java\", \"2011\", 1, 31.49, \"Data Structures, Java, Programming\")");
		Manager.createBook("97814496", "Duncan A. Buell", "Data Structures Using Java", "2011", 1, 31.49, "Data Structures, Java, Programming");
		while (go() == false) {}
		
		System.out.println("Manager create books: \nManager.createBook(\"9781449628079\", \"Duncan A. Buell\", \"Data Structures Using Java\", \"2011\", 1, 31.49,\"Data Structures, Java, Programming\")");
		Manager.createBook("9781449628079", "Duncan A. Buell", "Data Structures Using Java", "2011", 1, 31.49,"Data Structures, Java, Programming");
		while (go() == false) {}
		
		
		///////edit book functions
		System.out.println("\n\n\n\nEdit Book functions: Profiles in Courage");
		while (go() == false) {}
		Manager.editBookName("0060854936", "Edited Book Title");
		Manager.editBookPrice("0060854936", 10);
		Manager.editTotalCopies("0060854936", -1);
		
		System.out.println("ISBN 0060854936");
		System.out.println("Profiles in Courage -> Edited Book Title");
		System.out.println("20.15 -> 10");
		System.out.println("3 -> 2");
		while (go() == false) {}
		
		
		
		//create members and associates
		System.out.println("\n\n\n\nCreate Managers and Associtates");
		System.out.println("Manager.createMember(\"Nemo\", \"Sacosa\", \"77 Goro Street\", \"2298889876\", \"nsacosa\")");
		Associate.addMember("Nemo", "Sacosa", "77 Goro Street", "2298889876", "nsacosa");
		System.out.println("Manager.createAssociate(\"assoc5\", \"apass5\")");
		Manager.createAssociate("assoc5", "apass5");
		while (go() == false) {}
		
		
		
		///////Login
		System.out.println("\n\n\n\nLogin Functions: ");
		System.out.println("loginManager(\"man1\",\"mpass1\"): ");
		Manager man = login_logout.testManager( new String[]{"man1","mpass1"} );
		System.out.println("loginAssociate(\"assoc1\",\"apass1\"): ");
		Associate a = login_logout.testAssociate( new String[]{"assoc1","apass1"} );
		System.out.println("loginMemmber(\"ssmitherson\",\"7996870c\"): ");
		Member memb = login_logout.testMember( new String[]{"ssmitherson","f2694800"} );
		while (go() == false) {}
		
		
		///////Scan In/Scan out
		System.out.println("\n\n\n\n\n- ScanIn/Out");
		System.out.println("- Member exceeding 10 books");
		//member exceeding ten books
		Associate.scanOutBook("5693", "9781501175565");
		Associate.scanOutBook("5693", "0394900014");
		Associate.scanOutBook("5693", "0397001517");
		Associate.scanOutBook("5693", "0439064872");
		Associate.scanOutBook("5693", "0439708184");
		Associate.scanOutBook("5693", "0743233212");
		Associate.scanOutBook("5693", "0440445450");
		Associate.scanOutBook("5693", "0915957264");
		Associate.scanOutBook("5693", "9780062645227");
		Associate.scanOutBook("5693", "9780345803481");
		while (go() == false) {}
		Associate.scanOutBook("5693", "9780789331144");
		while (go() == false) {}
		
		System.out.println("\n\n\n\n- Scan out a few more books");
		Associate.scanOutBook("2698", "9781501175565");
		Associate.scanOutBook("2698", "0394900014");
		Associate.scanOutBook("6907", "1400082773");
		Associate.scanOutBook("8339", "9781501165320");
		while (go() == false) {}
		
		//Member requesting checkout of non - available book
		System.out.println("\n\n\nMember requesting checkout of non-available book:");
		System.out.println("8339 requests checkout on: 0439064872");
		Associate.scanOutBook("6528", "0439064872");
		while (go() == false) {}
		
		
		
		//request holds
		System.out.println("\n\n\nMember requesting holds on non-availble book:");
		Member.requestHold("0439064872", "8339");
		Member.requestHold("9781501165320", "8339");
		Member.requestHold("0439064872", "6528");
		while (go() == false) {}
		
		
		
		//scan held books back in
		System.out.println("\n\n\n");
		Member.returnToDropbox("5693", "0439064872");
		while (go() == false) {}
		System.out.println("Associate scans from dropbox");
		Associate.scanDropBox();
		while (go() == false) {}
		
		///////member - report lost - request renewal
		System.out.println("\n\n\n- Member reporting book lost");
		Member.reportLost("9780345803481", "5693");
		while (go() == false) {}
		
		
		
		System.out.println("\n\n\n- Member requesting renewal");
		Member.requestRenewal("0743233212", "5693");
		while (go() == false) {}
		
		
		System.out.println("\n\n\n- Member requesting renewal of new release book");
		Member.requestRenewal("9781501175565", "5693");
		while (go() == false) {}
		
		
		
		///////member retrieving displaying info (checkouts + holds)
		System.out.println("\n\n\n- Member showing their info: ");
		memberInfo.main(new String[] {"5693"});
		while (go() == false) {}
		
		
		
		///////Manger applying charges
		System.out.println("\n\n\n- Manager applying charges: ");
		do{
			System.out.println("Manager.applyCharges(): ");
			long t= System.currentTimeMillis();
			long end = t+1000;
			while(System.currentTimeMillis() < end) {
			  Manager.applyCharges();
			  Thread.sleep(1);
			}
		}while (go() == false);

		///////member retrieving displaying info (checkouts + holds)
		System.out.println("\n\n\n- Member showing their info: ");
		memberInfo.main(new String[] {"5693"});
		while (go() == false) {}
		
		
		///////Member make payment
		System.out.println("\n\n\n- Member making payment: ");
		do{
			System.out.println("Manager.makePayment(10,5693): ");
			double rem = Member.makePayment(10,"5693");
			Manager.applyCharges();
			System.out.println(rem);
		}while (go() == false);

		
		
		////////logout
		System.out.println("\n\n\nLogout all previosuly logged in");
		login_logout.logout();
	}
	
	static boolean go() {
		System.out.print("::");
		String input;
		Scanner scan = new Scanner(System.in);
		input = scan.nextLine();
		if (input.isEmpty())
			return true;
		else
			return false;
	}
}
