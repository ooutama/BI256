package me.outama.BI256.main;

import java.util.ArrayList;
import java.util.List;

import me.outama.BI256.main.exceptions.DivisionByZeroException;
import me.outama.BI256.main.exceptions.OverflowException;

public class BI256
{
	
	/**
	 * The number represented in a list
	 * @var List<Integer>
	 */
	private List<Integer> digits = new ArrayList<Integer>();
	
	/**
	 * True if number < 0 false otherwise.
	 * @var boolean
	 */
	private boolean signed = false;
	
	/**
	 * The length of the number
	 * E.x.: length of 343 is 3
	 * @var int
	 */
	private int length = 0;
	
	/**
	 * The mininum value that a number can take (-2.pow(255))
	 *
	 * @var String
	 */
	final private static String MIN_VALUE = "-57896044618658097711785492504343953926634992332820282019728792003956564819968";
	
	/**
	 * The biggest value that a number can be (2.pow(255) - 1)
	 * E.x.: length of 343 is 3
	 * @var int
	 */
	final private static String MAX_VALUE = "57896044618658097711785492504343953926634992332820282019728792003956564819967";
	
	/**
	 * The constructor using long number
	 *
	 * @param Long
	 * @throws Exception
	 */
	public BI256(Long number) throws Exception
	{
		// Check if the number is signed
		if (number < 0) {
			this.signed = true;
			number *= -1;
		}
		
		fillInDigitsFromString(number.toString());
	}
	
	/**
	 * The constructor using long number
	 *
	 * @param Integer
	 * @throws Exception
	 */
	public BI256(Integer number) throws Exception
	{
		if (number < 0) {
			this.signed = true;
			number *= -1;
		}
		
		fillInDigitsFromString(number.toString());
	}
	
	/**
	 * The constructor using a sign and list of digits
	 *
	 * @param boolean sign
	 * @param List<Integer> digits
	 * @throws Exception
	 */
	public BI256(boolean signed, List<Integer> digits) throws Exception
	{
		this.checkOverflow(digits.toString());

		this.digits = digits;
		this.signed = signed;
		this.length = digits.size();

	}
	
	/**
	 * The constructor using a sign, number as a string
	 *
	 * @param boolean sign
	 * @param String number
	 * @throws Exception
	 */
	public BI256(boolean signed, String value) throws Exception
	{	
		this.signed = signed;
		
		fillInDigitsFromString(value);
	}
	
	/**
	 * The constructor using number as a string
	 *
	 * @param String number
	 * @throws Exception
	 */
	public BI256(String value) throws Exception
	{
		// Check if the number is signed
		// In other words, check if the string starts with a dash '-'
		if (value.charAt(0) == '-') {		
			this.signed = true;
			value = value.substring(1, value.length());
		}
		
		fillInDigitsFromString(value);
	}

