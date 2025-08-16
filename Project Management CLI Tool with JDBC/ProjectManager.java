import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ProjectManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/project_management";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "2002";
    
    private Connection connection;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public ProjectManager() {
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
                                "email VARCHAR(100) UNIQUE NOT NULL)";
        
        String createProjectsTable = "CREATE TABLE IF NOT EXISTS projects (" +
                                   "project_id SERIAL PRIMARY KEY, " +
                                   "project_name VARCHAR(100) NOT NULL, " +
                                   "description TEXT, " +
                                   "start_date DATE, " +
                                   "target_date DATE, " +
                                   "status VARCHAR(20) DEFAULT 'Not Started')";
        
        String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks (" +
                                "task_id SERIAL PRIMARY KEY, " +
                                "project_id INTEGER REFERENCES projects(project_id), " +
                                "task_name VARCHAR(100) NOT NULL, " +
                                "description TEXT, " +
                                "status VARCHAR(20) DEFAULT 'Not Started', " +
                                "priority VARCHAR(10), " +
                                "due_date DATE, " +
                                "assigned_to INTEGER REFERENCES users(user_id))";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createProjectsTable);
            stmt.execute(createTasksTable);
        }
    }
    
    public static void main(String[] args) {
        ProjectManager manager = new ProjectManager();
        manager.run();
    }
    
    public void run() {
        System.out.println("Welcome to Project Management CLI");
        
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    manageUsers();
                    break;
                case "2":
                    manageProjects();
                    break;
                case "3":
                    manageTasks();
                    break;
                case "4":
                    generateReports();
                    break;
                case "5":
                    System.out.println("Exiting Project Manager. Goodbye!");
                    try {
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Manage Users");
        System.out.println("2. Manage Projects");
        System.out.println("3. Manage Tasks");
        System.out.println("4. Generate Reports");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }
    
    private void manageUsers() {
        while (true) {
            System.out.println("\nUser Management:");
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice (1-3): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addUser();
                    break;
                case "2":
                    viewAllUsers();
                    break;
                case "3":
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
        
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            
            System.out.println("\nUser added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
    
    private void viewAllUsers() {
        System.out.println("\nAll Users");
        System.out.println("---------");
        
        String sql = "SELECT user_id, username, email FROM users ORDER BY username";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.printf("%-5s %-20s %-30s%n", "ID", "Username", "Email");
            System.out.println("--------------------------------------------------");
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                
                System.out.printf("%-5d %-20s %-30s%n", userId, username, email);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
    }
    
    private void manageProjects() {
        while (true) {
            System.out.println("\nProject Management:");
            System.out.println("1. Create Project");
            System.out.println("2. View All Projects");
            System.out.println("3. Update Project Status");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice (1-4): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    createProject();
                    break;
                case "2":
                    viewAllProjects();
                    break;
                case "3":
                    updateProjectStatus();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void createProject() {
        System.out.println("\nCreate New Project");
        System.out.println("------------------");
        
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        
        System.out.print("Enter project description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        System.out.print("Enter target completion date (YYYY-MM-DD): ");
        LocalDate targetDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        String sql = "INSERT INTO projects (project_name, description, start_date, target_date) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            pstmt.setString(2, description);
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(targetDate));
            pstmt.executeUpdate();
            
            System.out.println("\nProject created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating project: " + e.getMessage());
        }
    }
    
    private void viewAllProjects() {
        System.out.println("\nAll Projects");
        System.out.println("------------");
        
        String sql = "SELECT project_id, project_name, start_date, target_date, status " +
                     "FROM projects ORDER BY target_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No projects found.");
                return;
            }
            
            System.out.printf("%-5s %-30s %-12s %-12s %-15s%n", 
                "ID", "Project Name", "Start Date", "Target Date", "Status");
            System.out.println("------------------------------------------------------------------");
            
            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
                Date startDate = rs.getDate("start_date");
                Date targetDate = rs.getDate("target_date");
                String status = rs.getString("status");
                
                System.out.printf("%-5d %-30s %-12s %-12s %-15s%n", 
                    projectId, projectName, startDate, targetDate, status);
            }
            
            System.out.print("\nEnter project ID to view details or 0 to go back: ");
            int projectId = Integer.parseInt(scanner.nextLine());
            
            if (projectId != 0) {
                viewProjectDetails(projectId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving projects: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to project menu.");
        }
    }
    
    private void viewProjectDetails(int projectId) {
        String sql = "SELECT project_name, description, start_date, target_date, status " +
                     "FROM projects WHERE project_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String projectName = rs.getString("project_name");
                String description = rs.getString("description");
                Date startDate = rs.getDate("start_date");
                Date targetDate = rs.getDate("target_date");
                String status = rs.getString("status");
                
                System.out.println("\nProject Details");
                System.out.println("---------------");
                System.out.println("Name: " + projectName);
                System.out.println("Description: " + description);
                System.out.println("Start Date: " + startDate);
                System.out.println("Target Date: " + targetDate);
                System.out.println("Status: " + status);
                
                // Show project tasks
                System.out.println("\nProject Tasks:");
                viewTasksForProject(projectId);
            } else {
                System.out.println("Project not found with ID: " + projectId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving project details: " + e.getMessage());
        }
    }
    
    private void updateProjectStatus() {
        viewAllProjects();
        
        System.out.print("\nEnter project ID to update status (or 0 to cancel): ");
        int projectId = Integer.parseInt(scanner.nextLine());
        
        if (projectId == 0) return;
        
        System.out.println("\nAvailable statuses:");
        System.out.println("1. Not Started");
        System.out.println("2. In Progress");
        System.out.println("3. On Hold");
        System.out.println("4. Completed");
        System.out.print("Enter new status (1-4): ");
        
        String statusChoice = scanner.nextLine();
        String newStatus;
        
        switch (statusChoice) {
            case "1":
                newStatus = "Not Started";
                break;
            case "2":
                newStatus = "In Progress";
                break;
            case "3":
                newStatus = "On Hold";
                break;
            case "4":
                newStatus = "Completed";
                break;
            default:
                System.out.println("Invalid choice. No changes made.");
                return;
        }
        
        String sql = "UPDATE projects SET status = ? WHERE project_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, projectId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nProject status updated successfully!");
            } else {
                System.out.println("\nProject not found with ID: " + projectId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating project status: " + e.getMessage());
        }
    }
    
    private void manageTasks() {
        while (true) {
            System.out.println("\nTask Management:");
            System.out.println("1. Create Task");
            System.out.println("2. View All Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Assign Task");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice (1-5): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    createTask();
                    break;
                case "2":
                    viewAllTasks();
                    break;
                case "3":
                    updateTaskStatus();
                    break;
                case "4":
                    assignTask();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void createTask() {
        System.out.println("\nCreate New Task");
        System.out.println("---------------");
        
        viewAllProjects();
        
        System.out.print("\nEnter project ID for this task: ");
        int projectId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter task name: ");
        String taskName = scanner.nextLine();
        
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        
        System.out.println("Select priority:");
        System.out.println("1. Low");
        System.out.println("2. Medium");
        System.out.println("3. High");
        System.out.print("Enter choice (1-3): ");
        String priorityChoice = scanner.nextLine();
        String priority;
        
        switch (priorityChoice) {
            case "1":
                priority = "Low";
                break;
            case "2":
                priority = "Medium";
                break;
            case "3":
                priority = "High";
                break;
            default:
                System.out.println("Invalid choice. Setting to Medium.");
                priority = "Medium";
        }
        
        System.out.print("Enter due date (YYYY-MM-DD): ");
        LocalDate dueDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
        
        String sql = "INSERT INTO tasks (project_id, task_name, description, priority, due_date) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setString(2, taskName);
            pstmt.setString(3, description);
            pstmt.setString(4, priority);
            pstmt.setDate(5, Date.valueOf(dueDate));
            pstmt.executeUpdate();
            
            System.out.println("\nTask created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating task: " + e.getMessage());
        }
    }
    
    private void viewAllTasks() {
        System.out.println("\nAll Tasks");
        System.out.println("---------");
        
        String sql = "SELECT t.task_id, t.task_name, p.project_name, t.priority, t.due_date, t.status, " +
                     "u.username AS assigned_to FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.project_id " +
                     "LEFT JOIN users u ON t.assigned_to = u.user_id " +
                     "ORDER BY t.due_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No tasks found.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-20s %-8s %-12s %-15s %-15s%n", 
                "ID", "Task Name", "Project", "Priority", "Due Date", "Status", "Assigned To");
            System.out.println("--------------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String projectName = rs.getString("project_name");
                String priority = rs.getString("priority");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("status");
                String assignedTo = rs.getString("assigned_to");
                
                System.out.printf("%-5d %-25s %-20s %-8s %-12s %-15s %-15s%n", 
                    taskId, taskName, projectName, priority, dueDate, status, 
                    assignedTo != null ? assignedTo : "Unassigned");
            }
            
            System.out.print("\nEnter task ID to view details or 0 to go back: ");
            int taskId = Integer.parseInt(scanner.nextLine());
            
            if (taskId != 0) {
                viewTaskDetails(taskId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to task menu.");
        }
    }
    
    private void viewTasksForProject(int projectId) {
        String sql = "SELECT t.task_id, t.task_name, t.priority, t.due_date, t.status, " +
                     "u.username AS assigned_to FROM tasks t " +
                     "LEFT JOIN users u ON t.assigned_to = u.user_id " +
                     "WHERE t.project_id = ? ORDER BY t.due_date";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No tasks found for this project.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-8s %-12s %-15s %-15s%n", 
                "ID", "Task Name", "Priority", "Due Date", "Status", "Assigned To");
            System.out.println("----------------------------------------------------------------------");
            
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String priority = rs.getString("priority");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("status");
                String assignedTo = rs.getString("assigned_to");
                
                System.out.printf("%-5d %-25s %-8s %-12s %-15s %-15s%n", 
                    taskId, taskName, priority, dueDate, status, 
                    assignedTo != null ? assignedTo : "Unassigned");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving project tasks: " + e.getMessage());
        }
    }
    
    private void viewTaskDetails(int taskId) {
        String sql = "SELECT t.task_name, t.description, p.project_name, t.priority, " +
                     "t.due_date, t.status, u.username AS assigned_to FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.project_id " +
                     "LEFT JOIN users u ON t.assigned_to = u.user_id " +
                     "WHERE t.task_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String taskName = rs.getString("task_name");
                String description = rs.getString("description");
                String projectName = rs.getString("project_name");
                String priority = rs.getString("priority");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("status");
                String assignedTo = rs.getString("assigned_to");
                
                System.out.println("\nTask Details");
                System.out.println("------------");
                System.out.println("Name: " + taskName);
                System.out.println("Description: " + description);
                System.out.println("Project: " + projectName);
                System.out.println("Priority: " + priority);
                System.out.println("Due Date: " + dueDate);
                System.out.println("Status: " + status);
                System.out.println("Assigned To: " + (assignedTo != null ? assignedTo : "Unassigned"));
            } else {
                System.out.println("Task not found with ID: " + taskId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving task details: " + e.getMessage());
        }
    }
    
    private void updateTaskStatus() {
        viewAllTasks();
        
        System.out.print("\nEnter task ID to update status (or 0 to cancel): ");
        int taskId = Integer.parseInt(scanner.nextLine());
        
        if (taskId == 0) return;
        
        System.out.println("\nAvailable statuses:");
        System.out.println("1. Not Started");
        System.out.println("2. In Progress");
        System.out.println("3. On Hold");
        System.out.println("4. Completed");
        System.out.print("Enter new status (1-4): ");
        
        String statusChoice = scanner.nextLine();
        String newStatus;
        
        switch (statusChoice) {
            case "1":
                newStatus = "Not Started";
                break;
            case "2":
                newStatus = "In Progress";
                break;
            case "3":
                newStatus = "On Hold";
                break;
            case "4":
                newStatus = "Completed";
                break;
            default:
                System.out.println("Invalid choice. No changes made.");
                return;
        }
        
        String sql = "UPDATE tasks SET status = ? WHERE task_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nTask status updated successfully!");
            } else {
                System.out.println("\nTask not found with ID: " + taskId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating task status: " + e.getMessage());
        }
    }
    
    private void assignTask() {
        viewAllTasks();
        
        System.out.print("\nEnter task ID to assign (or 0 to cancel): ");
        int taskId = Integer.parseInt(scanner.nextLine());
        
        if (taskId == 0) return;
        
        viewAllUsers();
        
        System.out.print("\nEnter user ID to assign to this task: ");
        int userId = Integer.parseInt(scanner.nextLine());
        
        String sql = "UPDATE tasks SET assigned_to = ? WHERE task_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, taskId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nTask assigned successfully!");
            } else {
                System.out.println("\nFailed to assign task. Task or user may not exist.");
            }
        } catch (SQLException e) {
            System.err.println("Error assigning task: " + e.getMessage());
        }
    }
    
    private void generateReports() {
        while (true) {
            System.out.println("\nReport Generation:");
            System.out.println("1. Project Summary");
            System.out.println("2. Task Summary by Status");
            System.out.println("3. Overdue Tasks");
            System.out.println("4. User Workload");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice (1-5): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    generateProjectSummary();
                    break;
                case "2":
                    generateTaskStatusReport();
                    break;
                case "3":
                    generateOverdueTasksReport();
                    break;
                case "4":
                    generateUserWorkloadReport();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void generateProjectSummary() {
        System.out.println("\nProject Summary Report");
        System.out.println("----------------------");
        
        String sql = "SELECT p.project_id, p.project_name, p.status, p.target_date, " +
                     "COUNT(t.task_id) AS total_tasks, " +
                     "SUM(CASE WHEN t.status = 'Completed' THEN 1 ELSE 0 END) AS completed_tasks " +
                     "FROM projects p LEFT JOIN tasks t ON p.project_id = t.project_id " +
                     "GROUP BY p.project_id, p.project_name, p.status, p.target_date " +
                     "ORDER BY p.target_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No projects found.");
                return;
            }
            
            System.out.printf("%-5s %-30s %-15s %-12s %-15s %-15s%n", 
                "ID", "Project Name", "Status", "Target Date", "Total Tasks", "Completed Tasks");
            System.out.println("----------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
                String status = rs.getString("status");
                Date targetDate = rs.getDate("target_date");
                int totalTasks = rs.getInt("total_tasks");
                int completedTasks = rs.getInt("completed_tasks");
                
                System.out.printf("%-5d %-30s %-15s %-12s %-15d %-15d%n", 
                    projectId, projectName, status, targetDate, totalTasks, completedTasks);
            }
        } catch (SQLException e) {
            System.err.println("Error generating project summary: " + e.getMessage());
        }
    }
    
    private void generateTaskStatusReport() {
        System.out.println("\nTask Status Report");
        System.out.println("------------------");
        
        String sql = "SELECT status, COUNT(*) AS task_count FROM tasks GROUP BY status";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No tasks found.");
                return;
            }
            
            System.out.printf("%-15s %-10s%n", "Status", "Task Count");
            System.out.println("---------------------");
            
            while (rs.next()) {
                String status = rs.getString("status");
                int taskCount = rs.getInt("task_count");
                
                System.out.printf("%-15s %-10d%n", status, taskCount);
            }
        } catch (SQLException e) {
            System.err.println("Error generating task status report: " + e.getMessage());
        }
    }
    
    private void generateOverdueTasksReport() {
        System.out.println("\nOverdue Tasks Report");
        System.out.println("--------------------");
        
        String sql = "SELECT t.task_id, t.task_name, p.project_name, t.due_date, t.status, " +
                     "u.username AS assigned_to FROM tasks t " +
                     "LEFT JOIN projects p ON t.project_id = p.project_id " +
                     "LEFT JOIN users u ON t.assigned_to = u.user_id " +
                     "WHERE t.due_date < CURRENT_DATE AND t.status != 'Completed' " +
                     "ORDER BY t.due_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No overdue tasks found.");
                return;
            }
            
            System.out.printf("%-5s %-25s %-20s %-12s %-15s %-15s%n", 
                "ID", "Task Name", "Project", "Due Date", "Status", "Assigned To");
            System.out.println("----------------------------------------------------------------------");
            
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String projectName = rs.getString("project_name");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("status");
                String assignedTo = rs.getString("assigned_to");
                
                System.out.printf("%-5d %-25s %-20s %-12s %-15s %-15s%n", 
                    taskId, taskName, projectName, dueDate, status, 
                    assignedTo != null ? assignedTo : "Unassigned");
            }
        } catch (SQLException e) {
            System.err.println("Error generating overdue tasks report: " + e.getMessage());
        }
    }
    
    private void generateUserWorkloadReport() {
        System.out.println("\nUser Workload Report");
        System.out.println("--------------------");
        
        String sql = "SELECT u.user_id, u.username, " +
                     "COUNT(t.task_id) AS total_tasks, " +
                     "SUM(CASE WHEN t.status = 'Completed' THEN 1 ELSE 0 END) AS completed_tasks, " +
                     "SUM(CASE WHEN t.due_date < CURRENT_DATE AND t.status != 'Completed' THEN 1 ELSE 0 END) AS overdue_tasks " +
                     "FROM users u LEFT JOIN tasks t ON u.user_id = t.assigned_to " +
                     "GROUP BY u.user_id, u.username " +
                     "ORDER BY total_tasks DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n", 
                "ID", "Username", "Total Tasks", "Completed", "Overdue");
            System.out.println("------------------------------------------------------------");
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                int totalTasks = rs.getInt("total_tasks");
                int completedTasks = rs.getInt("completed_tasks");
                int overdueTasks = rs.getInt("overdue_tasks");
                
                System.out.printf("%-5d %-20s %-15d %-15d %-15d%n", 
                    userId, username, totalTasks, completedTasks, overdueTasks);
            }
        } catch (SQLException e) {
            System.err.println("Error generating user workload report: " + e.getMessage());
        }
    }
}
