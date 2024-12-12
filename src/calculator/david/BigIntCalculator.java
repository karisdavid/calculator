package calculator.david;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Scanner;

public class BigIntCalculator {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Arbitrary Precision Integer Calculator!");
        System.out.println("Supported operations: +, -, *, /, %, ^, !, base, log, fractions");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("Enter calculation: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                processInput(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void processInput(String input) {
        // Preprocess input to add spaces around operators if needed
        input = input.replaceAll("!", " !");
    
        // Check if the input is a fraction operation
        if (input.contains("/") && (input.contains("+") || input.contains("-") || input.contains("*") || input.contains("/"))) {
            handleFractionArithmetic(input);
            return;
        }
    
        String[] parts = input.split(" ");
    
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid input format.");
        }
    
        if (parts.length == 2 && parts[1].equals("!")) {
            handleFactorial(parts);
            return;
        }
    
        if (parts[0].equals("log")) {
            handleLogarithm(parts);
            return;
        }
    
        if (input.contains(" base ") && input.contains(" to ")) {
            handleBaseConversion(parts);
            return;
        }
    
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid input format. Example: 2 + 2");
        }
    
        // Process normal arithmetic
        try {
            BigDecimal leftOperand = new BigDecimal(parts[0]);
            String operator = parts[1];
            BigDecimal rightOperand = new BigDecimal(parts[2]);
    
            switch (operator) {
                case "+":
                    System.out.println("Result: " + leftOperand.add(rightOperand));
                    break;
                case "-":
                    System.out.println("Result: " + leftOperand.subtract(rightOperand));
                    break;
                case "*":
                    System.out.println("Result: " + leftOperand.multiply(rightOperand));
                    break;
                case "/":
                    if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
                        System.out.println("Error: Division by zero is not allowed.");
                        break;
                    }
                    System.out.println("Result: " + leftOperand.divide(rightOperand, 2, RoundingMode.HALF_UP));
                    break;
                case "%":
                    if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
                        System.out.println("Error: Modulo by zero is not allowed.");
                        break;
                    }
                    System.out.println("Result: " + leftOperand.remainder(rightOperand));
                    break;
                case "^":
                    handleExponentiation(leftOperand.toBigInteger(), rightOperand.toBigInteger());
                    break;
                default:
                    System.out.println("Unsupported operator. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }

    // Handling Factorial: Example 5!
    private static void handleFactorial(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid input format for factorial. Example: 3!");
        }
    
        try {
            // Parse the integer for factorial calculation
            BigInteger number = new BigInteger(parts[0]);
            if (number.compareTo(BigInteger.ZERO) < 0) {
                System.out.println("Error: Factorial is not defined for negative numbers.");
                return;
            }
    
            // Compute factorial: Example 5!
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.TWO; i.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(i);
            }
    
            System.out.println("Result: " + result);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input for factorial. Only integers are allowed.");
        }
    }

   
   //Handling Exponential: Example 2 ^ 5 
    private static void handleExponentiation(BigInteger base, BigInteger exponent) {
        try {
            int exp = exponent.intValueExact();
            if (exp < 0) {
                System.out.println("Error: Negative exponents are not supported."); // 2 ^ -5
                return;
            }
            System.out.println("Result: " + base.pow(exp));
        } catch (ArithmeticException e) {
            System.out.println("Error: Exponent too large.");
        }
    }

    // Handling baseconversion : Example 5 base 10 to 2
    private static void handleBaseConversion(String[] parts) {
        if (parts.length != 5 || !parts[1].equals("base") || !parts[3].equals("to")) {
            throw new IllegalArgumentException("Invalid input format for base conversion. Example: 1010 base 2 to 10");
        }
    
        String number = parts[0];
        int fromBase = Integer.parseInt(parts[2]);
        int toBase = Integer.parseInt(parts[4]);
    
        BigInteger value = new BigInteger(number, fromBase);
        String result = value.toString(toBase);
    
        System.out.println("Result: " + result);
    }

    
    // Handling logarithm: Example you write log 2 1024 on the console
    private static void handleLogarithm(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid input format for logarithm. Example: log 2 1024");
        }
    
        BigDecimal base = new BigDecimal(parts[1]);
        BigDecimal number = new BigDecimal(parts[2]);
    
        if (number.compareTo(BigDecimal.ONE) <= 0 || base.compareTo(BigDecimal.ONE) <= 0) {
            System.out.println("Error: Logarithm is defined for values greater than 1.");
            return;
        }
    
        // Logarithm formula: log_b(a) = log_c(a) / log_c(b)
        // We use natural logarithms (log base e), so we calculate log(a) / log(b)
        BigDecimal logBase = new BigDecimal(Math.log(base.doubleValue())); // Natural log of base
        BigDecimal logNumber = new BigDecimal(Math.log(number.doubleValue())); // Natural log of number
    
        BigDecimal result = logNumber.divide(logBase, 10, RoundingMode.HALF_UP); // Change of base formula
    
        System.out.println("Result: " + result.setScale(2, RoundingMode.HALF_UP));
    }

    
    // Handling fraction : Example 1/2 + 2/3 
    private static void handleFractionArithmetic(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid input format for fractions. Example: 1/2 + 2/3");
        }

        Fraction left = Fraction.parse(parts[0]);
        Fraction right = Fraction.parse(parts[2]);
        String operator = parts[1];

        Fraction result;
        switch (operator) {
            case "+":
                result = left.add(right);
                break;
            case "-":
                result = left.subtract(right);
                break;
            case "*":
                result = left.multiply(right);
                break;
            case "/":
                result = left.divide(right);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator for fractions.");
        }
        System.out.println("Result: " + result);
    }
}

class Fraction {
    private final BigInteger numerator;
    private final BigInteger denominator;

    public Fraction(BigInteger numerator, BigInteger denominator) {
        if (denominator.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }

        BigInteger gcd = numerator.gcd(denominator);
        BigInteger adjustedNumerator = numerator.divide(gcd);
        BigInteger adjustedDenominator = denominator.divide(gcd);

        // Ensure the denominator is positive
        if (adjustedDenominator.compareTo(BigInteger.ZERO) < 0) {
            adjustedNumerator = adjustedNumerator.negate();
            adjustedDenominator = adjustedDenominator.negate();
        }

        this.numerator = adjustedNumerator;
        this.denominator = adjustedDenominator;
    }

    public static Fraction parse(String input) {
        String[] parts = input.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid fraction format. Example: 1/2");
        }
        return new Fraction(new BigInteger(parts[0]), new BigInteger(parts[1]));
    }

    public Fraction add(Fraction other) {
        BigInteger newNumerator = this.numerator.multiply(other.denominator)
                .add(other.numerator.multiply(this.denominator));
        BigInteger newDenominator = this.denominator.multiply(other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        BigInteger newNumerator = this.numerator.multiply(other.denominator)
                .subtract(other.numerator.multiply(this.denominator));
        BigInteger newDenominator = this.denominator.multiply(other.denominator);
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction multiply(Fraction other) {
        return new Fraction(this.numerator.multiply(other.numerator), this.denominator.multiply(other.denominator));
    }

    public Fraction divide(Fraction other) {
        return new Fraction(this.numerator.multiply(other.denominator), this.denominator.multiply(other.numerator));
    }

    @Override
    public String toString() {
        if (denominator.equals(BigInteger.ONE)) {
            return numerator.toString();
        }
        return numerator + "/" + denominator;
    }
}