	/**
	 * Addition of two BI256 numbers
	 *
	 * @param BI256 number
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 add(BI256 number) throws Exception
	{	
		//////////////////////////////////////////////////////////////////////////////
		// Check if one of the numbers is signed and return the subtraction of them.
		//////////////////////////////////////////////////////////////////////////////
		if (number.signed && !this.signed) {
			return this.subtract(number.negate());
		}
		if (!number.signed && this.signed) {
			return number.subtract(this.negate());
		}
		
		int carry = 0;
		
		// Length of the numbers 
		int num1Len = this.length;
		int num2Len = number.length;

		// Save result as a StringBuilder object
		StringBuilder result = new StringBuilder();
			
		// Loop for the max of the two lengths
		for (int i = 0; i < Math.max(num1Len, num2Len); i++) {
			// Check if still there is a digit in the list of number 1 digits
			// If no, affect 0 instead.
			int sum = (num1Len <= i) ? 0 : this.digits.get(i);
			// Same here but for number 2
			sum += (num2Len <= i) ? 0 : number.digits.get(i);
			
			// Add 1 if the carry isn't null
			if (carry == 1) {
				sum++;
				carry = 0;
			}
			
			// If the sum is greater than 10
			if (sum >= 10) {
				// Subtract 10
				sum -= 10;
				// Make carry equal to
				carry = 1;
			}
			
			// Add the number to the list
			result.append(sum);
		}
		
		//  Check if the carry isn't null
		if (carry == 1) {
			// Append the carry to the result
			result.append(carry);
		}
		
		// Check if both numbers are signed. If so, then the result is signed too.
		boolean signed = number.signed && this.signed;

		// 
		BI256 resultAsBI256 = new BI256(signed, result.reverse().toString());
		return resultAsBI256;
	}
	
	/**
	 * Subtraction of two BI256 numbers
	 *
	 * @param BI256 number
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 subtract(BI256 number) throws Exception
	{
		// Check if the two numbers are equal 
		if (this.isEqualTo(number))
			// If so, it returns zero
			return new BI256(0);
		
		////////////////////////////////////////////////////////////////////////////
		// Analyze all the sign possibilities
		////////////////////////////////////////////////////////////////////////////
		// -x - -y ==> -x + y ==> y - x
		if (this.signed && number.signed) {
			return number.negate().subtract(this.negate());
		}
		// -x - y ==> -(x + y)
		if(this.signed && !number.signed) {
			return this.negate().add(number).negate();
		}
		// x - -y ==> x + y
		if (!this.signed && number.signed) {
			return this.add(number.negate());
		}
		
		// The lengths of numbers
		int num1Len = this.length;
		int num2Len = number.length;
		
		// Conditions to avoid a subtraction of smaller number and big number.
		if (this.isSmallerThan(number)) {
			return new BI256(true, primarySubtract(number, this));
		}
		
		// Switch the numbers 
		return new BI256(false, primarySubtract(this, number));
	}
	
	/**
	 * Check if this is greater than a number
	 *
	 * @param BI256 number
	 * @return boolean
	 */
	public boolean isGreaterThan(BI256 number)
	{		
		return BI256.isGreaterThan(this, number);
	}
	
	/**
	 * Check if this is equal to a number
	 *
	 * @param BI256 number
	 * @return boolean
	 */
	public boolean isEqualTo(BI256 number)
	{		
		return BI256.isEqualTo(this, number);
	}
	
	/**
	 * Check if this is smaller than a number
	 *
	 * @param BI256 number
	 * @return boolean
	 */
	public boolean isSmallerThan(BI256 number)
	{		
		return BI256.isSmallerThan(this, number);
	}
	
