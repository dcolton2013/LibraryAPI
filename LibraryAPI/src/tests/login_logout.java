package tests;

import models.*;

public class login_logout {
	static String manager,associate,member = "";
	public static Manager testManager(String[] args){
		//Login manage;
		manager = args[0];
		return Library.loginManager(args[0], args[1]);
	}
	
	public static Associate testAssociate(String[] args){
		//Login manager
		associate = args[0];
		return Library.loginAssociate(args[0], args[1]);
	}
	
	public static Member testMember(String[] args){
		//Login manager
		member = args[0];
		return Library.loginMember(args[0], args[1]);
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
