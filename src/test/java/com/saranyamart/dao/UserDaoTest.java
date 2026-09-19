package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.RegisterRequest;
import com.saranyamart.model.Role;
import com.saranyamart.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Unit Test suite for UserDao per Section 9 Testing Requirements.
 */
public class UserDaoTest {

    private final UserDao userDao = new UserDao();

    @BeforeAll
    public static void setUp() {
        DatabaseManager.initializeDatabase();
    }

    @Test
    public void testFindUserByEmail() {
        User admin = userDao.findByEmail("admin@saranyamart.com");
        assertNotNull(admin, "Admin user should exist in database");
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    public void testRegisterAndAuthenticateUser() {
        String testEmail = "testbuyer_" + System.currentTimeMillis() + "@domain.com";
        RegisterRequest req = new RegisterRequest("Test Buyer", testEmail, "BuyerSecret@123", Role.BUYER);
        User newBuyer = userDao.createUser(req);

        assertNotNull(newBuyer, "Newly registered buyer user should not be null");
        assertTrue(newBuyer.getId() > 0);

        User authenticated = userDao.authenticate(testEmail, "BuyerSecret@123");
        assertNotNull(authenticated, "User authentication should succeed with correct password");
        assertEquals("Test Buyer", authenticated.getFullName());

        User invalidAuth = userDao.authenticate(testEmail, "WrongPassword");
        assertNull(invalidAuth, "User authentication should fail with wrong password");
    }
}
