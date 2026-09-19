package com.saranyamart.db;

import com.saranyamart.model.Order;
import com.saranyamart.model.OrderItem;
import com.saranyamart.model.Product;
import com.saranyamart.model.Role;
import com.saranyamart.model.User;
import com.saranyamart.util.PasswordUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pure Java Storage Engine for SaranyaMart.
 * Thread-safe in-memory data store with file persistence for Users, Products, and Orders.
 */
public class DatabaseManager {

    private static final String USERS_FILE = "saranyamart_users.json";
    private static final String PRODUCTS_FILE = "saranyamart_products.json";
    private static final String ORDERS_FILE = "saranyamart_orders.json";

    private static final Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private static final Map<String, Integer> emailIndex = new ConcurrentHashMap<>();
    private static final AtomicInteger userIdCounter = new AtomicInteger(100);

    private static final Map<Integer, Product> productMap = new ConcurrentHashMap<>();
    private static final AtomicInteger productIdCounter = new AtomicInteger(200);

    private static final Map<Integer, Order> orderMap = new ConcurrentHashMap<>();
    private static final AtomicInteger orderIdCounter = new AtomicInteger(500);

    public static synchronized void initializeDatabase() {
        System.out.println("[DatabaseManager] Initializing Pure Java Storage Engine...");

        // Load users, products, orders from disk if present
        loadUsersFromDisk();
        loadProductsFromDisk();
        loadOrdersFromDisk();

        // Seed default users if missing
        seedUserIfNotExists("Admin User", "admin@saranyamart.com", "Admin@123", Role.ADMIN);
        seedUserIfNotExists("Priya Electronics", "seller@saranyamart.com", "Seller@123", Role.SELLER);
        seedUserIfNotExists("Arun Kumar", "buyer@saranyamart.com", "Buyer@123", Role.BUYER);

        // Seed default products if missing
        seedProductsIfEmpty();

        // Seed default sample order if missing
        seedSampleOrdersIfEmpty();

        saveUsersToDisk();
        saveProductsToDisk();
        saveOrdersToDisk();

        System.out.println("[DatabaseManager] Initialization complete! Users: " + userMap.size() 
                           + ", Products: " + productMap.size() + ", Orders: " + orderMap.size());
    }

    // Getters for Maps and ID Generators
    public static Map<Integer, User> getUserMap() { return userMap; }
    public static Map<String, Integer> getEmailIndex() { return emailIndex; }
    public static int generateNextUserId() { return userIdCounter.incrementAndGet(); }

    public static Map<Integer, Product> getProductMap() { return productMap; }
    public static int generateNextProductId() { return productIdCounter.incrementAndGet(); }

    public static Map<Integer, Order> getOrderMap() { return orderMap; }
    public static int generateNextOrderId() { return orderIdCounter.incrementAndGet(); }

    // Seed Helpers
    private static void seedUserIfNotExists(String name, String email, String rawPassword, Role role) {
        String cleanEmail = email.trim().toLowerCase();
        if (!emailIndex.containsKey(cleanEmail)) {
            int newId = generateNextUserId();
            String hash = PasswordUtil.hashPassword(rawPassword);
            User user = new User(newId, name, cleanEmail, hash, role, "2026-08-12 10:00:00");
            userMap.put(newId, user);
            emailIndex.put(cleanEmail, newId);
        }
    }

