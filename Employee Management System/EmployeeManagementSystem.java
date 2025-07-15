import java.sql.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EmployeeManagementSystem {
    // Database configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DEFAULT_DB = "postgres";
    private static final String APP_DB = "employee_management";
    private static final String USER = "postgres";
    private static final String PASSWORD = "2002"; // Change this

    // Application state
    private static Connection connection;
    private static String currentUser;
    private static String currentUserRole;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeDatabase();
        if (login()) {
            showMainMenu();
        }
        scanner.close();
    }

    // ================= DATABASE INITIALIZATION =================
    private static void initializeDatabase() {
        try {
            // First connect to default database to create our application database
            Connection defaultConn = DriverManager.getConnection(DB_URL + DEFAULT_DB, USER, PASSWORD);
            Statement stmt = defaultConn.createStatement();

            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE " + APP_DB);
            System.out.println("Database initialized successfully.");

            // Connect to our application database
            connection = DriverManager.getConnection(DB_URL + APP_DB, USER, PASSWORD);

            // Create tables
            createTables();

            // Insert admin user if not exists
            insertAdminUser();

        } catch (SQLException e) {
            if (e.getSQLState().equals("42P04")) { // Database already exists
                try {
                    connection = DriverManager.getConnection(DB_URL + APP_DB, USER, PASSWORD);
                } catch (SQLException ex) {
                    System.err.println("Error connecting to existing database: " + ex.getMessage());
                    System.exit(1);
                }
            } else {
                System.err.println("Database initialization failed: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Users table for authentication
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(64) NOT NULL, " + // SHA-256 hash
                "role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'USER')), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // Employees table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employees (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "department VARCHAR(50) NOT NULL, " +
                "position VARCHAR(50) NOT NULL, " +
                "salary DECIMAL(10,2) NOT NULL, " +
                "hire_date DATE NOT NULL, " +
                "created_by INTEGER REFERENCES users(id), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
    }

    private static void insertAdminUser() throws SQLException {
        // Check if admin exists
        PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT 1 FROM users WHERE username = 'admin'");
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            // Insert admin user with default password "admin123"
            String hashedPassword = hashPassword("admin123");
            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, 'ADMIN')");
            insertStmt.setString(1, "admin");
            insertStmt.setString(2, hashedPassword);
            insertStmt.executeUpdate();
            System.out.println("Default admin user created with password 'admin123'");
        }
    }

    // ================= AUTHENTICATION =================
    private static boolean login() {
        System.out.println("\n=== Employee Management System Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, password, role FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String inputHash = hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    currentUser = username;
                    currentUserRole = rs.getString("role");
                    System.out.println("\nLogin successful! Welcome, " + username + " (" + currentUserRole + ")");
                    return true;
                }
            }

            System.out.println("Invalid username or password!");
            return false;

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    // ================= MAIN MENU =================
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. View Employees");
            System.out.println("2. Add Employee");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");

            if (currentUserRole.equals("ADMIN")) {
                System.out.println("5. User Management");
            }

            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewEmployees();
                    break;
                case 2:
                    addEmployee();
                    break;
                case 3:
                    updateEmployee();
                    break;
                case 4:
                    deleteEmployee();
                    break;
                case 5:
                    if (currentUserRole.equals("ADMIN")) {
                        userManagement();
                    } else {
                        System.out.println("Invalid choice!");
                    }
                    break;
                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= EMPLOYEE OPERATIONS =================
    private static void viewEmployees() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT e.id, e.name, e.email, e.department, e.position, e.salary, e.hire_date " +
                            "FROM employees e ORDER BY e.id");

            System.out.println("\n=== Employee List ===");
            System.out.printf("%-5s %-20s %-25s %-15s %-20s %-10s %-12s%n",
                    "ID", "Name", "Email", "Department", "Position", "Salary", "Hire Date");
            System.out.println("------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-25s %-15s %-20s $%-9.2f %-12s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date"));
            }

        } catch (SQLException e) {
            System.err.println("Error viewing employees: " + e.getMessage());
        }
    }

    private static void addEmployee() {
        if (!currentUserRole.equals("ADMIN") && !currentUserRole.equals("USER")) {
            System.out.println("You don't have permission to add employees!");
            return;
        }

        System.out.println("\n=== Add New Employee ===");

        try {
            // Get user ID for created_by
            PreparedStatement userStmt = connection.prepareStatement(
                    "SELECT id FROM users WHERE username = ?");
            userStmt.setString(1, currentUser);
            ResultSet userRs = userStmt.executeQuery();
            userRs.next();
            int createdBy = userRs.getInt("id");

            // Get employee details
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Department: ");
            String department = scanner.nextLine();
            System.out.print("Position: ");
            String position = scanner.nextLine();
            System.out.print("Salary: ");
            double salary = Double.parseDouble(scanner.nextLine());
            System.out.print("Hire Date (YYYY-MM-DD): ");
            String hireDate = scanner.nextLine();

            // Insert employee
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO employees (name, email, department, position, salary, hire_date, created_by) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, department);
            stmt.setString(4, position);
            stmt.setDouble(5, salary);
            stmt.setDate(6, Date.valueOf(hireDate));
            stmt.setInt(7, createdBy);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Employee added successfully!");
            } else {
                System.out.println("Failed to add employee.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary format!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Use YYYY-MM-DD.");
        }
    }

    private static void updateEmployee() {
        if (!currentUserRole.equals("ADMIN")) {
            System.out.println("You don't have permission to update employees!");
            return;
        }

        System.out.println("\n=== Update Employee ===");
        System.out.print("Enter employee ID to update: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            // Check if employee exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT 1 FROM employees WHERE id = ?");
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Employee not found!");
                return;
            }

            // Get updated details
            System.out.print("Name (leave blank to keep current): ");
            String name = scanner.nextLine();
            System.out.print("Email (leave blank to keep current): ");
            String email = scanner.nextLine();
            System.out.print("Department (leave blank to keep current): ");
            String department = scanner.nextLine();
            System.out.print("Position (leave blank to keep current): ");
            String position = scanner.nextLine();
            System.out.print("Salary (leave blank to keep current): ");
            String salaryStr = scanner.nextLine();
            Double salary = salaryStr.isEmpty() ? null : Double.parseDouble(salaryStr);

            // Build update query
            StringBuilder query = new StringBuilder("UPDATE employees SET ");
            boolean needsComma = false;

            if (!name.isEmpty()) {
                query.append("name = ?");
                needsComma = true;
            }
            if (!email.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("email = ?");
                needsComma = true;
            }
            if (!department.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("department = ?");
                needsComma = true;
            }
            if (!position.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("position = ?");
                needsComma = true;
            }
            if (salary != null) {
                if (needsComma) query.append(", ");
                query.append("salary = ?");
                needsComma = true;
            }

            if (!needsComma) {
                System.out.println("No changes provided!");
                return;
            }

            query.append(" WHERE id = ?");

            PreparedStatement stmt = connection.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!name.isEmpty()) {
                stmt.setString(paramIndex++, name);
            }
            if (!email.isEmpty()) {
                stmt.setString(paramIndex++, email);
            }
            if (!department.isEmpty()) {
                stmt.setString(paramIndex++, department);
            }
            if (!position.isEmpty()) {
                stmt.setString(paramIndex++, position);
            }
            if (salary != null) {
                stmt.setDouble(paramIndex++, salary);
            }

            stmt.setInt(paramIndex, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Employee updated successfully!");
            } else {
                System.out.println("Failed to update employee.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID or salary format!");
        }
    }

    private static void deleteEmployee() {
        if (!currentUserRole.equals("ADMIN")) {
            System.out.println("You don't have permission to delete employees!");
            return;
        }

        System.out.println("\n=== Delete Employee ===");
        System.out.print("Enter employee ID to delete: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM employees WHERE id = ?");
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Employee deleted successfully!");
            } else {
                System.out.println("Employee not found!");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    // ================= USER MANAGEMENT (ADMIN ONLY) =================
    private static void userManagement() {
        while (true) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. List Users");
            System.out.println("2. Add User");
            System.out.println("3. Change User Password");
            System.out.println("4. Delete User");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    listUsers();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    changeUserPassword();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void listUsers() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, username, role, created_at FROM users ORDER BY id");

            System.out.println("\n=== User List ===");
            System.out.printf("%-5s %-20s %-10s %-20s%n",
                    "ID", "Username", "Role", "Created At");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10s %-20s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at"));
            }

        } catch (SQLException e) {
            System.err.println("Error listing users: " + e.getMessage());
        }
    }

    private static void addUser() {
        System.out.println("\n=== Add New User ===");

        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Role (ADMIN/USER): ");
            String role = scanner.nextLine().toUpperCase();

            if (!role.equals("ADMIN") && !role.equals("USER")) {
                System.out.println("Invalid role! Must be ADMIN or USER.");
                return;
            }

            String hashedPassword = hashPassword(password);

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User added successfully!");
            } else {
                System.out.println("Failed to add user.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    private static void changeUserPassword() {
        System.out.println("\n=== Change User Password ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        try {
            // Check if user exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT 1 FROM users WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("User not found!");
                return;
            }

            System.out.print("New password: ");
            String newPassword = scanner.nextLine();
            String hashedPassword = hashPassword(newPassword);

            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET password = ? WHERE username = ?");
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Failed to change password.");
            }

        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n=== Delete User ===");
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine();

        // Prevent deleting yourself
        if (username.equals(currentUser)) {
            System.out.println("You cannot delete your own account!");
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM users WHERE username = ?");
            stmt.setString(1, username);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found!");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }
}