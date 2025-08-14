import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class BlogManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/blog_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "2002";
    
    private Connection connection;
    private Scanner scanner;
    
    public BlogManager() {
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
        String createPostsTable = "CREATE TABLE IF NOT EXISTS posts (" +
                                "id SERIAL PRIMARY KEY, " +
                                "title VARCHAR(100) NOT NULL, " +
                                "content TEXT NOT NULL, " +
                                "author VARCHAR(50) NOT NULL, " +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPostsTable);
        }
    }
    
    public static void main(String[] args) {
        BlogManager manager = new BlogManager();
        manager.run();
    }
    
    public void run() {
        System.out.println("Welcome to Blog Manager CLI");
        
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    createPost();
                    break;
                case "2":
                    viewPosts();
                    break;
                case "3":
                    editPost();
                    break;
                case "4":
                    deletePost();
                    break;
                case "5":
                    System.out.println("Exiting Blog Manager. Goodbye!");
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
        System.out.println("1. Create a new post");
        System.out.println("2. View all posts");
        System.out.println("3. Edit a post");
        System.out.println("4. Delete a post");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }
    
    private void createPost() {
        System.out.println("\nCreate New Post");
        System.out.println("--------------");
        
        System.out.print("Enter post title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter your name (author): ");
        String author = scanner.nextLine();
        
        System.out.println("Enter post content (type 'END' on a new line to finish):");
        StringBuilder content = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
            content.append(line).append("\n");
        }
        
        String sql = "INSERT INTO posts (title, content, author) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content.toString());
            pstmt.setString(3, author);
            pstmt.executeUpdate();
            
            System.out.println("\nPost created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating post: " + e.getMessage());
        }
    }
    
    private void viewPosts() {
        System.out.println("\nAll Blog Posts");
        System.out.println("--------------");
        
        String sql = "SELECT id, title, author, created_at FROM posts ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No posts found.");
                return;
            }
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                Timestamp createdAt = rs.getTimestamp("created_at");
                
                System.out.printf("[ID: %d] %s by %s (%s)%n", 
                    id, title, author, createdAt.toLocalDateTime());
            }
            
            System.out.print("\nEnter post ID to view details or 0 to go back: ");
            int postId = Integer.parseInt(scanner.nextLine());
            
            if (postId != 0) {
                viewPostDetails(postId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving posts: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }
    }
    
    private void viewPostDetails(int postId) {
        String sql = "SELECT title, content, author, created_at, updated_at FROM posts WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String title = rs.getString("title");
                String content = rs.getString("content");
                String author = rs.getString("author");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                
                System.out.println("\nPost Details");
                System.out.println("------------");
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Created: " + createdAt);
                System.out.println("Last Updated: " + updatedAt);
                System.out.println("\nContent:");
                System.out.println(content);
            } else {
                System.out.println("Post not found with ID: " + postId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving post details: " + e.getMessage());
        }
    }
    
    private void editPost() {
        System.out.println("\nEdit Post");
        System.out.println("---------");
        
        viewPosts(); // Show posts first
        
        System.out.print("\nEnter ID of post to edit (or 0 to cancel): ");
        int postId = Integer.parseInt(scanner.nextLine());
        
        if (postId == 0) return;
        
        String sql = "SELECT title, content FROM posts WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String currentTitle = rs.getString("title");
                String currentContent = rs.getString("content");
                
                System.out.println("\nCurrent Title: " + currentTitle);
                System.out.print("Enter new title (leave blank to keep current): ");
                String newTitle = scanner.nextLine();
                
                System.out.println("\nCurrent Content:");
                System.out.println(currentContent);
                System.out.println("\nEnter new content (type 'END' on a new line to finish, leave blank to keep current):");
                StringBuilder newContent = new StringBuilder();
                String line;
                while (!(line = scanner.nextLine()).isEmpty() && !line.equalsIgnoreCase("END")) {
                    newContent.append(line).append("\n");
                }
                
                // Only update if there are changes
                if (!newTitle.isEmpty() || newContent.length() > 0) {
                    String updateSql = "UPDATE posts SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                    
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, newTitle.isEmpty() ? currentTitle : newTitle);
                        updateStmt.setString(2, newContent.length() == 0 ? currentContent : newContent.toString());
                        updateStmt.setInt(3, postId);
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\nPost updated successfully!");
                        } else {
                            System.out.println("\nFailed to update post.");
                        }
                    }
                } else {
                    System.out.println("\nNo changes made.");
                }
            } else {
                System.out.println("Post not found with ID: " + postId);
            }
        } catch (SQLException e) {
            System.err.println("Error editing post: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }
    }
    
    private void deletePost() {
        System.out.println("\nDelete Post");
        System.out.println("-----------");
        
        viewPosts(); // Show posts first
        
        System.out.print("\nEnter ID of post to delete (or 0 to cancel): ");
        int postId = Integer.parseInt(scanner.nextLine());
        
        if (postId == 0) return;
        
        System.out.print("Are you sure you want to delete this post? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes")) {
            String sql = "DELETE FROM posts WHERE id = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, postId);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("\nPost deleted successfully!");
                } else {
                    System.out.println("\nPost not found with ID: " + postId);
                }
            } catch (SQLException e) {
                System.err.println("Error deleting post: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}
