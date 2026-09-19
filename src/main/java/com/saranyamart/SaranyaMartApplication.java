package com.saranyamart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.saranyamart.db.DatabaseManager;

/**
 * Main Spring Boot Entry Point for SaranyaMart E-Commerce Platform.
 * Embedded Tomcat web engine automatically boots on port 8080.
 */
@SpringBootApplication
public class SaranyaMartApplication {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("                SARANYAMART E-COMMERCE PLATFORM                  ");
        System.out.println("              Spring Boot & Embedded Tomcat Engine               ");
        System.out.println("==================================================================");

        // Initialize Database & Seed Default Accounts
        DatabaseManager.initializeDatabase();

        // Start Spring Boot Embedded Tomcat Application
        SpringApplication.run(SaranyaMartApplication.class, args);

        System.out.println("\n[SUCCESS] SaranyaMart Spring Boot Application is running live!");
        System.out.println("-> Access Website at: http://localhost:8080");
        System.out.println("-> Default Admin Account: admin@saranyamart.com / Admin@123");
        System.out.println("-> Default Seller Account: seller@saranyamart.com / Seller@123");
        System.out.println("-> Default Buyer Account: buyer@saranyamart.com / Buyer@123");
        System.out.println("------------------------------------------------------------------\n");
    }
}
