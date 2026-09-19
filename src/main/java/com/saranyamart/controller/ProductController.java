package com.saranyamart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.saranyamart.dao.ProductDao;
import com.saranyamart.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot REST Controller handling Product Module (Catalog search, Category filtering, Seller CRUD, Admin Moderation).
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductDao productDao = new ProductDao();

    /**
     * GET /api/products - Search & Category Filter
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false, defaultValue = "false") boolean admin) {

        List<Product> products;
        if (admin) {
            products = productDao.getAllProductsAdmin();
        } else {
            products = productDao.getAllActiveProducts(category, search, minPrice, maxPrice, sortBy);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", products.size());
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/{id} - Product Details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable int id) {
        Product p = productDao.getProductById(id);
        Map<String, Object> response = new HashMap<>();
        if (p == null) {
            response.put("success", false);
            response.put("message", "Product not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("success", true);
        response.put("product", p);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/seller/{sellerId} - Seller Listings
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Map<String, Object>> getSellerProducts(@PathVariable int sellerId) {
        List<Product> products = productDao.getProductsBySeller(sellerId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", products.size());
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/products - Seller Add Product
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        if (product == null || product.getTitle() == null || product.getTitle().trim().isEmpty() || product.getPrice() <= 0) {
            response.put("success", false);
            response.put("message", "Valid product title and price are required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Product created = productDao.createProduct(product);
        response.put("success", true);
        response.put("message", "Product listed successfully!");
        response.put("product", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/products/{id} - Seller Edit Product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable int id, @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        product.setId(id);
        try {
            Product updated = productDao.updateProduct(product);
            response.put("success", true);
            response.put("message", "Product updated successfully!");
            response.put("product", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * DELETE /api/products/{id} - Seller / Admin Delete Product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = productDao.deleteProduct(id);
        if (deleted) {
            response.put("success", true);
            response.put("message", "Product removed successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Product not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/products/{id}/flag - Admin Inappropriate Product Moderation
     */
    @PutMapping("/{id}/flag")
    public ResponseEntity<Map<String, Object>> flagProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean flagged = productDao.flagProduct(id);
        if (flagged) {
            response.put("success", true);
            response.put("message", "Product flagged for moderation.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Product not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
