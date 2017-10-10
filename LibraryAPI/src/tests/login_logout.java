package tests;

import models.Library;

public class login_logout {
	
	public static void testManager(String[] args){
		//Login manager
		Library.loginManager(args[0], args[1]);
		Library.logoutManager();
	}
	public static void testAssociate(String[] args){
		//Login manager
		Library.loginAssociate(args[0], args[1]);
		Library.logoutAssociate();
	}
	
}
