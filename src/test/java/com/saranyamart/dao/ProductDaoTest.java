package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Unit Test suite for ProductDao per Section 9 Testing Requirements.
 */
public class ProductDaoTest {

    private final ProductDao productDao = new ProductDao();

    @BeforeAll
    public static void setUp() {
        DatabaseManager.initializeDatabase();
    }

    @Test
    public void testGetAllActiveProducts() {
        List<Product> products = productDao.getAllActiveProducts(null, null);
        assertNotNull(products);
        assertFalse(products.isEmpty(), "Product catalog should contain seeded products");
    }

    @Test
    public void testCreateAndRetrieveProduct() {
        Product p = new Product(0, "Test Gaming Laptop", "High performance GPU", 75000.0, "Laptop", "https://img.com/lap.jpg", 102, "Priya Electronics", 5, "active", null);
        Product created = productDao.createProduct(p);

        assertNotNull(created);
        assertTrue(created.getId() > 0);

        Product fetched = productDao.getProductById(created.getId());
        assertNotNull(fetched);
        assertEquals("Test Gaming Laptop", fetched.getTitle());
        assertEquals(75000.0, fetched.getPrice());
    }

    @Test
    public void testCategoryFiltering() {
        List<Product> laptops = productDao.getAllActiveProducts("Laptop", null);
        assertNotNull(laptops);
        for (Product prod : laptops) {
            assertEquals("Laptop", prod.getCategory());
        }
    }
}
