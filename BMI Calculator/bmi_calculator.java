import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class BMIEntry {
    String date;
    double bmi;
    String status;

    public BMIEntry(String date, double bmi, String status) {
        this.date = date;
        this.bmi = bmi;
        this.status = status;
    }
}

public class EnhancedBMICalculator {
    private static List<BMIEntry> bmiHistory = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🌟 ENHANCED BMI CALCULATOR 🌟");
        System.out.println("----------------------------");

        while (true) {
            System.out.println("\n🔹 MAIN MENU");
            System.out.println("1. Calculate BMI (Metric)");
            System.out.println("2. Calculate BMI (Imperial)");
            System.out.println("3. View BMI History");
            System.out.println("4. Exit");
            System.out.print("Choose an option (1-4): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    calculateBMI(false);
                    break;
                case 2:
                    calculateBMI(true);
                    break;
                case 3:
                    viewHistory();
                    break;
                case 4:
                    System.out.println("\n👋 Thank you for using the BMI Calculator!");
                    return;
                default:
                    System.out.println("⚠️ Invalid choice. Please try again.");
            }
        }
    }

    private static void calculateBMI(boolean isImperial) {
        System.out.println("\n📝 ENTER YOUR DETAILS");

        double height, weight;
        if (isImperial) {
            System.out.print("📏 Enter height (feet): ");
            double feet = scanner.nextDouble();
            System.out.print("📏 Enter height (inches): ");
            double inches = scanner.nextDouble();
            height = (feet * 12 + inches) * 0.0254; // Convert to meters

            System.out.print("⚖️ Enter weight (pounds): ");
            weight = scanner.nextDouble() * 0.453592; // Convert to kg
        } else {
            System.out.print("📏 Enter height in meters: ");
            height = scanner.nextDouble();
            System.out.print("⚖️ Enter weight in kilograms: ");
            weight = scanner.nextDouble();
        }

        System.out.print("📅 Enter today's date (DD-MM-YYYY): ");
        String date = scanner.next();

        double bmi = weight / (height * height);
        String[] status = getBMIStatus(bmi);

        // Store the entry
        bmiHistory.add(new BMIEntry(date, bmi, status[0]));

        // Display results
        System.out.println("\n📊----- RESULTS -----📊");
        System.out.printf("🖥️ Your BMI: %.2f\n", bmi);
        System.out.println("🏷️ Status: " + status[0] + " " + status[1]);
        System.out.println("\n💡 Health Tips:");
        displayHealthTips(bmi);
    }

    private static String[] getBMIStatus(double bmi) {
        if (bmi < 18.5) return new String[]{"Underweight", "😟"};
        if (bmi < 25) return new String[]{"Normal weight", "😊"};
        if (bmi < 30) return new String[]{"Overweight", "⚠️"};
        return new String[]{"Obese", "❌"};
    }

    private static void displayHealthTips(double bmi) {
        if (bmi < 18.5) {
            System.out.println("- Consider increasing calorie intake");
            System.out.println("- Focus on nutrient-dense foods");
        } else if (bmi >= 25) {
            System.out.println("- Consider more physical activity");
            System.out.println("- Watch your portion sizes");
        } else {
            System.out.println("- Maintain your healthy lifestyle!");
        }
    }

    private static void viewHistory() {
        if (bmiHistory.isEmpty()) {
            System.out.println("\n📭 No BMI records found.");
            return;
        }

        System.out.println("\n📅----- BMI HISTORY -----📅");
        System.out.println("Date\t\tBMI\tStatus");
        System.out.println("------------------------");
        for (BMIEntry entry : bmiHistory) {
            System.out.printf("%s\t%.2f\t%s\n", entry.date, entry.bmi, entry.status);
        }

        // Calculate trend
        if (bmiHistory.size() > 1) {
            double firstBMI = bmiHistory.get(0).bmi;
            double lastBMI = bmiHistory.get(bmiHistory.size()-1).bmi;
            double change = lastBMI - firstBMI;
            
            System.out.println("\n📈 Trend Analysis:");
            System.out.printf("First BMI: %.2f\n", firstBMI);
            System.out.printf("Last BMI: %.2f\n", lastBMI);
            System.out.printf("Change: %.2f (%s)\n", Math.abs(change), 
                change > 0 ? "↑ Increase" : change < 0 ? "↓ Decrease" : "No change");
        }
    }
}
