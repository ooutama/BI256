# BI256
> It's a class that handles large numbers that can be represented in 256 bits.

## Class
### Constructors
> The constructor check automaticly if the number can be represented in 256 bits or not. In other words, if the number can not be represented in 256 bits (it is greater than 2^255-1 or it is smaller than -2^255) an OverflowException will be thrown.
1. Create a BI256 object using long|int number
```java
// You can use both int and Integer
int number = 123;
new BI256(number);

// Same here, you can use both long and Long
Long longNumber = 12345;
new BI256(longNumber);
```
2. You can create a BI256 using a String
```java
new BI256("123456789");

// For signed numbers, you can use:
new BI256("-987654321");

// Or you can use
boolean signed = true;
new BI256(signed, "987654321");
```
**P.S:Floating numbers are not allowed.**

### Methods 
1. Addition of two BI256 numbers
```java
BI256 a = new BI256("123456789");
BI256 b = new BI256("987654321");

// c = a + b
BI256 c = a.add(b);
```
2. Subtraction
```java
BI256 a = new BI256("123456789");
BI256 b = new BI256("987654321");

// c = a - b
BI256 c = a.subtract(b);
```
3. Multiplication
```java
BI256 a = new BI256("123456789");
BI256 b = new BI256("987654321");

// c = a - b
BI256 c = a.multiply(b);
```
4. Division
```java
BI256 a = new BI256("987654321");
BI256 b = new BI256("123456789");

// c = a - b
BI256 c = a.divide(b);
```
**P.S.: The result is without precision. You need to use the modulo to know the rest of the division**
**Ex: 5/2 = 2**
5. Modulo
```java
BI256 a = new BI256("5");
BI256 b = new BI256("123456789");

// c = a % b = 5
BI256 c = a.mod(b);
```
6. Add modulo
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");
BI256 c = new BI256("2123312");

// d = (a + b) % c
BI256 d = a.addMod(b,c);
```
7. Subtract modulo
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");
BI256 c = new BI256("2123312");

// d = (a - b) % c
BI256 d = a.subtractMod(b,c);
```
8. Multiply modulo
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");
BI256 c = new BI256("2123312");

// d = (a * b) % c
BI256 d = a.multiplyMod(b,c);
```
9. Divide modulo
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");
BI256 c = new BI256("2123312");

// d = (a / b) % c
BI256 d = a.divideMod(b,c);
```
10. isSmallerThan
    - This method checks if the object is smaller than another BI256 number. 
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");

// d = false
boolean result = a.isSmallerThan(b);
```
11. isGreaterThan
    - This method checks if the object is greater than another BI256 number. 
```java
BI256 a = new BI256("98765432");
BI256 b = new BI256("123456789");

// d = true
boolean result = a.isGreaterThan(b);
```
11. isEqualTo
    - This method checks if the object is equal to another BI256 number. 
```java
BI256 a = new BI256("123456789");
BI256 b = new BI256("123456789");

// d = true
boolean result = a.isEqualTo(b);
```
