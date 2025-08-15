
# Java CLI Calculator

A simple **Command-Line Interface (CLI) Calculator** built using Java.  
This project allows users to perform basic arithmetic operations interactively in the terminal.

---

## 📌 Features
- Addition (`+`)
- Subtraction (`-`)
- Multiplication (`*`)
- Division (`/`) with division-by-zero handling
- Input validation for choices and numbers
- Continuous loop until the user chooses **Exit**

---

## 🛠️ Technologies Used
- **Java SE**
- **Scanner Class** for user input
- **Switch Case** for operation handling
- **Exception Handling** for invalid inputs

---

## 📂 Project Structure
```
Calculator.java
```

---

## 📜 Source Code

```java
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
```

---

## ▶️ How to Run

1. Save the code in a file named `Calculator.java`.
2. Open a terminal in the project directory.
3. Compile the program:

   ```bash
   javac Calculator.java
   ```
4. Run the program:

   ```bash
   java Calculator
   ```

---

## 📊 Sample Output

```
-------------------
Java CLI Calculator
-------------------

Available operations:
1. Addition (+)
2. Subtraction (-)
3. Multiplication (*)
4. Division (/)
5. Exit
Choose an operation (1-5): 1
Enter first number: 12
Enter second number: 8

Result: 12.00 + 8.00 = 20.00
```

---

## ✅ Future Improvements

* Add support for modulus and power operations
* Implement history of calculations
* Add GUI version (Swing/JavaFX)

---

## 👨‍💻 Author

**Tikesh Sahu**  
Full-Stack Developer | Java Enthusiast
