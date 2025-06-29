import java.util.*;
import java.io.*;

class Contact implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthday;
    private String notes;

    public Contact(String name, String phone, String email, String address, LocalDate birthday, String notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthday = birthday;
        this.notes = notes;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public LocalDate getBirthday() { return birthday; }
    public String getNotes() { return notes; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format(
            "👤 %s\n☎️ %s\n✉️ %s\n🏠 %s\n🎂 %s\n📝 %s\n",
            name, phone, email, address, 
            birthday != null ? birthday.format(DateTimeFormatter.ISO_DATE) : "Not specified",
            notes != null ? notes : "No notes"
        );
    }
}

public class AddressBook {
    private static final String DATA_FILE = "addressbook.dat";
    private static Map<String, Contact> contacts = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        loadContacts();
        
        System.out.println("📒 ADDRESS BOOK APPLICATION");
        System.out.println("---------------------------");

        while (true) {
            showMainMenu();
        }
    }

    private static void showMainMenu() {
        System.out.println("\n🔹 MAIN MENU");
        System.out.println("1. Add New Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contacts");
        System.out.println("4. Edit Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Save & Exit");
        System.out.print("Choose an option (1-6): ");

        int choice = getIntInput(1, 6);
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: addContact(); break;
            case 2: viewAllContacts(); break;
            case 3: searchContacts(); break;
            case 4: editContact(); break;
            case 5: deleteContact(); break;
            case 6: saveAndExit(); break;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void addContact() {
        System.out.println("\n➕ ADD NEW CONTACT");
        
        System.out.print("Name: ");
        String name = scanner.nextLine();
        
        if (contacts.containsKey(name.toLowerCase())) {
            System.out.println("❌ A contact with this name already exists.");
            return;
        }
        
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Address: ");
        String address = scanner.nextLine();
        
        LocalDate birthday = null;
        System.out.print("Birthday (YYYY-MM-DD, leave blank if unknown): ");
        String birthdayStr = scanner.nextLine();
        if (!birthdayStr.isEmpty()) {
            try {
                birthday = LocalDate.parse(birthdayStr, dateFormatter);
            } catch (Exception e) {
                System.out.println("⚠️ Invalid date format. Birthday not set.");
            }
        }
        
        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        contacts.put(name.toLowerCase(), new Contact(name, phone, email, address, birthday, notes));
        System.out.println("\n✅ Contact added successfully!");
    }

    private static void viewAllContacts() {
        System.out.println("\n👥 ALL CONTACTS (" + contacts.size() + ")");
        if (contacts.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }
        
        contacts.values().stream()
            .sorted(Comparator.comparing(Contact::getName))
            .forEach(System.out::println);
    }

    private static void searchContacts() {
        System.out.println("\n🔍 SEARCH CONTACTS");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Phone");
        System.out.println("3. Search by Email");
        System.out.print("Choose search method (1-3): ");
        
        int method = getIntInput(1, 3);
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter search term: ");
        String term = scanner.nextLine().toLowerCase();
        
        System.out.println("\n🔎 SEARCH RESULTS:");
        boolean found = false;
        
        for (Contact contact : contacts.values()) {
            boolean match = false;
            switch (method) {
                case 1: match = contact.getName().toLowerCase().contains(term); break;
                case 2: match = contact.getPhone().toLowerCase().contains(term); break;
                case 3: match = contact.getEmail().toLowerCase().contains(term); break;
            }
            
            if (match) {
                System.out.println(contact);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("❌ No matching contacts found.");
        }
    }

    private static void editContact() {
        System.out.println("\n✏️ EDIT CONTACT");
        System.out.print("Enter name of contact to edit: ");
        String name = scanner.nextLine().toLowerCase();
        
        if (!contacts.containsKey(name)) {
            System.out.println("❌ Contact not found.");
            return;
        }
        
        Contact contact = contacts.get(name);
        System.out.println("\nCurrent contact details:");
        System.out.println(contact);
        
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Phone");
        System.out.println("2. Email");
        System.out.println("3. Address");
        System.out.println("4. Birthday");
        System.out.println("5. Notes");
        System.out.println("6. Cancel");
        System.out.print("Choose field to edit (1-6): ");
        
        int choice = getIntInput(1, 6);
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                System.out.print("New phone: ");
                contact.setPhone(scanner.nextLine());
                break;
            case 2:
                System.out.print("New email: ");
                contact.setEmail(scanner.nextLine());
                break;
            case 3:
                System.out.print("New address: ");
                contact.setAddress(scanner.nextLine());
                break;
            case 4:
                System.out.print("New birthday (YYYY-MM-DD): ");
                try {
                    contact.setBirthday(LocalDate.parse(scanner.nextLine(), dateFormatter));
                } catch (Exception e) {
                    System.out.println("⚠️ Invalid date format. Birthday not changed.");
                }
                break;
            case 5:
                System.out.print("New notes: ");
                contact.setNotes(scanner.nextLine());
                break;
            case 6:
                return;
        }
        
        System.out.println("\n✅ Contact updated successfully!");
    }

    private static void deleteContact() {
        System.out.println("\n❌ DELETE CONTACT");
        System.out.print("Enter name of contact to delete: ");
        String name = scanner.nextLine().toLowerCase();
        
        if (!contacts.containsKey(name)) {
            System.out.println("❌ Contact not found.");
            return;
        }
        
        System.out.println("\nContact to delete:");
        System.out.println(contacts.get(name));
        System.out.print("Are you sure you want to delete this contact? (y/n): ");
        String confirmation = scanner.nextLine().toLowerCase();
        
        if (confirmation.equals("y")) {
            contacts.remove(name);
            System.out.println("\n✅ Contact deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void saveAndExit() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(contacts);
            System.out.println("\n💾 Contacts saved successfully. Goodbye!");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("⚠️ Error saving contacts: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadContacts() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                contacts = (Map<String, Contact>) ois.readObject();
                System.out.println("📂 Loaded " + contacts.size() + " contacts from file.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("⚠️ Error loading contacts: " + e.getMessage());
            }
        }
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.printf("⚠️ Please enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Please enter a number: ");
                scanner.next();
            }
        }
    }
}
