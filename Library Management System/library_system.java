import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class Book {
    String bookId;
    String title;
    String author;
    String genre;
    boolean isAvailable;
    LocalDate dueDate;
    String borrowedBy; // Track which member borrowed it

    public Book(String bookId, String title, String author, String genre) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isAvailable = true;
        this.dueDate = null;
        this.borrowedBy = null;
    }

    @Override
    public String toString() {
        return String.format(
            "📖 ID: %s | Title: %s | Author: %s | Genre: %s | Status: %s",
            bookId, title, author, genre, 
            isAvailable ? "✅ Available" : "❌ Borrowed by: " + borrowedBy + " (Due: " + dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ")"
        );
    }
}

class Member {
    String memberId;
    String name;
    String email;
    String phone;
    List<String> borrowedBooks;
    double totalFines;

    public Member(String memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowedBooks = new ArrayList<>();
        this.totalFines = 0.0;
    }

    @Override
    public String toString() {
        return String.format(
            "👤 ID: %s | Name: %s | Email: %s | Phone: %s | Borrowed: %d | Fines: $%.2f",
            memberId, name, email, phone, borrowedBooks.size(), totalFines
        );
    }
}

class Librarian {
    String username;
    String password;
    String name;

    public Librarian(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}

public class EnhancedLibrarySystem {
    private static HashMap<String, Book> books = new HashMap<>();
    private static HashMap<String, Member> members = new HashMap<>();
    private static HashMap<String, Librarian> librarians = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final double DAILY_FINE = 0.50; // $0.50 per day overdue
    private static Librarian currentLibrarian = null;

    public static void main(String[] args) {
        initializeSampleData();
        
        System.out.println("📚 ENHANCED LIBRARY MANAGEMENT SYSTEM 📚");
        System.out.println("-----------------------------------------");

        // Login before accessing system
        librarianLogin();

        while (true) {
            showMainMenu();
        }
    }

