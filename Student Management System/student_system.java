import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Student {
    private String name;
    private String rollNumber;
    private String grade;
    private String email;
    private String phone;

    public Student(String name, String rollNumber, String grade, String email, String phone) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getGrade() { return grade; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("| %-15s | %-12s | %-6s | %-25s | %-12s |",
                name, rollNumber, grade, email, phone);
    }
}

class StudentManagementSystem {
    private List<Student> students;
    private static final String FILE_NAME = "students.txt";

    public StudentManagementSystem() {
        students = new ArrayList<>();
        loadStudentsFromFile();
    }

    // Add a new student
    public void addStudent(Student student) {
        students.add(student);
        saveStudentsToFile();
    }

    // Remove a student by roll number
    public boolean removeStudent(String rollNumber) {
        Student student = findStudent(rollNumber);
        if (student != null) {
            students.remove(student);
            saveStudentsToFile();
            return true;
        }
        return false;
    }

    // Find a student by roll number
    public Student findStudent(String rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber().equals(rollNumber)) {
                return student;
            }
        }
        return null;
    }

    // Display all students
    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in the system.");
            return;
        }

        System.out.println("\n+-----------------+------------+--------+---------------------------+------------+");
        System.out.println("| Name            | Roll Number| Grade  | Email                     | Phone      |");
        System.out.println("+-----------------+------------+--------+---------------------------+------------+");
        for (Student student : students) {
            System.out.println(student);
        }
        System.out.println("+-----------------+------------+--------+---------------------------+------------+");
        System.out.println("Total students: " + students.size());
    }

    // Save students to file
    private void saveStudentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.println(student.getName() + "," + student.getRollNumber() + "," + 
                              student.getGrade() + "," + student.getEmail() + "," + student.getPhone());
            }
        } catch (IOException e) {
            System.out.println("Error saving student data: " + e.getMessage());
        }
    }

    // Load students from file
    private void loadStudentsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    students.add(new Student(data[0], data[1], data[2], data[3], data[4]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading student data: " + e.getMessage());
        }
    }
}

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static StudentManagementSystem sms = new StudentManagementSystem();

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════════");
        System.out.println("      STUDENT MANAGEMENT SYSTEM");
        System.out.println("══════════════════════════════════════════════");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    removeStudent();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    editStudent();
                    break;
                case 5:
                    sms.displayAllStudents();
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Add New Student");
        System.out.println("2. Remove Student");
        System.out.println("3. Search Student");
        System.out.println("4. Edit Student");
        System.out.println("5. Display All Students");
        System.out.println("6. Exit");
    }

    private static void addStudent() {
        System.out.println("\nAdd New Student");
        System.out.println("----------------");

        String name = getStringInput("Enter student name: ");
        String rollNumber = getRollNumberInput();
        String grade = getStringInput("Enter grade: ");
        String email = getEmailInput();
        String phone = getPhoneInput();

        Student student = new Student(name, rollNumber, grade, email, phone);
        sms.addStudent(student);
        System.out.println("Student added successfully!");
    }

    private static void removeStudent() {
        System.out.println("\nRemove Student");
        System.out.println("----------------");
        String rollNumber = getStringInput("Enter roll number to remove: ");

        if (sms.removeStudent(rollNumber)) {
            System.out.println("Student removed successfully!");
        } else {
            System.out.println("Student not found with roll number: " + rollNumber);
        }
    }

    private static void searchStudent() {
        System.out.println("\nSearch Student");
        System.out.println("----------------");
        String rollNumber = getStringInput("Enter roll number to search: ");

        Student student = sms.findStudent(rollNumber);
        if (student != null) {
            System.out.println("\nStudent Found:");
            System.out.println("Name: " + student.getName());
            System.out.println("Roll Number: " + student.getRollNumber());
            System.out.println("Grade: " + student.getGrade());
            System.out.println("Email: " + student.getEmail());
            System.out.println("Phone: " + student.getPhone());
        } else {
            System.out.println("Student not found with roll number: " + rollNumber);
        }
    }

    private static void editStudent() {
        System.out.println("\nEdit Student");
        System.out.println("----------------");
        String rollNumber = getStringInput("Enter roll number to edit: ");

        Student student = sms.findStudent(rollNumber);
        if (student != null) {
            System.out.println("\nCurrent Information:");
            System.out.println("1. Name: " + student.getName());
            System.out.println("2. Grade: " + student.getGrade());
            System.out.println("3. Email: " + student.getEmail());
            System.out.println("4. Phone: " + student.getPhone());

            int choice = getIntInput("\nEnter field number to edit (1-4, 0 to cancel): ");
            if (choice >= 1 && choice <= 4) {
                switch (choice) {
                    case 1:
                        student.setName(getStringInput("Enter new name: "));
                        break;
                    case 2:
                        student.setGrade(getStringInput("Enter new grade: "));
                        break;
                    case 3:
                        student.setEmail(getEmailInput());
                        break;
                    case 4:
                        student.setPhone(getPhoneInput());
                        break;
                }
                sms.saveStudentsToFile();
                System.out.println("Student information updated successfully!");
            }
        } else {
            System.out.println("Student not found with roll number: " + rollNumber);
        }
    }

    // Helper methods for input validation
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.println("This field cannot be empty!");
            System.out.print(prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    private static String getRollNumberInput() {
        while (true) {
            String rollNumber = getStringInput("Enter roll number: ");
            if (sms.findStudent(rollNumber) == null) {
                return rollNumber;
            }
            System.out.println("Roll number already exists. Please enter a unique roll number.");
        }
    }

    private static String getEmailInput() {
        while (true) {
            String email = getStringInput("Enter email: ");
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("Invalid email format. Please include '@' and '.'");
        }
    }

    private static String getPhoneInput() {
        while (true) {
            String phone = getStringInput("Enter phone number: ");
            if (phone.matches("\\d{10,15}")) {
                return phone;
            }
            System.out.println("Invalid phone number. Please enter 10-15 digits.");
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
