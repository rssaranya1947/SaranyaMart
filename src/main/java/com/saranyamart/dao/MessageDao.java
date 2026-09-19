package com.saranyamart.dao;

import com.saranyamart.db.DatabaseManager;
import com.saranyamart.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for Buyer-Seller In-App Product Inquiry Messaging in SaranyaMart.
 */
public class MessageDao {

    public synchronized Message sendMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty.");
        }

        int newId = DatabaseManager.generateNextMessageId();
        message.setId(newId);
        if (message.getCreatedAt() == null || message.getCreatedAt().isEmpty()) {
            message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        DatabaseManager.getMessageMap().put(newId, message);
        DatabaseManager.saveMessagesToDisk();
        return message;
    }

    public List<Message> getMessagesForUser(int userId) {
        return DatabaseManager.getMessageMap().values().stream()
                .filter(m -> m.getSenderId() == userId || m.getRecipientId() == userId)
                .sorted(Comparator.comparingInt(Message::getId).reversed())
                .collect(Collectors.toList());
    }
}
