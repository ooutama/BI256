package ma.ensa.java;

import java.util.ArrayList;
import java.util.List;

public class BI256
{
	
	private List<Integer> digits = new ArrayList<Integer>();
	
	private boolean signed = false;
	
	private int length = 0;
	
	public BI256(Long number) throws Exception
	{
		if (number < 0) {
			this.signed = true;
			number *= -1;
		}
		
		fillInDigitsFromString(number.toString(), 10);
	}
	
	public BI256(Integer number) throws Exception
	{
		if (number < 0) {
			this.signed = true;
			number *= -1;
		}
		
		fillInDigitsFromString(number.toString(), 10);
	}
	
	public BI256(boolean signed, List<Integer> digits)
	{
		this.digits = digits;
		this.signed = signed;
		this.length = digits.size();
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
	
	private void fillInDigitsFromString(String value, int radix) throws Exception
	{
		for (int i = value.length() - 1; i >= 0 ; i--)
		{
			int digit = Integer.parseInt(new Character(value.charAt(i)).toString(), radix); // base 10
			
			this.digits.add(digit);
		}
		
		this.length = this.digits.size();
	}

	public BI256 add(BI256 bigNumber) throws Exception
	{	
		// 9 + -3 => 9 - 3
		if (bigNumber.signed && !this.signed) {
			return this.subtract(bigNumber.negate());
		}
		
		// -4 + 3 => 3 - 4
		if (!bigNumber.signed && this.signed) {
			return bigNumber.subtract(this.negate());
		}
		
		int carry = 0;
		
		int num1Len = this.length;
		int num2Len = bigNumber.length;

		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < Math.max(num1Len, num2Len); i++) {
			// Check if the num1 length is less than the max
			int sum = (num1Len <= i) ? 0 : this.digits.get(i);
			
			// Same for num2
			sum += (num2Len <= i) ? 0 : bigNumber.digits.get(i);
			
			// Add 1 if the carry is 1
			if (carry == 1) {
				sum++;
				carry = 0;
			}
			
			// If the sum is greater than 10
			if (sum >= 10) {
				// Subtract 10
				sum -= 10;
				// Make carry 1
				carry = 1;
			}
			
			// Add the number to the list
			 result.append(sum);
		}
		
		if (carry == 1) {
			result.append(carry);
		}
		
		boolean signed = bigNumber.signed && this.signed;
		BI256 SumBigInteger = new BI256(signed, result.reverse().toString(), 10);
		return SumBigInteger;
	}
	
	public BI256 subtract(BI256 bigNumber) throws Exception
	{
		if (this.isEqualTo(bigNumber))
			return new BI256(0);
		
		/*
		 (x) - (y)
		 (-x) - (y) => -(x+y)
		 (x) - (-y) => x + y
		 (-x) - (-y) => -x + y => y - x
		*/
		// -8 - -9 ==> -8+9 ==> 9-8
		if (this.signed && bigNumber.signed) {
			return bigNumber.negate().subtract(this.negate());
		}
		// -8 - 9 ==> -(8+9)
		if(this.signed && !bigNumber.signed) {
			return this.negate().add(bigNumber).negate();
		}
		
		// 8 - -9 => 8+9
		if (!this.signed && bigNumber.signed) {
			return this.add(bigNumber.negate());
		}
		
		int num1Len = this.length;
		int num2Len = bigNumber.length;
		
		if (num1Len < num2Len) {
			return new BI256(true, primarySubtract(bigNumber, this));
		}
		
		if (!this.isGreaterThan(bigNumber)) {
			return new BI256(true, primarySubtract(bigNumber, this));
		}
		
		return new BI256(false, primarySubtract(this, bigNumber));
	}
	
	public static boolean isGreaterThan(BI256 number1, BI256 number2)
	{
		// If one of the numbers is signed
		if (number1.signed ^ number2.signed)
			// Check if number 1 is signed and return false
			// Otherwise return true (number2 is signed)
			return ! number1.signed;
		
		if (number1.signed && number2.signed)
			return ! BI256.isGreaterThan(number1.negate(), number2.negate()) && !BI256.isEqualTo(number1.negate(), number2.negate());
		
		int num1Len = number1.length;
		int num2Len = number2.length;
		
		if (num1Len > num2Len)
			return true;
		
		if (num1Len < num2Len)
			return false;
		
		for (int i=num1Len-1; i>=0; i--) {
			if (number1.digits.get(i) > number2.digits.get(i)) {
				return true;
			}
			
			if (number1.digits.get(i) < number2.digits.get(i))
				return false;
		}
		return false;
	}
	
	public static boolean isEqualTo(BI256 number1, BI256 number2) 
	{
		// If one of the numbers is signed
		if (number1.signed ^ number2.signed)
			// It returns false because they don't have the same sign
			return false;
				
		int num1Len = number1.length;
		int num2Len = number2.length;
		
        if (num1Len != num2Len) {
            return false;
        }
        
        for (int i=num1Len-1; i>=0; i--) {
        	if (number1.digits.get(i) != number2.digits.get(i))
				return false;
        }
        
        return true;
    }
	
