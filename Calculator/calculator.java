import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println();       
        System.out.println("-------------------");
        System.out.println("Java CLI Calculator");
        System.out.println("-------------------");
        
        while (true) {
            System.out.println("\nAvailable operations:");
            System.out.println("1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.println("5. Exit");
            
            System.out.print("Choose an operation (1-5): ");
            int choice;
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue;
            }
            
            if (choice == 5) {
                System.out.println("Thank you for using the calculator. Goodbye!");
                break;
            }
            
            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please select between 1 and 5.");
                continue;
            }
            
            double num1, num2;
            
            try {
                System.out.print("Enter first number: ");
                num1 = Double.parseDouble(scanner.nextLine());
                
                System.out.print("Enter second number: ");
                num2 = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number input. Please enter numeric values.");
                continue;
            }
            
            double result = 0;
            String operation = "";
            
            switch (choice) {
                case 1:
                    result = num1 + num2;
                    operation = "+";
                    break;
                case 2:
                    result = num1 - num2;
                    operation = "-";
                    break;
                case 3:
                    result = num1 * num2;
                    operation = "*";
                    break;
                case 4:
                    if (num2 == 0) {
                        System.out.println("Error: Division by zero is not allowed.");
                        continue;
                    }
                    result = num1 / num2;
                    operation = "/";
                    break;
            }
            
            System.out.printf("\nResult: %.2f %s %.2f = %.2f%n", num1, operation, num2, result);
        }
        
        scanner.close();
    }
}
