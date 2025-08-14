![Java version](https://img.shields.io/badge/Java-8%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![GitHub top language](https://img.shields.io/github/languages/top/your-username/address-book-java)

# Address Book Application (Java)

A console-based **Address Book** written in Java that allows you to manage contacts efficiently.  
Contacts are stored locally using Java serialization, so your data is preserved between sessions.

---

## Features

- Add new contacts (Name, Phone, Email, Address, Birthday, Notes)  
- View all contacts (sorted alphabetically)  
- Search contacts by **Name**, **Phone**, or **Email**  
- Edit existing contacts (update individual fields)  
- Delete contacts with confirmation  
- Save and exit (data stored in a `.dat` file)  
- Automatically load saved contacts on startup  

---

## Technologies Used

- **Java SE 8+**  
- **Collections Framework** (`HashMap`, `Comparator`)  
- **Java Time API** (`LocalDate`, `DateTimeFormatter`)  
- **Object Serialization** for data persistence  

---

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/address-book-java.git
cd address-book-java
```

### 2. Compile the Program
```bash
javac AddressBook.java
```

### 3. Run the Application
```bash
java AddressBook
```

---

## Example Output (Indian Version)

```
ADDRESS BOOK APPLICATION
------------------------

MAIN MENU
1. Add New Contact
2. View All Contacts
3. Search Contacts
4. Edit Contact
5. Delete Contact
6. Save & Exit
Choose an option (1-6): 1

ADD NEW CONTACT
Name: Rahul Sharma
Phone: +91-9876543210
Email: rahul.sharma@example.com
Address: 221B MG Road, Bengaluru, Karnataka
Birthday (YYYY-MM-DD, leave blank if unknown): 1995-08-15
Notes: School friend

Contact added successfully!
```

**Viewing Contacts**
```
ALL CONTACTS (1)

👤 Rahul Sharma
☎️ +91-9876543210
✉️ rahul.sharma@example.com
🏠 221B MG Road, Bengaluru, Karnataka
🎂 1995-08-15
📝 School friend
```

**Searching by Name**
```
SEARCH CONTACTS
Choose search method (1-3): 1
Enter search term: rahul

SEARCH RESULTS:

👤 Rahul Sharma
☎️ +91-9876543210
✉️ rahul.sharma@example.com
🏠 221B MG Road, Bengaluru, Karnataka
🎂 1995-08-15
📝 School friend
```

---

## Possible Improvements

We have several ideas for enhancing the functionality and user experience of this project in the future:

- **Export/Import Contacts:**  
  - Export contact data to CSV or JSON for backup or sharing.  
  - Import contacts from CSV or JSON files for easy migration.  

- **Categories or Groups:**  
  - Organize contacts into groups (e.g., Work, Family, Friends).  

- **GUI Version (Swing or JavaFX):**  
  - Develop a graphical user interface for easier and more interactive contact management.  
  - See [Oracle Docs on Swing to JavaFX migration](https://docs.oracle.com/javafx/2/swing/port-to-javafx.htm).  

- **Validation:**  
  - Add strong validation for Indian mobile numbers, PIN codes, and email formats.  

---

## License

This project is licensed under the **MIT License**.  
You are free to use, modify, and distribute this project.

---
