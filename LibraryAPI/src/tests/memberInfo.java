package tests;

import models.Member;

public class memberInfo {
	public static void main (String []args ) {
		Member.displayCheckoutInfo(args[0]);
		Member.displayHolds(args[0]);
	}
}
