package com.saranyamart.model;

/**
 * Domain model representing an In-App Buyer-Seller Message / Product Inquiry in SaranyaMart.
 */
public class Message {
    private int id;
    private int senderId;
    private String senderName;
    private int recipientId;
    private String recipientName;
    private int productId;
    private String productTitle;
    private String messageText;
    private String createdAt;

    public Message() {}

    public Message(int id, int senderId, String senderName, int recipientId, String recipientName, 
                   int productId, String productTitle, String messageText, String createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.productId = productId;
        this.productTitle = productTitle;
        this.messageText = messageText;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
