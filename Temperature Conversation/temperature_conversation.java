import java.util.Scanner;

public class TemperatureConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("-----------------------------");
        System.out.println(" Temperature Conversion Tool ");
        System.out.println("-----------------------------");
        
        while (true) {
            System.out.println("\nAvailable conversions:");
            System.out.println("1. Celsius to Fahrenheit");
            System.out.println("2. Fahrenheit to Celsius");
            System.out.println("3. Celsius to Kelvin");
            System.out.println("4. Kelvin to Celsius");
            System.out.println("5. Fahrenheit to Kelvin");
            System.out.println("6. Kelvin to Fahrenheit");
            System.out.println("7. Exit");
            
            System.out.print("Choose a conversion (1-7): ");
            int choice;
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }
            
            if (choice == 7) {
                System.out.println("Thank you for using the temperature converter. Goodbye!");
                break;
            }
            
            if (choice < 1 || choice > 7) {
                System.out.println("Invalid choice. Please select between 1 and 7.");
                continue;
            }
            
            double temperature;
            
            try {
                System.out.print("Enter temperature value: ");
                temperature = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid temperature input. Please enter a numeric value.");
                continue;
            }
            
            double convertedTemp = 0;
            String fromUnit = "";
            String toUnit = "";
            
            switch (choice) {
                case 1: // Celsius to Fahrenheit
                    convertedTemp = (temperature * 9/5) + 32;
                    fromUnit = "°C";
                    toUnit = "°F";
                    break;
                case 2: // Fahrenheit to Celsius
                    convertedTemp = (temperature - 32) * 5/9;
                    fromUnit = "°F";
                    toUnit = "°C";
                    break;
                case 3: // Celsius to Kelvin
                    convertedTemp = temperature + 273.15;
                    fromUnit = "°C";
                    toUnit = "K";
                    break;
                case 4: // Kelvin to Celsius
                    convertedTemp = temperature - 273.15;
                    fromUnit = "K";
                    toUnit = "°C";
                    break;
                case 5: // Fahrenheit to Kelvin
                    convertedTemp = (temperature - 32) * 5/9 + 273.15;
                    fromUnit = "°F";
                    toUnit = "K";
                    break;
                case 6: // Kelvin to Fahrenheit
                    convertedTemp = (temperature - 273.15) * 9/5 + 32;
                    fromUnit = "K";
                    toUnit = "°F";
                    break;
            }
            
            System.out.printf("\nConversion result: %.2f%s = %.2f%s%n", 
                             temperature, fromUnit, convertedTemp, toUnit);
        }
        
        scanner.close();
    }
}
