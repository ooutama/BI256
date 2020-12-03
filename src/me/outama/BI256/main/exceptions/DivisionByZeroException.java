package me.outama.BI256.main.exceptions;

public class DivisionByZeroException extends Exception 
{
	public DivisionByZeroException()
	{
		super("Can't be divided by Zero.");
	}
}
