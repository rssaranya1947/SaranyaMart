# SaranyaMart - Multi-Seller E-Commerce Platform (Week 9 - AI Chatbot Integration)

**SaranyaMart** is an online shopping web application built **100% using Java & Spring Boot**. It supports multi-role access for **Buyers**, **Sellers**, and **Admins**.

**Repository Owner & Developer**: [rssaranya1947](https://github.com/rssaranya1947)

---

## 🚀 Milestones Completed (Weeks 1 - 9)
- [x] **Project Repository & Architecture**: Spring Boot 2.7.18 structure with Maven (`pom.xml`), `.gitignore`, `README.md`.
- [x] **Database Engine & Schema**: Pure Java Persistence & SQLite engine (`db/schema.sql`).
- [x] **User Authentication & Roles**: Buyer, Seller, and Admin login/registration with bcrypt password security.
- [x] **Product Catalog & Management**: Product CRUD, category browsing, price filters, and inventory tracking.
- [x] **Cart & Mock Checkout**: Dynamic cart running totals, coupon/promo codes, and mock payment checkout.
- [x] **Order History & Invoices**: Order tracking with printable GST invoices.
- [x] **Reviews & Ratings**: Product reviews, star ratings, and seller storefront view.
- [x] **In-App Messaging**: Direct inquiry messaging between buyers and sellers.
- [x] **Week 9 - AI Chatbot Integration (Phase 3)**:
  - Pluggable `ChatProvider` interface architecture (`MockChatProvider` + `GeminiChatProvider`).
  - Server-side REST API proxy controller (`/api/chat` & `/api/v1/chat`).
  - Per-session rate limiting (10 msg/min), input length caps (300 chars), and in-memory question caching.
  - Interactive floating AI Chatbot UI widget with suggestion chips, typing indicator, and auto-scroll.


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
