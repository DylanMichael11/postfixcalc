package calculator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.io.File; 

public class PostFixCalc {
    // Add just the most important error messages as constants
    private static final String ERROR_NULL_EMPTY = "Expression cannot be null or empty";
    private static final String ERROR_INSUFFICIENT_OPERANDS = "Invalid: insufficient operands";
    private static final String ERROR_TOO_MANY_OPERANDS = "Invalid: too many operands";
    private static final String ERROR_DIVISION_BY_ZERO = "Division by zero";
    
    /**
     * Evaluates a postfix expression and returns the result
     * @param postfixExpression The postfix expression to evaluate
     * @return The result of the evaluation
     * @throws IllegalArgumentException if the expression is invalid
     */
    public int evaluatePostfix(String postfixExpression) {
        if (postfixExpression == null || postfixExpression.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_NULL_EMPTY);
        }

        Stack<Integer> stack = new Stack<>();
        String[] tokens = postfixExpression.split("\\s+");

        try {
            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()) continue;

                if (isOperator(token)) {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException(ERROR_INSUFFICIENT_OPERANDS);
                    }
                    int operand2 = stack.pop();
                    int operand1 = stack.pop();
                    stack.push(performOperation(operand1, operand2, token));
                } else {
                    // Try to parse as a number
                    try {
                        stack.push(Integer.parseInt(token));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid token: " + token);
                    }
                }
            }

            if (stack.size() != 1) {
                throw new IllegalArgumentException(ERROR_TOO_MANY_OPERANDS);
            }

            return stack.pop();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(" Error: " + e.getMessage());
        }
    }

    /**
     * Checks to see if it is an operator
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("*") || token.equals("/") || 
               token.equals("%");
    }

    /**
     * Performs the operation
     */
    private int performOperation(int operand1, int operand2, String operator) {
        switch (operator) {
            case "+": return operand1 + operand2;
            case "-": return operand1 - operand2;
            case "*": return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ArithmeticException(ERROR_DIVISION_BY_ZERO);
                }
                return operand1 / operand2;
            case "%":
                if (operand2 == 0) {
                    throw new ArithmeticException("Modulo by zero");
                }
                return operand1 % operand2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    /**
     * Reads expressions from a file and evaluates them
     */
    public void evaluateFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                try {
                    System.out.printf("Expression %d: %s%n", lineNumber, line);
                    int result = evaluatePostfix(line);
                    System.out.printf("Result %d: %d%n", lineNumber, result);
                } catch (IllegalArgumentException e) {
                    System.out.printf("Error in expression %d: %s%n", lineNumber, e.getMessage());
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        PostFixCalc calculator = new PostFixCalc();

        // Test Case 1: Single-digit 
        String expr1 = "5 3 + 2 *";
        System.out.println("\nTest Case 1 - Single-digit operands");
        System.out.println("Expression: " + expr1);
        try {
            System.out.println("Result: " + calculator.evaluatePostfix(expr1));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Test Case 2: Multi-digit 
        String expr2 = "15 7 1 1 + - / 3 * 2 1 1 + + -";
        System.out.println("\nTest Case 2 - Multi-digit operands");
        System.out.println("Expression: " + expr2);
        try {
            System.out.println("Result: " + calculator.evaluatePostfix(expr2));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test Case 3: Division by zero
        String expr3 = "5 0 /";
        System.out.println("\nTest Case 3 - Division by zero");
        System.out.println("Expression: " + expr3);
        try {
            System.out.println("Result: " + calculator.evaluatePostfix(expr3));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test Case 4: Invalid expression 
        String expr4 = "1 2 + +";
        System.out.println("\nTest Case 4 - Invalid expression");
        System.out.println("Expression: " + expr4);
        try {
            System.out.println("Result: " + calculator.evaluatePostfix(expr4));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test Case 5: Empty expression
        String expr5 = "";
        System.out.println("\nTest Case 5 - Empty expression");
        System.out.println("Expression: " + expr5);
        try {
            System.out.println("Result: " + calculator.evaluatePostfix(expr5));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nReading expressions from file test:");
        String filePath = "expressions.txt";  // file in project root
        System.out.println("Testing file at: " + new File(filePath).getAbsolutePath());
        calculator.evaluateFromFile(filePath);
    }
}
