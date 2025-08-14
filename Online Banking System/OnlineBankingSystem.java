import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class OnlineBankingSystem {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/banking";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "2002";
    
    private Connection connection;
    private Scanner scanner;
    
    public OnlineBankingSystem() {
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
        String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                                    "account_number VARCHAR(10) PRIMARY KEY, " +
                                    "first_name VARCHAR(50) NOT NULL, " +
                                    "last_name VARCHAR(50) NOT NULL, " +
                                    "pin VARCHAR(4) NOT NULL, " +
                                    "balance DECIMAL(10, 2) NOT NULL DEFAULT 0)";
        
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                                       "id SERIAL PRIMARY KEY, " +
                                       "account_number VARCHAR(10) REFERENCES accounts(account_number), " +
                                       "transaction_type VARCHAR(20) NOT NULL, " +
                                       "amount DECIMAL(10, 2) NOT NULL, " +
                                       "timestamp TIMESTAMP NOT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createAccountsTable);
            stmt.execute(createTransactionsTable);
        }
    }
    
    public static void main(String[] args) {
        OnlineBankingSystem bank = new OnlineBankingSystem();
        bank.run();
    }
    
    public void run() {
        System.out.println("Welcome to Online Banking System");
        
        while (true) {
            displayMainMenu();
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    Account account = login();
                    if (account != null) {
                        accountMenu(account);
                    } else {
                        System.out.println("Invalid account number or PIN.");
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using our banking system. Goodbye!");
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
        System.out.println("1. Create New Account");
        System.out.println("2. Login to Account");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void accountMenu(Account account) {
        while (true) {
            System.out.println("\nAccount Menu:");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Change PIN");
            System.out.println("6. View Account Info");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    deposit(account);
                    break;
                case 2:
                    withdraw(account);
                    break;
                case 3:
                    transfer(account);
                    break;
                case 4:
                    viewTransactionHistory(account);
                    break;
                case 5:
                    changePin(account);
                    break;
                case 6:
                    account.display();
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }
    
    private double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter a valid amount.");
            scanner.next();
        }
        return scanner.nextDouble();
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    private boolean isValidPin(String pin) {
        return pin.matches("\\d{4}");
    }
    
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }
    
    public void createAccount() {
        System.out.println("\nCreate New Account");
        System.out.println("-----------------");
        
        String firstName;
        do {
            System.out.print("Enter First Name: ");
            firstName = scanner.nextLine();
            if (!isValidName(firstName)) {
                System.out.println("Invalid name. Please use letters only.");
            }
        } while (!isValidName(firstName));
        
        String lastName;
        do {
            System.out.print("Enter Last Name: ");
            lastName = scanner.nextLine();
            if (!isValidName(lastName)) {
                System.out.println("Invalid name. Please use letters only.");
            }
        } while (!isValidName(lastName));
        
        String pin;
        do {
            System.out.print("Create a 4-digit PIN: ");
            pin = scanner.nextLine();
            if (!isValidPin(pin)) {
                System.out.println("Invalid PIN. Please enter exactly 4 digits.");
            }
        } while (!isValidPin(pin));
        
        String accountNumber;
        boolean accountExists;
        do {
            accountNumber = generateAccountNumber();
            accountExists = checkAccountExists(accountNumber);
        } while (accountExists);
        
        String sql = "INSERT INTO accounts (account_number, first_name, last_name, pin) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, pin);
            pstmt.executeUpdate();
            
            System.out.println("\nAccount created successfully!");
            System.out.println("Your account number is: " + accountNumber);
            System.out.println("Please remember this number for future access.");
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }
    }
    
    private boolean checkAccountExists(String accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking account: " + e.getMessage());
            return false;
        }
    }
    
    public Account login() {
        System.out.println("\nLogin to Your Account");
        System.out.println("--------------------");
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, pin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                double balance = rs.getDouble("balance");
                return new Account(accountNumber, firstName, lastName, pin, balance);
            }
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
        
        return null;
    }
    
    public void deposit(Account account) {
        System.out.println("\nDeposit Money");
        System.out.println("-------------");
        System.out.println("Current Balance: $" + account.getBalance());
        System.out.print("Enter amount to deposit: $");
        double amount = getDoubleInput();
        scanner.nextLine(); // Consume newline
        
        if (amount <= 0) {
            System.out.println("Invalid amount. Deposit failed.");
            return;
        }
        
        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, account.getAccountNumber());
                pstmt.executeUpdate();
            }
            
            logTransaction(account.getAccountNumber(), "DEPOSIT", amount);
            
            connection.commit();
            account.setBalance(account.getBalance() + amount);
            System.out.println("Deposit successful. New balance: $" + account.getBalance());
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error depositing money: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    
    public void withdraw(Account account) {
        System.out.println("\nWithdraw Money");
        System.out.println("--------------");
        System.out.println("Current Balance: $" + account.getBalance());
        System.out.print("Enter amount to withdraw: $");
        double amount = getDoubleInput();
        scanner.nextLine(); // Consume newline
        
        if (amount <= 0) {
            System.out.println("Invalid amount. Withdrawal failed.");
            return;
        }
        
        if (account.getBalance() < amount) {
            System.out.println("Insufficient funds. Withdrawal failed.");
            return;
        }
        
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, account.getAccountNumber());
                pstmt.executeUpdate();
            }
            
            logTransaction(account.getAccountNumber(), "WITHDRAWAL", -amount);
            
            connection.commit();
            account.setBalance(account.getBalance() - amount);
            System.out.println("Withdrawal successful. New balance: $" + account.getBalance());
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error withdrawing money: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    
    public void transfer(Account fromAccount) {
        System.out.println("\nTransfer Money");
        System.out.println("--------------");
        System.out.println("Your Account Balance: $" + fromAccount.getBalance());
        System.out.print("Enter recipient's account number: ");
        String toAccountNumber = scanner.nextLine();
        System.out.print("Enter amount to transfer: $");
        double amount = getDoubleInput();
        scanner.nextLine(); // Consume newline
        
        if (amount <= 0) {
            System.out.println("Invalid amount. Transfer failed.");
            return;
        }
        
        if (fromAccount.getBalance() < amount) {
            System.out.println("Insufficient funds. Transfer failed.");
            return;
        }
        
        if (fromAccount.getAccountNumber().equals(toAccountNumber)) {
            System.out.println("Cannot transfer to the same account. Transfer failed.");
            return;
        }
        
        String checkSql = "SELECT 1 FROM accounts WHERE account_number = ?";
        String updateFromSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String updateToSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        
        try {
            connection.setAutoCommit(false);
            
            // Check if recipient account exists
            try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
                pstmt.setString(1, toAccountNumber);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("Recipient account not found. Transfer failed.");
                    connection.rollback();
                    return;
                }
            }
            
            // Deduct from sender's account
            try (PreparedStatement pstmt = connection.prepareStatement(updateFromSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, fromAccount.getAccountNumber());
                pstmt.executeUpdate();
            }
            
            // Add to recipient's account
            try (PreparedStatement pstmt = connection.prepareStatement(updateToSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, toAccountNumber);
                pstmt.executeUpdate();
            }
            
            // Log transactions
            logTransaction(fromAccount.getAccountNumber(), "TRANSFER_TO_" + toAccountNumber, -amount);
            logTransaction(toAccountNumber, "TRANSFER_FROM_" + fromAccount.getAccountNumber(), amount);
            
            connection.commit();
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            System.out.println("Transfer successful.");
            System.out.println("Your new balance: $" + fromAccount.getBalance());
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error transferring money: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    
    private void logTransaction(String accountNumber, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, timestamp) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        }
    }
    
    public void viewTransactionHistory(Account account) {
        System.out.println("\nTransaction History for Account: " + account.getAccountNumber());
        System.out.println("--------------------------------------------");
        
        String sql = "SELECT transaction_type, amount, timestamp FROM transactions " +
                     "WHERE account_number = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No transactions yet.");
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                
                System.out.printf("%s | %s | $%.2f%n", 
                    timestamp.toLocalDateTime().format(formatter), 
                    type, 
                    amount);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
        }
    }
    
    public void changePin(Account account) {
        System.out.println("\nChange PIN");
        System.out.println("----------");
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();
        
        if (!currentPin.equals(account.getPin())) {
            System.out.println("Incorrect PIN. Operation cancelled.");
            return;
        }
        
        String newPin;
        do {
            System.out.print("Enter new 4-digit PIN: ");
            newPin = scanner.nextLine();
            if (!isValidPin(newPin)) {
                System.out.println("Invalid PIN. Please enter exactly 4 digits.");
            }
        } while (!isValidPin(newPin));
        
        String sql = "UPDATE accounts SET pin = ? WHERE account_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPin);
            pstmt.setString(2, account.getAccountNumber());
            pstmt.executeUpdate();
            
            account.setPin(newPin);
            System.out.println("PIN changed successfully.");
        } catch (SQLException e) {
            System.err.println("Error changing PIN: " + e.getMessage());
        }
    }
    
    static class Account {
        private String accountNumber;
        private String firstName;
        private String lastName;
        private String pin;
        private double balance;
        
        public Account(String accountNumber, String firstName, String lastName, String pin, double balance) {
            this.accountNumber = accountNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.pin = pin;
            this.balance = balance;
        }
        
        public String getAccountNumber() {
            return accountNumber;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public String getPin() {
            return pin;
        }
        
        public void setPin(String pin) {
            this.pin = pin;
        }
        
        public double getBalance() {
            return balance;
        }
        
        public void setBalance(double balance) {
            this.balance = balance;
        }
        
        public void display() {
            System.out.println("\nAccount Number: " + accountNumber);
            System.out.println("Account Holder: " + firstName + " " + lastName);
            System.out.printf("Current Balance: $%.2f%n", balance);
        }
    }
}
