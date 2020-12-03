package me.outama.BI256.main.exceptions;

public class OverflowException extends Exception 
{
	public OverflowException()
	{
		super("Can't represente the number in 256 bits.");
	}
}
