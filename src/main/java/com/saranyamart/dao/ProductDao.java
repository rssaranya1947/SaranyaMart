package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for Product operations in SaranyaMart.
 */
public class ProductDao {

    public List<Product> getAllActiveProducts(String category, String search) {
        return DatabaseManager.getProductMap().values().stream()
                .filter(p -> "active".equalsIgnoreCase(p.getStatus()))
                .filter(p -> category == null || category.isEmpty() || "all".equalsIgnoreCase(category) || p.getCategory().equalsIgnoreCase(category))
                .filter(p -> search == null || search.isEmpty() || p.getTitle().toLowerCase().contains(search.toLowerCase()) || (p.getDescription() != null && p.getDescription().toLowerCase().contains(search.toLowerCase())))
                .sorted(Comparator.comparingInt(Product::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<Product> getAllProductsAdmin() {
        return DatabaseManager.getProductMap().values().stream()
                .sorted(Comparator.comparingInt(Product::getId).reversed())
                .collect(Collectors.toList());
    }

    public Product getProductById(int id) {
        return DatabaseManager.getProductMap().get(id);
    }

    public List<Product> getProductsBySeller(int sellerId) {
        return DatabaseManager.getProductMap().values().stream()
                .filter(p -> p.getSellerId() == sellerId)
                .sorted(Comparator.comparingInt(Product::getId).reversed())
                .collect(Collectors.toList());
    }

    public synchronized Product createProduct(Product product) {
        int newId = DatabaseManager.generateNextProductId();
        product.setId(newId);
        if (product.getStatus() == null || product.getStatus().isEmpty()) {
            product.setStatus("active");
        }
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
            product.setImageUrl("https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500");
        }
        product.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        DatabaseManager.getProductMap().put(newId, product);
        DatabaseManager.saveProductsToDisk();
        return product;
    }

    public synchronized Product updateProduct(Product product) {
        Product existing = getProductById(product.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " not found.");
        }

        existing.setTitle(product.getTitle());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());
        existing.setStockQuantity(product.getStockQuantity());
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(product.getImageUrl());
        }

        DatabaseManager.saveProductsToDisk();
        return existing;
    }

    public synchronized boolean deleteProduct(int id) {
        Product p = DatabaseManager.getProductMap().get(id);
        if (p != null) {
            DatabaseManager.getProductMap().remove(id);
            DatabaseManager.saveProductsToDisk();
            return true;
        }
        return false;
    }

    public synchronized boolean flagProduct(int id) {
        Product p = DatabaseManager.getProductMap().get(id);
        if (p != null) {
            p.setStatus("flagged");
            DatabaseManager.saveProductsToDisk();
            return true;
        }
        return false;
    }
}
