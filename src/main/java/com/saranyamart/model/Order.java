package com.saranyamart.model;

import java.util.List;

/**
 * Domain model representing a customer Order in SaranyaMart.
 */
public class Order {
    private int id;
    private int buyerId;
    private String buyerName;
    private String buyerEmail;
    private double totalAmount;
    private String status; // 'Pending', 'Processing', 'Delivered', 'Cancelled'
    private String shippingAddress;
    private String orderDate;
    private List<OrderItem> items;

    public Order() {}

    public Order(int id, int buyerId, String buyerName, String buyerEmail, double totalAmount, 
                 String status, String shippingAddress, String orderDate, List<OrderItem> items) {
        this.id = id;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
        this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
