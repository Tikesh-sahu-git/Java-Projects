# Blog Manager (Java + PostgreSQL)

## 📌 Overview
The **Blog Manager** is a Java-based command-line application that allows users to manage blog posts with **Create, Read, Update, and Delete (CRUD)** operations.  
It uses **PostgreSQL** as the backend database.

---

## 🚀 Features
- Create new blog posts with **title, content, and author**
- View all blog posts (with ID, title, author, and timestamp)
- View detailed post content
- Edit posts (title & content)
- Delete posts with confirmation
- Automatically creates the `posts` table if not present

---

## 🛠️ Technologies Used
- **Java** (JDBC)
- **PostgreSQL**
- **SQL**
- **CLI-based interface**

---

## 🗄️ Database Schema
**Table: posts**
```sql
CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ⚙️ Setup Instructions

### 1️⃣ Install PostgreSQL
Make sure PostgreSQL is installed and running.

### 2️⃣ Create Database
```sql
CREATE DATABASE blog_db;
```

### 3️⃣ Configure Database Credentials
Update the connection details inside **BlogManager.java**:
```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/blog_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "your_password";
```

### 4️⃣ Compile and Run
```bash
javac BlogManager.java
java BlogManager
```

---

## 📖 Usage Guide

### Main Menu Options:
1. **Create a new post** → Add a blog entry with title, author, and content  
2. **View all posts** → List all blog posts and select one to view details  
3. **Edit a post** → Update title/content of an existing post  
4. **Delete a post** → Remove a post by ID with confirmation  
5. **Exit** → Close the application  

---

## 📂 Example Workflow
```
Welcome to Blog Manager CLI

Main Menu:
1. Create a new post
2. View all posts
3. Edit a post
4. Delete a post
5. Exit
Enter your choice (1-5): 1

Create New Post
--------------
Enter post title: My First Blog
Enter your name (author): Alice
Enter post content (type 'END' on a new line to finish):
Hello world!
This is my first blog post.
END

Post created successfully!
```

---

## 🔒 Security Notes
- Use **environment variables** for DB credentials instead of hardcoding.  
- Sanitize inputs to prevent SQL injection.  
- Use **prepared statements** (already implemented).  

---

## 👨‍💻 Author
Developed by *[Your Name]*  
📧 Email: your.email@example.com  