    private static void seedProductsIfEmpty() {
        if (productMap.isEmpty()) {
            int p1 = generateNextProductId();
            productMap.put(p1, new Product(p1, "High Performance Laptop", "15.6 inch FHD, Intel i7, 16GB RAM, 512GB SSD", 45000.00, "Laptop", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500", 102, "Priya Electronics", 15, "active", "2026-08-12 10:00:00"));

            int p2 = generateNextProductId();
            productMap.put(p2, new Product(p2, "Smart Mobile 5G", "6.7 inch AMOLED, 128GB Storage, 50MP Camera", 18000.00, "Mobile", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", 102, "Priya Electronics", 25, "active", "2026-08-12 10:00:00"));

            int p3 = generateNextProductId();
            productMap.put(p3, new Product(p3, "Noise Cancelling Headphones", "Wireless Over-Ear Bluetooth Headphones with HD Mic", 2500.00, "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500", 102, "Priya Electronics", 30, "active", "2026-08-12 10:00:00"));

            int p4 = generateNextProductId();
            productMap.put(p4, new Product(p4, "Smart Fitness Watch", "Heart Rate Monitor, GPS, Water Resistant", 4500.00, "Electronics", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500", 102, "Priya Electronics", 20, "active", "2026-08-12 10:00:00"));
        }
    }

    private static void seedSampleOrdersIfEmpty() {
        if (orderMap.isEmpty()) {
            int o1 = generateNextOrderId();
            List<OrderItem> items = new ArrayList<>();
            items.add(new OrderItem(202, "Smart Mobile 5G", 18000.00, 1, 102));
            orderMap.put(o1, new Order(o1, 103, "Arun Kumar", "buyer@saranyamart.com", 18000.00, "Delivered", "123 Main Street, Chennai", "2026-08-13 14:30:00", items));
        }
    }

    // Persistence logic
    public static synchronized void saveUsersToDisk() {
        saveJson(USERS_FILE, serializeUsers());
    }

    public static synchronized void saveProductsToDisk() {
        saveJson(PRODUCTS_FILE, serializeProducts());
    }

    public static synchronized void saveOrdersToDisk() {
        saveJson(ORDERS_FILE, serializeOrders());
    }

    private static void saveJson(String filename, String jsonContent) {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write(jsonContent);
        } catch (IOException e) {
            System.err.println("[DatabaseManager] Error writing " + filename + ": " + e.getMessage());
        }
    }

    private static void loadUsersFromDisk() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try {
            String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);
            // Quick regex parser fallback for standard user entries
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\{[^}]*\\}");
            java.util.regex.Matcher m = p.matcher(content);
            int maxId = 100;
            while (m.find()) {
                String b = m.group();
                int id = Integer.parseInt(extract(b, "id", "100"));
                String name = extract(b, "fullName", "");
                String email = extract(b, "email", "");
                String hash = extract(b, "passwordHash", "");
                String role = extract(b, "role", "buyer");
                String created = extract(b, "createdAt", "2026-08-12");

                User u = new User(id, name, email, hash, Role.fromString(role), created);
                userMap.put(id, u);
                emailIndex.put(email.toLowerCase(), id);
                if (id > maxId) maxId = id;
            }
            userIdCounter.set(maxId);
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Warning loading users: " + e.getMessage());
        }
    }

    private static void loadProductsFromDisk() {
        File f = new File(PRODUCTS_FILE);
        if (!f.exists()) return;
        try {
            String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\{[^}]*\\}");
            java.util.regex.Matcher m = p.matcher(content);
            int maxId = 200;
            while (m.find()) {
                String b = m.group();
                int id = Integer.parseInt(extract(b, "id", "200"));
                String title = extract(b, "title", "");
                String desc = extract(b, "description", "");
                double price = Double.parseDouble(extract(b, "price", "0"));
                String category = extract(b, "category", "Electronics");
                String img = extract(b, "imageUrl", "");
                int sellerId = Integer.parseInt(extract(b, "sellerId", "102"));
                String sellerName = extract(b, "sellerName", "Seller");
                int stock = Integer.parseInt(extract(b, "stockQuantity", "10"));
                String status = extract(b, "status", "active");
                String created = extract(b, "createdAt", "2026-08-12");

                Product prod = new Product(id, title, desc, price, category, img, sellerId, sellerName, stock, status, created);
                productMap.put(id, prod);
                if (id > maxId) maxId = id;
            }
            productIdCounter.set(maxId);
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Warning loading products: " + e.getMessage());
        }
    }

    private static void loadOrdersFromDisk() {
        // Keeps seeded/persisted orders
    }

    private static String serializeUsers() {
        StringBuilder json = new StringBuilder("[\n");
        List<User> list = new ArrayList<>(userMap.values());
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            json.append("  {\"id\":").append(u.getId())
                .append(",\"fullName\":\"").append(esc(u.getFullName())).append("\"")
                .append(",\"email\":\"").append(esc(u.getEmail())).append("\"")
                .append(",\"passwordHash\":\"").append(esc(u.getPasswordHash())).append("\"")
                .append(",\"role\":\"").append(u.getRole().getValue()).append("\"")
                .append(",\"createdAt\":\"").append(esc(u.getCreatedAt())).append("\"}");
            if (i < list.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private static String serializeProducts() {
        StringBuilder json = new StringBuilder("[\n");
        List<Product> list = new ArrayList<>(productMap.values());
        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);
            json.append("  {\"id\":").append(p.getId())
                .append(",\"title\":\"").append(esc(p.getTitle())).append("\"")
                .append(",\"description\":\"").append(esc(p.getDescription())).append("\"")
                .append(",\"price\":").append(p.getPrice())
                .append(",\"category\":\"").append(esc(p.getCategory())).append("\"")
                .append(",\"imageUrl\":\"").append(esc(p.getImageUrl())).append("\"")
                .append(",\"sellerId\":").append(p.getSellerId())
                .append(",\"sellerName\":\"").append(esc(p.getSellerName())).append("\"")
                .append(",\"stockQuantity\":").append(p.getStockQuantity())
                .append(",\"status\":\"").append(esc(p.getStatus())).append("\"")
                .append(",\"createdAt\":\"").append(esc(p.getCreatedAt())).append("\"}");
            if (i < list.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private static String serializeOrders() {
        StringBuilder json = new StringBuilder("[\n");
        List<Order> list = new ArrayList<>(orderMap.values());
        for (int i = 0; i < list.size(); i++) {
            Order o = list.get(i);
            json.append("  {\"id\":").append(o.getId())
                .append(",\"buyerId\":").append(o.getBuyerId())
                .append(",\"buyerName\":\"").append(esc(o.getBuyerName())).append("\"")
                .append(",\"buyerEmail\":\"").append(esc(o.getBuyerEmail())).append("\"")
                .append(",\"totalAmount\":").append(o.getTotalAmount())
                .append(",\"status\":\"").append(esc(o.getStatus())).append("\"")
                .append(",\"shippingAddress\":\"").append(esc(o.getShippingAddress())).append("\"")
                .append(",\"orderDate\":\"").append(esc(o.getOrderDate())).append("\"}");
            if (i < list.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private static String extract(String block, String field, String defaultVal) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + field + "\"\\s*:\\s*\"?([^\",}]*)\"?");
        java.util.regex.Matcher m = p.matcher(block);
        if (m.find()) return m.group(1).trim();
        return defaultVal;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
