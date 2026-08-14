package com.saranyamart;

import com.sun.net.httpserver.HttpServer;
import com.saranyamart.db.DatabaseManager;
import com.saranyamart.handler.AuthHandler;
import com.saranyamart.handler.StaticFileHandler;

import java.net.InetSocketAddress;
import java.io.File;
import java.util.concurrent.Executors;

/**
 * Main application entry point for SaranyaMart - Multi-seller E-Commerce Application (Week 1).
 * Built 100% in Java.
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("                SARANYAMART E-COMMERCE PLATFORM                  ");
        System.out.println("                  Week 1: Pure Java Web Engine                   ");
        System.out.println("==================================================================");

        try {
            // Step 1: Initialize Database & Seed Default Accounts
            DatabaseManager.initializeDatabase();

            // Step 2: Determine Web Static Files Directory
            String publicDir = "public";
            File publicFolder = new File(publicDir);
            if (!publicFolder.exists()) {
                publicFolder.mkdirs();
            }

            // Step 3: Create embedded Java HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Step 4: Register API and Static File Contexts
            server.createContext("/api", new AuthHandler());
            server.createContext("/", new StaticFileHandler(publicDir));

            // Step 5: Configure multi-threaded execution pool
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Step 6: Start Web Server
            server.start();

            System.out.println("\n[SUCCESS] SaranyaMart Application Server is running live!");
            System.out.println("-> Access Website at: http://localhost:" + PORT);
            System.out.println("-> Default Admin Account: admin@saranyamart.com / Admin@123");
            System.out.println("-> Default Seller Account: seller@saranyamart.com / Seller@123");
            System.out.println("-> Default Buyer Account: buyer@saranyamart.com / Buyer@123");
            System.out.println("------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.err.println("[FATAL] Failed to start SaranyaMart Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
