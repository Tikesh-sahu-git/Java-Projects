import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class AttendanceSystem {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/attendance_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "yourpassword";
    
    private Connection connection;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int currentUserId = -1;
    private String currentUserRole = "";
    
    public AttendanceSystem() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            scanner = new Scanner(System.in);
            createTablesIfNotExist();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void createTablesIfNotExist() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                "user_id SERIAL PRIMARY KEY, " +
                                "username VARCHAR(50) UNIQUE NOT NULL, " +
                                "password_hash VARCHAR(64) NOT NULL, " + // SHA-256 produces 64-character hash
                                "email VARCHAR(100) UNIQUE NOT NULL, " +
                                "role VARCHAR(20) NOT NULL, " +
                                "CHECK (role IN ('Admin', 'Teacher', 'Student')))";
        
        String createPersonsTable = "CREATE TABLE IF NOT EXISTS persons (" +
                                   "person_id SERIAL PRIMARY KEY, " +
                                   "name VARCHAR(100) NOT NULL, " +
                                   "email VARCHAR(100) UNIQUE NOT NULL, " +
                                   "role VARCHAR(20) NOT NULL, " +
                                   "department VARCHAR(50), " +
                                   "user_id INTEGER REFERENCES users(user_id))";
        
        String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                                     "attendance_id SERIAL PRIMARY KEY, " +
                                     "person_id INTEGER REFERENCES persons(person_id), " +
                                     "date DATE NOT NULL, " +
                                     "status VARCHAR(10) NOT NULL, " +
                                     "CHECK (status IN ('Present', 'Absent', 'Late', 'Leave')))";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createPersonsTable);
            stmt.execute(createAttendanceTable);
            
            // Create default admin account if it doesn't exist
            String checkAdmin = "SELECT 1 FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (!rs.next()) {
                String defaultAdmin = "INSERT INTO users (username, password_hash, email, role) " +
                                    "VALUES ('admin', ?, 'admin@school.edu', 'Admin')";
                try (PreparedStatement pstmt = connection.prepareStatement(defaultAdmin)) {
                    pstmt.setString(1, hashPassword("admin123")); // Default password
                    pstmt.executeUpdate();
                    System.out.println("Default admin account created with username 'admin' and password 'admin123'");
                }
            }
        }
    }
    
    public static void main(String[] args) {
        AttendanceSystem system = new AttendanceSystem();
        system.run();
    }
    
    public void run() {
        System.out.println("Welcome to Attendance Management System");
        
        // Authentication loop
        while (currentUserId == -1) {
            if (!authenticateUser()) {
                System.out.println("Authentication failed. Please try again.");
            }
        }
        
        // Main application loop
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    if (hasPermission("ManagePersons")) {
                        managePersons();
                    } else {
                        System.out.println("You don't have permission to access this feature.");
                    }
                    break;
                case "2":
                    if (hasPermission("RecordAttendance")) {
                        recordAttendance();
                    } else {
                        System.out.println("You don't have permission to access this feature.");
                    }
                    break;
                case "3":
                    if (hasPermission("ViewAttendance")) {
                        viewAttendance();
                    } else {
                        System.out.println("You don't have permission to access this feature.");
                    }
                    break;
                case "4":
                    if (hasPermission("GenerateReports")) {
                        generateReports();
                    } else {
                        System.out.println("You don't have permission to access this feature.");
                    }
                    break;
                case "5":
                    if (hasPermission("ManageUsers") && currentUserRole.equals("Admin")) {
                        manageUsers();
                    } else {
                        System.out.println("You don't have permission to access this feature.");
                    }
                    break;
                case "6":
                    System.out.println("Exiting Attendance System. Goodbye!");
                    try {
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                    System.exit(0);
                case "7":
                    currentUserId = -1; // Logout
                    System.out.println("Logged out successfully.");
                    run(); // Restart authentication process
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private boolean authenticateUser() {
        System.out.println("\nLogin");
        System.out.println("-----");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        String sql = "SELECT user_id, password_hash, role FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);
                
                if (inputHash.equals(storedHash)) {
                    currentUserId = rs.getInt("user_id");
                    currentUserRole = rs.getString("role");
                    System.out.println("\nLogin successful! Welcome, " + username);
                    System.out.println("Your role: " + currentUserRole);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }
        
        return false;
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean hasPermission(String feature) {
        switch (currentUserRole) {
            case "Admin":
                return true; // Admin has access to everything
            case "Teacher":
                return !feature.equals("ManageUsers"); // Teachers can't manage users
            case "Student":
                // Students can only view their own attendance
                return feature.equals("ViewAttendance") || feature.equals("GenerateReports");
            default:
                return false;
        }
    }
    
    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Manage Persons (Students/Employees)");
        System.out.println("2. Record Attendance");
        System.out.println("3. View Attendance");
        System.out.println("4. Generate Reports");
        if (currentUserRole.equals("Admin")) {
            System.out.println("5. Manage Users");
        }
        System.out.println("6. Exit");
        System.out.println("7. Logout");
        System.out.print("Enter your choice (1-" + (currentUserRole.equals("Admin") ? "7" : "6") + "): ");
    }
    
    private void manageUsers() {
        if (!currentUserRole.equals("Admin")) {
            System.out.println("Only administrators can manage users.");
            return;
        }
        
        while (true) {
            System.out.println("\nUser Management:");
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. Change User Password");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice (1-4): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addUser();
                    break;
                case "2":
                    viewAllUsers();
                    break;
                case "3":
                    changeUserPassword();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void addUser() {
        System.out.println("\nAdd New User");
        System.out.println("------------");
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.println("Select role:");
        System.out.println("1. Admin");
        System.out.println("2. Teacher");
        System.out.println("3. Student");
        System.out.print("Enter choice (1-3): ");
        String roleChoice = scanner.nextLine();
        String role = "";
        
        switch (roleChoice) {
            case "1":
                role = "Admin";
                break;
            case "2":
                role = "Teacher";
                break;
            case "3":
                role = "Student";
                break;
            default:
                System.out.println("Invalid choice. Setting to Student.");
                role = "Student";
        }
        
        String sql = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, email);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            
            System.out.println("\nUser added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
    
    private void viewAllUsers() {
        System.out.println("\nAll Users");
        System.out.println("---------");
        
        String sql = "SELECT user_id, username, email, role FROM users ORDER BY username";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.printf("%-5s %-20s %-30s %-10s%n", 
                "ID", "Username", "Email", "Role");
            System.out.println("----------------------------------------------------");
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                
                System.out.printf("%-5d %-20s %-30s %-10s%n", 
                    userId, username, email, role);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
    }
    
    private void changeUserPassword() {
        viewAllUsers();
        
        System.out.print("\nEnter user ID to change password: ");
        int userId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
    }
    
    // ... [Rest of your existing methods (managePersons, addPerson, viewAllPersons, etc.) remain the same]
    // Just make sure to check permissions at the start of each method as shown in the run() method
    
    // Modified addPerson method to associate with a user
    private void addPerson() {
        System.out.println("\nAdd New Person");
        System.out.println("--------------");
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.println("Select role:");
        System.out.println("1. Student");
        System.out.println("2. Employee");
        System.out.print("Enter choice (1-2): ");
        String roleChoice = scanner.nextLine();
        String role = roleChoice.equals("1") ? "Student" : "Employee";
        
        System.out.print("Enter department (optional, press Enter to skip): ");
        String department = scanner.nextLine();
        
        // Only admins can associate persons with users
        int userId = -1;
        if (currentUserRole.equals("Admin")) {
            System.out.print("Enter user ID to associate (0 for none): ");
            userId = Integer.parseInt(scanner.nextLine());
            if (userId == 0) userId = -1;
        }
        
        String sql = "INSERT INTO persons (name, email, role, department, user_id) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, role);
            pstmt.setString(4, department.isEmpty() ? null : department);
            pstmt.setObject(5, userId == -1 ? null : userId);
            pstmt.executeUpdate();
            
            System.out.println("\nPerson added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding person: " + e.getMessage());
        }
    }
    
    // Modified viewAttendanceByPerson to respect student permissions
    private void viewAttendanceByPerson() {
        if (currentUserRole.equals("Student")) {
            // Students can only view their own attendance
            viewAttendanceForCurrentUser();
            return;
        }
        
        viewAllPersons();
        
        System.out.print("\nEnter person ID to view attendance: ");
        int personId = Integer.parseInt(scanner.nextLine());
        
        // ... [rest of the existing viewAttendanceByPerson method]
    }
    
    private void viewAttendanceForCurrentUser() {
        // Get the person ID associated with the current user
        String personSql = "SELECT person_id FROM persons WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(personSql)) {
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int personId = rs.getInt("person_id");
                
                // Now reuse the existing viewAttendanceByPerson logic but for this specific person
                System.out.print("\nEnter start date (YYYY-MM-DD) or press Enter for all records: ");
                String startDateInput = scanner.nextLine();
                
                System.out.print("Enter end date (YYYY-MM-DD) or press Enter for no end date: ");
                String endDateInput = scanner.nextLine();
                
                String sql = "SELECT a.date, a.status, p.name FROM attendance a " +
                             "JOIN persons p ON a.person_id = p.person_id " +
                             "WHERE a.person_id = ?";
                
                if (!startDateInput.isEmpty()) {
                    sql += " AND a.date >= ?";
                }
                if (!endDateInput.isEmpty()) {
                    sql += " AND a.date <= ?";
                }
                sql += " ORDER BY a.date DESC";
                
                try (PreparedStatement attendanceStmt = connection.prepareStatement(sql)) {
                    int paramIndex = 1;
                    attendanceStmt.setInt(paramIndex++, personId);
                    
                    if (!startDateInput.isEmpty()) {
                        attendanceStmt.setDate(paramIndex++, Date.valueOf(LocalDate.parse(startDateInput, dateFormatter)));
                    }
                    if (!endDateInput.isEmpty()) {
                        attendanceStmt.setDate(paramIndex, Date.valueOf(LocalDate.parse(endDateInput, dateFormatter)));
                    }
                    
                    ResultSet attendanceRs = attendanceStmt.executeQuery();
                    
                    if (!attendanceRs.isBeforeFirst()) {
                        System.out.println("No attendance records found for you.");
                        return;
                    }
                    
                    System.out.println("\nYour Attendance Records");
                    System.out.printf("%-12s %-10s%n", "Date", "Status");
                    System.out.println("---------------------");
                    
                    while (attendanceRs.next()) {
                        Date date = attendanceRs.getDate("date");
                        String status = attendanceRs.getString("status");
                        
                        System.out.printf("%-12s %-10s%n", date, status);
                    }
                    
                    // Calculate summary statistics
                    calculatePersonAttendanceSummary(personId, 
                        startDateInput.isEmpty() ? null : LocalDate.parse(startDateInput, dateFormatter),
                        endDateInput.isEmpty() ? null : LocalDate.parse(endDateInput, dateFormatter));
                    
                }
            } else {
                System.out.println("No person record associated with your user account.");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving your attendance: " + e.getMessage());
        }
    }
} 
