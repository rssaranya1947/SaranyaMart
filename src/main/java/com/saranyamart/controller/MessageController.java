package com.saranyamart.controller;

import com.saranyamart.dao.MessageDao;
import com.saranyamart.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Spring REST Controller for Buyer-Seller Product Inquiry Messaging.
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageDao messageDao = new MessageDao();

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Message>> getUserMessages(@PathVariable int userId) {
        List<Message> list = messageDao.getMessagesForUser(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        try {
            if (message.getSenderId() <= 0 || message.getRecipientId() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Sender and Recipient IDs are required."));
            }
            Message sent = messageDao.sendMessage(message);
            return ResponseEntity.status(HttpStatus.CREATED).body(sent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