	/**
	 * Multiplication of two numbers
	 *
	 * @param BI256 number
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 multiply(BI256 number) throws Exception
	{	
		// Define zero
		BI256 zero = new BI256(0);
		
		// Check if one of the number is equal to zero
		if (number.isEqualTo(zero) || this.isEqualTo(zero)) {
			// if so, it returns zero
			return zero;
		}
		
		// Lengths
		int num1Len = this.length;
		int num2Len = number.length;
		
		// Definition of the result
		BI256 result = zero;
		
		// Loop the first number
		for(int i = 0; i < num1Len; i++) {
			int carry = 0;
			// The result of the step i
			StringBuilder resultI = new StringBuilder();
			
			// Loop the second number
			for (int j=0; j < num2Len; j++) {
				// Multiplication of i digit of number 1 and j digit of number 2
				Integer multi = this.digits.get(i) * number.digits.get(j);

				// Add the carry to multiplication
				multi += carry;
				
				// Make the carry equal to zero after we added it to the multi
				carry = 0;
				
				// Check if the multi is greater than 10
				if(multi >= 10) {
					// Add the floor of the division to the carry
					carry = (int) Math.floor(multi / 10);
					// And the rest of division to multi
					multi = (int) (multi % 10);
				}
				
				// Append the result to resultI
				resultI.append(multi);
			}
			
			// Check if the carry is greater than 0
			if (carry > 0) {
				// If so, add it to there resultI
				resultI.append(carry);
			}

			// Reverse the resultI
			// So tha we can add the zeros after this.
			resultI = resultI.reverse();
			
			// Add the zeros to the resultI
			// The zeros is the starts or points that we use in the hand method
			// Ex. 564** ==> 56400
			for (int k=1; k<=i; k++) {
				resultI.append(0);
			}

			// Add the resultI to general result
			result = result.add(new BI256(false, resultI.toString()));
		}
		
		// Check if one of the numbers is signed
		// If so then the result is signed
		result.signed = this.signed ^ number.signed;
		
		return result;
	}
	

	/**
	 * Division of two numbers
	 *
	 * @param BI256 number
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 divide(BI256 number) throws Exception
	{
		// Define zero and one
		BI256 zero = new BI256(0);
		BI256 one = new BI256(1);
		
		// Define the abs of this and number as a and b
		BI256 a = !this.signed ? this : this.negate();
		BI256 b = !number.signed ? number : number.negate();
		
		// Check if the b is equal to zero
		if (b.isEqualTo(zero)) {
			// If so, an exception will be thrown
			throw new DivisionByZeroException();
		}
		
		// Check if a is equal to zero
		if (a.isEqualTo(zero)) {
			// 
			return zero;
		}
		
		// Check if a is smaller than b
		if (a.isSmallerThan(b)) {
			// If so, it will throw a zero
			return zero;
		}
		
		// If a is equal to b
		if (a.isEqualTo(b)) {
			// Check the sign
			BI256 signedOne = new BI256(this.signed ^ number.signed, "1");
			// And return 1 Or -1 depending on the sign
			return signedOne;
		}
		
		if (b.isEqualTo(one)) {
			 BI256 signedOne = new BI256(this.signed ^ number.signed, this.digits);
			 return signedOne;
		}

		// Define the result
		BI256 result = new BI256(1);
		// difference = a - b
		BI256 difference = a.subtract(b);
		
		if (difference.isEqualTo(zero)) {
			return result;
		} 
		else if (difference.isSmallerThan(zero)) {
			return zero;
		}

		// Add the difference divided by b to result
		result = result.add(difference.divide(b));

		// Check the sign 
		result.signed = this.signed ^ number.signed;

		return result;
	}
	
	/**
	 * Modulo
	 *
	 * @param BI256 number
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 mod(BI256 number) throws Exception
	{
		// REST = THIS % NUMBER ==> REST = THIS - NUMBER*(THIS/NUMBER)
        return this.subtract(this.divide(number).multiply(number));
	}
	
	/**
	 * Add modulo
	 *
	 * @param BI256 a
	 * @param BI256 b
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 addMod(BI256 a, BI256 b) throws Exception
	{
		return this.add(a).mod(b);
	}
	
	/**
	 * Subtract modulo
	 *
	 * @param BI256 a
	 * @param BI256 b
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 subtractMod(BI256 a, BI256 b) throws Exception
	{
		return this.subtract(a).mod(b);
	}
	
	/**
	 * Multiply modulo
	 *
	 * @param BI256 a
	 * @param BI256 b
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 multiplyMod(BI256 a, BI256 b) throws Exception
	{
		return this.multiply(a).mod(b);
	}
	
	/**
	 * Divide modulo
	 *
	 * @param BI256 a
	 * @param BI256 b
	 * @return BI256
	 * @throws Exception
	 */
	public BI256 divideMod(BI256 a, BI256 b) throws Exception
	{
		return this.divide(a).mod(b);
	}
	
	/**
	 * Convert the object to a formatted string
	 *
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String toString()
	{
		// Save the digits in a StringBuilder
		StringBuilder number = new StringBuilder();
		
		// Append each digit to number
		for (int i=0; i<this.length; i++) {
			number.append(this.digits.get(i));
		}
		
		// Check if number is signed
		if (this.signed)
			// If so append dash to number
			number.append('-');
		
		return  number.reverse().toString();
	}
	
	/**
	 * Fill in the digits using string
	 *
	 * @param String number
	 * @return void
	 * @throws Exception
	 */
	private void fillInDigitsFromString(String value) throws Exception
	{
		this.checkOverflow(value);

		int length = value.length();
		
		// Check if the length is bigger than 1 (to avoid 0)
		if(length > 1) {
			// Remove all the zeros from the start 00003423 ==> 3423
			for (int i = 0; i < length; i++) {
				if ( value.charAt(i) != '0') {
					break;
				}
				value = value.substring(1, value.length());
				i--;
				length--;
			}
		}
		
		// Fill in the digits list
		for (int i = length - 1; i >= 0 ; i--) {
			int digit = Integer.parseInt(new Character(value.charAt(i)).toString()); // base 10
			
			this.digits.add(digit);
		}
		
		// Digits length
		this.length = this.digits.size();
	}

