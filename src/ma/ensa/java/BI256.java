package ma.ensa.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BI256
{
	
	private List<Integer> digits = new ArrayList<Integer>();
	
	private boolean signed = false;
	
	public BI256()
	{
		//
	}
	
	public BI256(boolean signed, List<Integer> digits)
	{
		this.digits = digits;
		this.signed = signed;
	}
	
	public BI256(boolean signed, String value, int radix) throws Exception
	{
		this.signed = signed;
		
		fillInDigitsFromString(value, radix);
		
		// Overflow verification here :)
	}
	
	public BI256(boolean signed, String value) throws Exception
	{	
		this.signed = signed;
		
		fillInDigitsFromString(value, 10);
		
		// Overflow verification here :)
	}
	
	public BI256(String value) throws Exception
	{	
		fillInDigitsFromString(value, 10);
		
		// Overflow verification here :)
	}
	
	private BI256 negate()
	{
		return new BI256(!this.signed, this.digits);
	}
	
	private void fillInDigitsFromString(String value, int radix)
	{
		for (int i = value.length() - 1; i >= 0 ; i--)
		{
			int digit = Integer.parseInt(new Character(value.charAt(i)).toString(), radix); // base 10
			
			this.digits.add(digit);
		}
	}

	public BI256 add(BI256 bigNumber) throws Exception
	{	
		// 9 + -3 => 9 - 3
		if (bigNumber.signed && !this.signed) {
			return this.substract(bigNumber.negate());
		}
		
		// -4 + 3 => 3 - 4
		if (!bigNumber.signed && this.signed) {
			return bigNumber.substract(this.negate());
		}
		
		int suffix = 0;
		
		int num1Len = this.digits.size();
		int num2Len = bigNumber.digits.size();

		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < Math.max(num1Len, num2Len); i++) {
			// Check if the num1 length is less than the max
			int sum = (num1Len <= i) ? 0 : this.digits.get(i);
			
			// Same for num2
			sum += (num2Len <= i) ? 0 : bigNumber.digits.get(i);
			
			// Add 1 if the suffix is 1
			if (suffix == 1) {
				sum++;
				suffix = 0;
			}
			
			// If the sum is greater than 10
			if (sum >= 10) {
				// Subtract 10
				sum -= 10;
				// Make suffix 1
				suffix = 1;
			}
			
			// Add the number to the list
			 result.append(sum);
		}
		
		if (suffix == 1) {
			result.append(1);
		}
		
		boolean signed = bigNumber.signed && this.signed;
		BI256 SumBigInteger = new BI256(signed, result.reverse().toString(), 10);
		return SumBigInteger;
	}
	
	public BI256 substract(BI256 bigNumber) throws Exception
	{
		/*
		 (x) - (y)
		 (-x) - (y) => -(x+y)
		 (x) - (-y) => x + y
		 (-x) - (-y) => -x + y => y - x
		*/
		// -8 - -9 ==> -8+9 ==> 9-8
		if (this.signed && bigNumber.signed) {
			return bigNumber.negate().substract(this.negate());
		}
		// -8 - 9 ==> -(8+9)
		if(this.signed && !bigNumber.signed) {
			return this.negate().add(bigNumber).negate();
		}
		
		// 8 - -9 => 8+9
		if (!this.signed && bigNumber.signed) {
			return this.add(bigNumber.negate());
		}
		
		// 8-9
		int suffix = 0;
		
		int num1Len = this.digits.size();
		int num2Len = bigNumber.digits.size();
		
		if (num1Len < num2Len) {
			return new BI256(true, primarySubstract(bigNumber, this));
		}
		
		if (num1Len == num2Len && !isBiggerThan(this, bigNumber, num1Len)) {
			return new BI256(true, primarySubstract(bigNumber, this));
		}
		
		return new BI256(false, primarySubstract(this, bigNumber));
	}
	
	private boolean isBiggerThan(BI256 number1, BI256 number2, int length)
	{
		for (int i=length-1; i>=0; i--) {
			if (number1.digits.get(i) > number2.digits.get(i)) {
				return true;
			}
			
			if (number1.digits.get(i) < number2.digits.get(i))
				return false;
		}
		return true;
	}
	
	private static String primarySubstract(BI256 number1, BI256 number2)
	{
		int suffix = 0;
		
		int num1Len = number1.digits.size();
		int num2Len = number2.digits.size();

		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < num1Len; i++) {
			int digit1 = number1.digits.get(i);
			int digit2 = (num2Len <= i) ? 0 : number2.digits.get(i);
			
			// Add 1 if the suffix is 1
			if (suffix == 1) {
				digit2++;
				suffix = 0;
			}
			
			if (digit1 < digit2) {
				digit1 += 10;
				suffix = 1;
			}
			
			// Add the number to the list
			int difference = digit1 - digit2;
			if (i == num1Len-1 && difference == 0)
				continue;
			
			result.append(difference);
		}

		return result.reverse().toString();
	}
	
	public BI256 multiply(BI256 bigNumber) throws Exception
	{	
		int num1Len = this.digits.size();
		int num2Len = bigNumber.digits.size();
		
		BI256 result = null;
		
		for(int i=0; i <num1Len; i++) {
			int suffix = 0;
			StringBuilder p = new StringBuilder();
			
			for (int j=0; j < num2Len; j++) {
				Integer multi = this.digits.get(i) * bigNumber.digits.get(j);
				multi += suffix;
				
				suffix = 0;
				
				if(multi >= 10) {
					suffix = (int) Math.floor(multi / 10);
					multi = (int) (multi % 10);
				}
				
				p.append(multi);
			}
			
			if (suffix > 0) {
				p.append(suffix);
			}
			
			p = p.reverse();
			
			for (int k=1; k<=i; k++) {
				p.append(0);
			}
			
			if (result != null) {
				result = result.add(new BI256(false, p.toString()));
				continue;
			}
			
			result = new BI256(false, p.toString());
			
		}
		
		result.signed = this.signed ^ bigNumber.signed;
		
		return result;
	}
	
	public BI256 division(BI256 bigNumber) throws Exception
	{
		BI256 result = null;
		return result;
	}
	
	public BI256 addMod(BI256 a, BI256 b)
	{
		BI256 result = null;
		return result;
	}
	
	@Override
	public String toString()
	{
		StringBuilder myString = new StringBuilder()
			.append(
				digits.toString()
				// Replace all the commas and spaces
				.replaceAll("[,\\s]", "")
				//Replace all the [ (right brackets)
				.replaceAll("\\[", "")
				//Replace all the ]
				.replaceAll("\\]","")
			);
		
		if (this.signed)
			myString.append('-');
		
		return  myString.reverse().toString();
	}
	
	public static void main(String[] args)
	{
		//
		System.out.println(new Random().nextInt(Integer.MAX_VALUE));
		
		try {
			//8 - -9 => 8+9
			BI256 m1 = new BI256(true, "54321", 10);
			BI256 m2 = new BI256(false, "12345", 10);
			System.out.println(m1.multiply(m2));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
