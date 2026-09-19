package com.saranyamart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.saranyamart.dao.OrderDao;
import com.saranyamart.model.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot REST Controller handling Orders & Checkout (Checkout, Buyer History, Seller Orders, Admin Supervision).
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderDao orderDao = new OrderDao();

    /**
     * POST /api/orders/checkout - Buyer Confirm & Place Order
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        if (order == null || order.getBuyerId() <= 0 || order.getItems() == null || order.getItems().isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid order payload. Items and buyer information required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Shipping address is required to place an order.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Order createdOrder = orderDao.createOrder(order);
        response.put("success", true);
        response.put("message", "Order placed successfully!");
        response.put("order", createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/buyer/{buyerId} - Buyer Order History
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<Map<String, Object>> getBuyerOrders(@PathVariable int buyerId) {
        List<Order> orders = orderDao.getOrdersByBuyer(buyerId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", orders.size());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/seller/{sellerId} - Seller Orders Received
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Map<String, Object>> getSellerOrders(@PathVariable int sellerId) {
        List<Order> orders = orderDao.getOrdersBySeller(sellerId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", orders.size());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders - Admin View All Platform Orders
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        List<Order> orders = orderDao.getAllOrders();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", orders.size());
        response.put("orders", orders);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/orders/{id}/status - Update Order Fulfillment Status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String status = payload != null ? payload.get("status") : null;

        if (status == null || status.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Order status string is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean updated = orderDao.updateOrderStatus(id, status);
        if (updated) {
            response.put("success", true);
            response.put("message", "Order status updated to '" + status + "'.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Order not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/orders/{id}/cancel - Buyer / Admin Cancel Order & Restore Stock
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean cancelled = orderDao.cancelOrder(id);
        if (cancelled) {
            response.put("success", true);
            response.put("message", "Order cancelled successfully and product inventory restored!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Order could not be cancelled or was already cancelled.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