	/**
	 * Check if we can represent number on 256 bits.
	 *
	 * @return void
	 * @throws Exception
	 */
	private void checkOverflow(String number) throws Exception
	{
		boolean signed = this.signed;

		int length = BI256.MAX_VALUE.length();
		int numberLength = number.length();
		
		if (numberLength < length)
			return;
		
		if (numberLength > length)
			throw new OverflowException();

		if (signed) {
			for (int i = 0; i < length ; i++) {
				int a = Character.getNumericValue(BI256.MIN_VALUE.charAt(i+1));
				int b = Character.getNumericValue(number.charAt(i));

				if (b > a)
					throw new OverflowException();
				if (b < a)
					return;
			}
		} else {
			for (int i = 0; i < length ; i++) {
				int a = Character.getNumericValue(BI256.MAX_VALUE.charAt(i));
				int b = Character.getNumericValue(number.charAt(i));
				
				if (b > a)
					throw new OverflowException();
				if (b < a)
					return;
			}
		}
	}

	/**
	 * Negate of the number
	 *
	 * @return BI256 number
	 * @throws Exception
	 */
	private BI256 negate()
	{
		try {
			return new BI256(!this.signed, this.digits);
		} catch(Exception error) {
			System.out.println("Error while trying to generate the negate.");
			error.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Check if a is greater than b
	 *
	 * @param BI256 number1
	 * @param BI256 number2
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isGreaterThan(BI256 number1, BI256 number2)
	{
		// If one of the numbers is signed
		if (number1.signed ^ number2.signed)
			// Check if number 1 is signed -> return false
			// Otherwise (that means number2 is signed) return true
			return ! number1.signed;
		
		// Check if they both are singed
		if (number1.signed && number2.signed)
			// Return the negate result
			return ! BI256.isGreaterThan(number1.negate(), number2.negate()) && !BI256.isEqualTo(number1.negate(), number2.negate());
		
		// Lengths
		int num1Len = number1.length;
		int num2Len = number2.length;
		
		// check if length of number 1 is greater than number 2
		if (num1Len > num2Len)
			// if so, return true 
			return true;
		// check if length of number 1 is less than number 2
		if (num1Len < num2Len)
			// if so, return false
			return false;
		
		// Let's loop the digits from the back
		// In other words, from the start of the number
		for (int i = num1Len-1; i >= 0; i--) {
			if (number1.digits.get(i) > number2.digits.get(i)) {
				return true;
			}
			
			if (number1.digits.get(i) < number2.digits.get(i))
				return false;
		}
		return false;
	}
	
	/**
	 * Check if a is equal to b
	 *
	 * @param BI256 number1
	 * @param BI256 number2
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isEqualTo(BI256 number1, BI256 number2) 
	{
		// If one of the numbers is signed
		if (number1.signed ^ number2.signed)
			// It returns false because they don't have the same sign
			return false;
				
		int num1Len = number1.length;
		int num2Len = number2.length;
		
		// check numbers length
        if (num1Len != num2Len) {
            return false;
        }
        
        // Loop the numbers
        for (int i = num1Len-1; i >= 0; i--) {
        	// Check if one digit is different than the other 
        	if (number1.digits.get(i) != number2.digits.get(i))
        		// if so return false 
				return false;
        }
        
        return true;
    }
	
	/**
	 * Check if a is smaller than b
	 *
	 * @param BI256 number1
	 * @param BI256 number2
	 * @return boolean
	 * @throws Exception
	 */
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
	
	/**
	 * Primatry subtract
	 *
	 * @param BI256 number1
	 * @param BI256 number2
	 * @return boolean
	 * @throws Exception
	 */
	private static String primarySubtract(BI256 number1, BI256 number2)
	{
		// Same as addition
		// Just here we are sure that number 1 is greater tha number 2
		int carry = 0;
		
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
			
			int difference = digit1 - digit2;
			result.append(difference);
		}

		return result.reverse().toString();
	}

}
