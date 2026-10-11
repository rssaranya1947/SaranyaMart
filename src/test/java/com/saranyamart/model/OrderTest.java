package com.saranyamart.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class OrderTest {

    @Test
    void testOrderPaymentMethodGetterSetter() {
        Order order = new Order();
        order.setId(101);
        order.setBuyerId(1);
        order.setBuyerName("Test Buyer");
        order.setBuyerEmail("buyer@example.com");
        order.setTotalAmount(1500.0);
        order.setShippingAddress("123 Sample Street, Chennai");
        order.setPaymentMethod("Cash on Delivery");
        order.setItems(new ArrayList<>());

        assertEquals("Cash on Delivery", order.getPaymentMethod());
        assertEquals("123 Sample Street, Chennai", order.getShippingAddress());
        assertEquals(1500.0, order.getTotalAmount());
    }

    @Test
    void testPaymentMethodEnumValidation() {
        assertTrue(PaymentMethod.isValid("UPI"));
        assertTrue(PaymentMethod.isValid("Credit/Debit Card"));
        assertTrue(PaymentMethod.isValid("Net Banking"));
        assertTrue(PaymentMethod.isValid("Wallet"));
        assertTrue(PaymentMethod.isValid("Cash on Delivery"));

        assertFalse(PaymentMethod.isValid(null));
        assertFalse(PaymentMethod.isValid(""));
        assertFalse(PaymentMethod.isValid("BitcoinCrypto"));
    }

    @Test
    void testCashOnDeliveryInvoiceCalculation() {
        Order order = new Order();
        order.setTotalAmount(5000.0);
        order.setPaymentMethod("Cash on Delivery");

        double gst = Math.round(order.getTotalAmount() * 0.18 * 100.0) / 100.0;
        double subtotal = Math.round((order.getTotalAmount() - gst) * 100.0) / 100.0;

        assertEquals(900.0, gst);
        assertEquals(4100.0, subtotal);
        assertEquals(5000.0, subtotal + gst);
    }
}
