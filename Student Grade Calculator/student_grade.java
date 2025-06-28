import java.util.Scanner;

public class GradeCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("🌟 STUDENT GRADE CALCULATOR 🌟");
        System.out.println("----------------------------");

        System.out.print("📚 Enter the number of subjects: ");
        int numSubjects = scanner.nextInt();

        if (numSubjects <= 0) {
            System.out.println("❌ Invalid number of subjects. Exiting...");
            return;
        }

        int[] marks = new int[numSubjects];
        int totalMarks = 0;

        // 📥 Input marks for each subject
        for (int i = 0; i < numSubjects; i++) {
            System.out.printf("➡️ Enter marks for Subject %d (out of 100): ", i + 1);
            marks[i] = scanner.nextInt();

            // 🔍 Validate marks (0-100)
            if (marks[i] < 0 || marks[i] > 100) {
                System.out.println("⚠️ Invalid marks! Please enter between 0 and 100.");
                i--; // Try again for same subject
                continue;
            }
            totalMarks += marks[i];
        }

        // 🧮 Calculate average percentage
        double averagePercentage = (double) totalMarks / numSubjects;

        // 🏆 Determine grade
        String grade;
        if (averagePercentage >= 90) {
            grade = "A+ 🌟";
        } else if (averagePercentage >= 80) {
            grade = "A 🎯";
        } else if (averagePercentage >= 70) {
            grade = "B 👍";
        } else if (averagePercentage >= 60) {
            grade = "C ✔️";
        } else if (averagePercentage >= 50) {
            grade = "D 😟";
        } else {
            grade = "F ❌";
        }

        // 📊 Display results
        System.out.println("\n📈----- RESULTS -----📉");
        System.out.println("📊 Total Marks: " + totalMarks + "/" + (numSubjects * 100));
        System.out.printf("📌 Average Percentage: %.2f%%\n", averagePercentage);
        System.out.println("🎓 Grade: " + grade);

        scanner.close();
    }
}
