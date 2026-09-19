package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Order;
import com.saranyamart.model.OrderItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for Order processing and fulfillment in SaranyaMart.
 */
public class OrderDao {

    public synchronized Order createOrder(Order order) {
        int newId = DatabaseManager.generateNextOrderId();
        order.setId(newId);
        order.setStatus("Pending");
        order.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        DatabaseManager.getOrderMap().put(newId, order);
        DatabaseManager.saveOrdersToDisk();
        return order;
    }

    public List<Order> getOrdersByBuyer(int buyerId) {
        return DatabaseManager.getOrderMap().values().stream()
                .filter(o -> o.getBuyerId() == buyerId)
                .sorted(Comparator.comparingInt(Order::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersBySeller(int sellerId) {
        List<Order> result = new ArrayList<>();
        for (Order o : DatabaseManager.getOrderMap().values()) {
            boolean sellerHasItem = false;
            if (o.getItems() != null) {
                for (OrderItem item : o.getItems()) {
                    if (item.getSellerId() == sellerId) {
                        sellerHasItem = true;
                        break;
                    }
                }
            }
            if (sellerHasItem) {
                result.add(o);
            }
        }
        result.sort(Comparator.comparingInt(Order::getId).reversed());
        return result;
    }

    public List<Order> getAllOrders() {
        return DatabaseManager.getOrderMap().values().stream()
                .sorted(Comparator.comparingInt(Order::getId).reversed())
                .collect(Collectors.toList());
    }

    public synchronized boolean updateOrderStatus(int orderId, String newStatus) {
        Order o = DatabaseManager.getOrderMap().get(orderId);
        if (o != null) {
            o.setStatus(newStatus);
            DatabaseManager.saveOrdersToDisk();
            return true;
        }
        return false;
    }
}
