package ma.ensa.java.tests;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ma.ensa.java.BI256;

public class BI256Test 
{
	
	private Integer random() 
	{
		Random rand = new Random(); 
		int x = rand.nextInt(Integer.MAX_VALUE);
		return x > 0 ? x : -x;
	}
	
	@Test
	public void test_it_can_convert_a_negative_bi256_to_a_string() throws Exception
	{
		BI256 number = new BI256(true, "4345332480", 10);
		
		assertEquals(number.toString(), "-4345332480");
	}
	
	@Test
	public void test_it_can_convert_a_bi256_to_a_string() throws Exception
	{
		BI256 number = new BI256("303485998230283498");
		
		assertEquals(number.toString(), "303485998230283498");
	}
	
	@Test
	public void test_it_can_add_two_positive_big256_numbers() throws Exception
	{
		Integer n1 = random();
		Integer n2 = random();
		
		BI256 number1 = new BI256(false, n1.toString());
		BI256 number2 = new BI256(false, n2.toString());
		
		Long sum = (long) (n1+n2);
		
		assertEquals(sum.toString(), number1.add(number2).toString());
	}
	
	@Test
	public void test_it_can_add_one_negative_big256_number_and_a_positive_big256() throws Exception
	{
		Integer n1 = random();
		Integer n2 = random();
		
		BI256 number1 = new BI256(true, n1.toString());
		BI256 number2 = new BI256(false, n2.toString());
		
		Long sum = (long) (n1+n2);
		
		assertEquals(sum.toString(), number1.add(number2).toString());
	}
	
	@Test
	public void test_it_can_add_one_positive_big256_number_and_a_negative_big256() throws Exception
	{
		Integer n1 = random();
		Integer n2 = random();
		
		BI256 number1 = new BI256(false, n1.toString());
		BI256 number2 = new BI256(true, n2.toString());
		
		Long sum = (long) (n1+n2);
		
		assertEquals(sum.toString(), number1.add(number2).toString());
	}
	
	@Test
	public void test_it_can_add_two_negative_numbers() throws Exception
	{
		Integer n1 = random();
		Integer n2 = random();
		
		BI256 number1 = new BI256(true, n1.toString());
		BI256 number2 = new BI256(true, n2.toString());
		
		Long sum = (long) (n1+n2);
		
		assertEquals(sum.toString(), number1.add(number2).toString());
	}
	
	@Test
	public void test_it_can_substract_two_positive_numbers() throws Exception
	{
		BI256 number1 = new BI256(false, "1908");
		BI256 number2 = new BI256(false, "2495");
		
		Integer sum = 1908-2495;
		
		assertEquals(sum.toString(), number1.substract(number2).toString());
	}
	
	@Test
	public void test_it_can_substract_one_positive_and_one_negative_number() throws Exception
	{
		BI256 number1 = new BI256(false, "1908");
		BI256 number2 = new BI256(true, "2495");
		
		Integer sum = 1908- (-2495);
		
		assertEquals(sum.toString(), number1.substract(number2).toString());
	}
	
	@Test
	public void test_it_can_substract_one_negative_number_and_one_positive_number() throws Exception
	{
		BI256 number1 = new BI256(true, "1908");
		BI256 number2 = new BI256(false, "2495");
		
		Integer sum = -1908 - 2495;
		
		assertEquals(sum.toString(), number1.substract(number2).toString());
	}
	
	@Test
	public void test_it_can_substract_two_negative_numbers() throws Exception
	{
		BI256 number1 = new BI256(true, "1908");
		BI256 number2 = new BI256(true, "2495");
		
		Integer sum = -1908 - (-2495);
		
		assertEquals(sum.toString(), number1.substract(number2).toString());
	}
	
	@Test
	public void test_it_multiply_two_positive_numbers() throws Exception
	{
		BI256 number1 = new BI256(false, "1190284");
		BI256 number2 = new BI256(false, "3194820");
		
		Long multi = new Long(1190284) *  new Long(3194820);
		
		System.out.println(multi);
		System.out.println(number1.multiply(number2).toString());
		
		
		assertEquals(multi.toString(), number1.multiply(number2).toString());
	}


}