	public static boolean isSmallerThan(BI256 number1, BI256 number2)
	{
		// If one of the numbers is signed
		if (number1.signed ^ number2.signed)
			// Check if number 1 is signed and return true (because it's the smaller)
			// Otherwise return false (number2 is signed)
			return number1.signed;

		if (number1.signed && number2.signed)
			return ! BI256.isSmallerThan(number1.negate(), number2.negate()) && !BI256.isEqualTo(number1.negate(), number2.negate());
		
		
		int num1Len = number1.length;
		int num2Len = number2.length;
		
		if (num1Len > num2Len)
			return false;
		
		if (num1Len < num2Len)
			return true;
		
		for (int i=num1Len-1; i>=0; i--) {
			if (number1.digits.get(i) > number2.digits.get(i)) {
				return false;
			}
			
			if (number1.digits.get(i) < number2.digits.get(i))
				return true;
		}
		return false;
	}
	
	public boolean isGreaterThan(BI256 number)
	{		
		return BI256.isGreaterThan(this, number);
	}
	
	public boolean isEqualTo(BI256 number)
	{		
		return BI256.isEqualTo(this, number);
	}
	
	public boolean isSmallerThan(BI256 number)
	{		
		return BI256.isSmallerThan(this, number);
	}
	
	private static String primarySubtract(BI256 number1, BI256 number2)
	{
		int carry = 0;
		boolean resultFilledIn = false;
		
		int num1Len = number1.digits.size();
		int num2Len = number2.digits.size();

		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < num1Len; i++) {
			int digit1 = number1.digits.get(i);
			int digit2 = (num2Len <= i) ? 0 : number2.digits.get(i);
			
			// Add 1 if the carry is 1
			if (carry == 1) {
				digit2++;
				carry = 0;
			}
			
			if (digit1 < digit2) {
				digit1 += 10;
				carry = 1;
			}
			
			// Add the number to the list
			int difference = digit1 - digit2;
			if (i == num1Len-1 && difference == 0 && resultFilledIn)
				continue;
			
			resultFilledIn = true;
			result.append(difference);
		}

		return result.reverse().toString();
	}
	
	// multiplier
	public BI256 multiply(BI256 bigNumber) throws Exception
	{	
		int num1Len = this.digits.size();
		int num2Len = bigNumber.digits.size();
		
		BI256 result = null;
		
		for(int i=0; i <num1Len; i++) {
			int carry = 0;
			StringBuilder p = new StringBuilder();
			
			for (int j=0; j < num2Len; j++) {
				Integer multi = this.digits.get(i) * bigNumber.digits.get(j);
				multi += carry;
				
				carry = 0;
				
				if(multi >= 10) {
					carry = (int) Math.floor(multi / 10);
					multi = (int) (multi % 10);
				}
				
				p.append(multi);
			}
			
			if (carry > 0) {
				p.append(carry);
			}
			
			p = p.reverse();
			
			for (int k=1; k<=i; k++) {
				p.append(0);
			}
			
			if (result == null) {
				result = new BI256(false, p.toString());
				continue;
			}
			
			result = result.add(new BI256(false, p.toString()));
			
		}
		
		result.signed = this.signed ^ bigNumber.signed;
		
		return result;
	}
	
	public BI256 divide(BI256 divisor) throws Exception
	{
		BI256 zero = new BI256(0);
		BI256 one = new BI256(1);
		
		BI256 a = !this.signed ? this : this.negate();
		BI256 b = !divisor.signed ? divisor : divisor.negate();
		
		if (b.isEqualTo(zero)) {
			throw new Exception("Division by zero.");
		}
		
		if (a.isEqualTo(zero)) {
			return zero;
		}
		
		if (a.isSmallerThan(b)) {
			return zero;
		}
		
		if (a.isEqualTo(b)) {
			BI256 signedOne = new BI256(this.signed ^ divisor.signed, "1");
			return signedOne;
		}
		
		if (b.isEqualTo(one)) {
			BI256 signedOne = new BI256(this.signed ^ divisor.signed, this.digits);
			return signedOne;
		}

		BI256 result = new BI256(1);
		BI256 difference = a.subtract(b);

		if (difference.isGreaterThan(zero) && (difference.isSmallerThan(one) || difference.isEqualTo(one))) {
			return result;
		} else if (difference.isSmallerThan(zero)) {
			return zero;
		}

		result = result.add(difference.divide(b));
		result.signed = this.signed ^ divisor.signed;

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
		StringBuilder number = new StringBuilder();
		
		for (int i=0; i<this.length; i++) {
			number.append(this.digits.get(i));
		}
		
		if (this.signed)
			number.append('-');
		
		return  number.reverse().toString();
	}
	
	public static void main(String[] args)
	{
		
		try {
			BI256 m1 = new BI256(false, "0", 10);
			BI256 m2 = new BI256(false, "18", 10);
			System.out.println(m1.divide(m2));
			//System.out.println(m1.subtract(m2));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