    private static void initializeSampleData() {
        // Sample Books
        books.put("B001", new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", "Classic"));
        books.put("B002", new Book("B002", "To Kill a Mockingbird", "Harper Lee", "Fiction"));

        // Sample Members
        members.put("M001", new Member("M001", "Alice Johnson", "alice@example.com", "555-1234"));
        members.put("M002", new Member("M002", "Bob Smith", "bob@example.com", "555-5678"));

        // Sample Librarians
        librarians.put("admin", new Librarian("admin", "admin123", "Head Librarian"));
        librarians.put("lisa", new Librarian("Tiku", "Tiku123", "Tikesh Sahu"));
    }

    private static void librarianLogin() {
        System.out.println("\n🔐 LIBRARIAN LOGIN");
        while (currentLibrarian == null) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            Librarian librarian = librarians.get(username);
            if (librarian != null && librarian.password.equals(password)) {
                currentLibrarian = librarian;
                System.out.printf("\n✅ Welcome, %s! (%s)\n", librarian.name, librarian.username);
            } else {
                System.out.println("❌ Invalid credentials. Try again.");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n🔹 MAIN MENU (Logged in as: " + currentLibrarian.name + ")");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrow/Return Operations");
        System.out.println("4. View Overdue Books with Fines");
        System.out.println("5. Pay Fines");
        System.out.println("6. Logout");
        System.out.print("Choose an option (1-6): ");

        int choice = getIntInput(1, 6);
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: showBookMenu(); break;
            case 2: showMemberMenu(); break;
            case 3: showTransactionMenu(); break;
            case 4: viewOverdueBooksWithFines(); break;
            case 5: payFines(); break;
            case 6: logout(); break;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void showBookMenu() {
        System.out.println("\n📚 BOOK MANAGEMENT");
        System.out.println("1. Add New Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Books");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option (1-4): ");

        int choice = getIntInput(1, 4);
        scanner.nextLine();

        switch (choice) {
            case 1: addNewBook(); break;
            case 2: viewAllBooks(); break;
            case 3: searchBook(); break;
            case 4: return;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void showMemberMenu() {
        System.out.println("\n👥 MEMBER MANAGEMENT");
        System.out.println("1. Register New Member");
        System.out.println("2. View All Members");
        System.out.println("3. Search Members");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option (1-4): ");

        int choice = getIntInput(1, 4);
        scanner.nextLine();

        switch (choice) {
            case 1: registerNewMember(); break;
            case 2: viewAllMembers(); break;
            case 3: searchMember(); break;
            case 4: return;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void showTransactionMenu() {
        System.out.println("\n🔄 BORROW/RETURN OPERATIONS");
        System.out.println("1. Borrow a Book");
        System.out.println("2. Return a Book");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose an option (1-3): ");

        int choice = getIntInput(1, 3);
        scanner.nextLine();

        switch (choice) {
            case 1: borrowBook(); break;
            case 2: returnBook(); break;
            case 3: return;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    // ========== BOOK OPERATIONS ==========
    private static void addNewBook() {
        System.out.println("\n➕ ADD NEW BOOK");
        
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        
        if (books.containsKey(bookId)) {
            System.out.println("❌ Book ID already exists.");
            return;
        }
        
        System.out.print("Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Author: ");
        String author = scanner.nextLine();
        
        System.out.print("Genre: ");
        String genre = scanner.nextLine();

        books.put(bookId, new Book(bookId, title, author, genre));
        System.out.println("\n✅ Book added successfully!");
    }

    private static void viewAllBooks() {
        System.out.println("\n📚 ALL BOOKS (" + books.size() + ")");
        if (books.isEmpty()) {
            System.out.println("No books in collection.");
            return;
        }
        books.values().forEach(System.out::println);
    }

    private static void searchBook() {
        System.out.println("\n🔍 SEARCH BOOKS");
        System.out.print("Enter search term (Title/Author/Genre): ");
        String term = scanner.nextLine().toLowerCase();

        boolean found = false;
        for (Book book : books.values()) {
            if (book.title.toLowerCase().contains(term) || 
                book.author.toLowerCase().contains(term) || 
                book.genre.toLowerCase().contains(term)) {
                System.out.println(book);
                found = true;
            }
        }
        
        if (!found) System.out.println("❌ No matching books found.");
    }

    // ========== MEMBER OPERATIONS ==========
    private static void registerNewMember() {
        System.out.println("\n👤 REGISTER NEW MEMBER");
        
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();
        
        if (members.containsKey(memberId)) {
            System.out.println("❌ Member ID already exists.");
            return;
        }
        
        System.out.print("Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        members.put(memberId, new Member(memberId, name, email, phone));
        System.out.println("\n✅ Member registered successfully!");
    }

    private static void viewAllMembers() {
        System.out.println("\n👥 ALL MEMBERS (" + members.size() + ")");
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        members.values().forEach(System.out::println);
    }

    private static void searchMember() {
        System.out.println("\n🔍 SEARCH MEMBERS");
        System.out.print("Enter search term (Name/ID/Email): ");
        String term = scanner.nextLine().toLowerCase();

        boolean found = false;
        for (Member member : members.values()) {
            if (member.name.toLowerCase().contains(term) || 
                member.memberId.toLowerCase().contains(term) || 
                member.email.toLowerCase().contains(term)) {
                System.out.println(member);
                found = true;
            }
        }
        
        if (!found) System.out.println("❌ No matching members found.");
    }

    // ========== TRANSACTION OPERATIONS ==========
    private static void borrowBook() {
        System.out.println("\n📖 BORROW BOOK");
        
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();
        Member member = members.get(memberId);
        
        if (member == null) {
            System.out.println("❌ Member not found.");
            return;
        }
        
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine();
        Book book = books.get(bookId);
        
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }
        
        if (!book.isAvailable) {
            System.out.println("❌ Book is already borrowed.");
            return;
        }
        
        // Set due date (14 days from today)
        LocalDate dueDate = LocalDate.now().plusDays(14);
        book.dueDate = dueDate;
        book.isAvailable = false;
        book.borrowedBy = memberId;
        member.borrowedBooks.add(bookId);
        
        System.out.printf("\n✅ Book borrowed! Due: %s\n", dueDate.format(dateFormatter));
    }

    private static void returnBook() {
        System.out.println("\n📖 RETURN BOOK");
        
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();
        Member member = members.get(memberId);
        
        if (member == null) {
            System.out.println("❌ Member not found.");
            return;
        }
        
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine();
        Book book = books.get(bookId);
        
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }
        
        if (book.isAvailable) {
            System.out.println("❌ This book wasn't borrowed.");
            return;
        }
        
        if (!book.borrowedBy.equals(memberId)) {
            System.out.println("❌ This member didn't borrow this book.");
            return;
        }
        
        // Calculate fine if overdue
        LocalDate today = LocalDate.now();
        double fine = 0.0;
        
        if (today.isAfter(book.dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(book.dueDate, today);
            fine = daysOverdue * DAILY_FINE;
            member.totalFines += fine;
            System.out.printf("⚠️ Overdue by %d days. Fine: $%.2f\n", daysOverdue, fine);
        }
        
        // Return book
        book.isAvailable = true;
        book.dueDate = null;
        book.borrowedBy = null;
        member.borrowedBooks.remove(bookId);
        
        System.out.printf("\n✅ Book returned! %s\n", fine > 0 ? "Fine added to member's account." : "");
    }

    // ========== FINE MANAGEMENT ==========
    private static void viewOverdueBooksWithFines() {
        System.out.println("\n⏰ OVERDUE BOOKS WITH FINES");
        LocalDate today = LocalDate.now();
        boolean found = false;
        
        for (Book book : books.values()) {
            if (!book.isAvailable && today.isAfter(book.dueDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(book.dueDate, today);
                double fine = daysOverdue * DAILY_FINE;
                Member member = members.get(book.borrowedBy);
                
                System.out.printf(
                    "📖 %s | Borrowed by: %s | Due: %s | Overdue: %d days | Fine: $%.2f\n",
                    book.title, member.name, book.dueDate.format(dateFormatter), daysOverdue, fine
                );
                found = true;
            }
        }
        
        if (!found) System.out.println("✅ No overdue books.");
    }

    private static void payFines() {
        System.out.println("\n💰 PAY FINES");
        
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine();
        Member member = members.get(memberId);
        
        if (member == null) {
            System.out.println("❌ Member not found.");
            return;
        }
        
        if (member.totalFines <= 0) {
            System.out.println("✅ This member has no outstanding fines.");
            return;
        }
        
        System.out.printf("Current fines: $%.2f\n", member.totalFines);
        System.out.print("Enter payment amount: $");
        double payment = getDoubleInput(0, member.totalFines);
        
        member.totalFines -= payment;
        System.out.printf("\n✅ Payment of $%.2f received. Remaining fines: $%.2f\n", 
                         payment, member.totalFines);
    }

    // ========== AUTHENTICATION ==========
    private static void logout() {
        System.out.println("\n👋 Logging out " + currentLibrarian.name + "...");
        currentLibrarian = null;
        librarianLogin(); // Return to login screen
    }

    // ========== HELPER METHODS ==========
    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.printf("⚠️ Enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Enter a number: ");
                scanner.next();
            }
        }
    }

    private static double getDoubleInput(double min, double max) {
        while (true) {
            try {
                double input = scanner.nextDouble();
                if (input >= min && input <= max) return input;
                System.out.printf("⚠️ Enter an amount between $%.2f and $%.2f: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Enter a number: ");
                scanner.next();
            }
        }
    }
}
