package tests;

import models.Library;

public class login_logout {
	static String manager,associate,member = "";
	public static void testManager(String[] args){
		//Login manage;
		manager = args[0];
		Library.loginManager(args[0], args[1]);
	}
	
	public static void testAssociate(String[] args){
		//Login manager
		associate = args[0];
		Library.loginAssociate(args[0], args[1]);
	}
	
	public static void testMember(String[] args){
		//Login manager
		member = args[0];
		Library.loginMember(args[0], args[1]);
	}
	
	public static void logout() {
		System.out.println("logoutManager(man1): ");
		Library.logoutManager(manager);
		System.out.println("logoutAssociate(assoc1): ");
		Library.logoutAssociate(associate);
		System.out.println("logoutMember(sbloobob): ");
		Library.logoutMember(member);
	}
}
