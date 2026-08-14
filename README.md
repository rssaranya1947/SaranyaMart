# SaranyaMart - Multi-Seller E-Commerce Website (Week 1)

**SaranyaMart** is an online shopping web application built **100% using Java**. It supports multi-role access for **Buyers**, **Sellers**, and **Admins**.

**Repository Owner & Developer**: [rssaranya1947](https://github.com/rssaranya1947)

---

## 🚀 Week 1 Milestones Completed
- [x] **Project Repository & Architecture**: Java project structure with `pom.xml`, `.gitignore`, `README.md`.
- [x] **Database Engine & Schema**: SQLite database integration via JDBC (`saranyamart.db` & `db/schema.sql`).
- [x] **User Registration**: Support for **Buyer** and **Seller** role registrations with email uniqueness validation and password strength checks.
- [x] **User Authentication & Login**: Role-aware login engine supporting **Buyer**, **Seller**, and pre-seeded **Admin**.
- [x] **Pre-seeded Admin & Test Accounts**:
  - **Admin**: `admin@saranyamart.com` | Password: `Admin@123`
  - **Seller**: `seller@saranyamart.com` | Password: `Seller@123`
  - **Buyer**: `buyer@saranyamart.com` | Password: `Buyer@123`
- [x] **Frontend Web Interface**: Responsive HTML5/CSS3/JavaScript SPA with dynamic role views and modal login/registration dialogues.

---

## 🛠️ Technology Stack
- **Backend Language**: Java (Java 17 JDK)
- **HTTP Web Server**: Embedded Java HTTP Server (`com.sun.net.httpserver.HttpServer`)
- **Database**: Pure Java Storage Engine & SQLite schema (`db/schema.sql`)
- **Security**: SHA-256 password hashing with salt (`PasswordUtil`)
- **Frontend**: HTML5, Vanilla CSS3 (Indigo & Emerald glassmorphism design system), JavaScript (Fetch API)

---

## 💻 How to Run SaranyaMart Live

### Method 1: Using Standard Java Compiler (`javac`)
```bash
# 1. Compile all Java source files into bin directory
javac -d bin src/main/java/com/saranyamart/model/*.java src/main/java/com/saranyamart/util/*.java src/main/java/com/saranyamart/db/*.java src/main/java/com/saranyamart/dao/*.java src/main/java/com/saranyamart/handler/*.java src/main/java/com/saranyamart/Main.java

# 2. Run Main application
java -cp bin com.saranyamart.Main
```

### Method 2: Using Maven
```bash
mvn compile exec:java
```

Once started, open your web browser and navigate to:
👉 **`http://localhost:8080`**
