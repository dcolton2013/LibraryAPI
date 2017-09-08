package models;
//may need to import book.java
import java.util.ArrayList;

public class Associate{
	private String password;
	private String username;
	private String name;
	private int id;

	//DS to keep up with Associate activity
	private ArrayList<String> activity;

	Associate(String name, int id){
		this.name=name;
		this.id=id;
		this.activity = new ArrayList<String>();
	}

	/*getters/setters of data field*/

/* Validate book, update availabilty*/
	public void scanInBook(Book book){
		
	}

	public void scanOutBook(Book book){
		
	}
/**/

	public String toString(){ return " ";}

	/* I thought about a check */
}
