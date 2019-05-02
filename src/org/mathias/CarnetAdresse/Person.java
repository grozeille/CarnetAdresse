package org.mathias.CarnetAdresse;

import java.io.Serializable;

/**
 * la classe personnes :)
 * @author mathias
 *
 */
public class Person implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String lastName;
	private String firstname;
	private String phone = ""; 
	
	public Person(String lastName, String firstname)
	{
		super();
		// TODO Auto-generated constructor stub
		this.lastName = lastName;
		this.firstname = firstname;
	}
	public Person()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getFirstname()
	{
		return firstname;
	}
	
	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String toString()
	{
		// TODO Auto-generated method stub
		return firstname+" "+lastName;
	}
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
}
