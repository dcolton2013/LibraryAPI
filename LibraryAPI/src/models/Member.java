package models;

import java.util.*;

public class Member {
	
	
	public static String generatePassword(){
		String password = UUID.randomUUID().toString();
		password.replace("-","");
		password = password.substring(0,8);
		return password;
		//return "pass";
	}
	
	public static String generateUsername(String s1, String s2){
		
		return s1.charAt(0) + s2;
	}
	
	public static String generateLibrarycode(){
		Random random = new Random();
		int code = random.nextInt(9000) + 1000;
		return "" + code;
	}
}
