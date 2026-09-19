package com.saranyamart.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saranyamart.service.ChatService;

/**
 * REST Controller for AI Chatbot Proxy Endpoint per Section 11 & Section 13 API Envelope Standards.
 * Supports POST /api/chat and POST /api/v1/chat.
 */
@RestController
@RequestMapping
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping({"/api/chat", "/api/v1/chat"})
    public ResponseEntity<Map<String, Object>> handleChatRequest(
            @RequestBody(required = false) Map<String, String> requestBody,
            HttpServletRequest request) {

        Map<String, Object> responseEnvelope = new HashMap<>();

        if (requestBody == null || !requestBody.containsKey("message")) {
            responseEnvelope.put("success", false);
            responseEnvelope.put("data", null);
            Map<String, String> err = new HashMap<>();
            err.put("code", "VALIDATION_ERROR");
            err.put("message", "Request body must contain 'message' field.");
            responseEnvelope.put("error", err);
            return ResponseEntity.badRequest().body(responseEnvelope);
        }

        String userMessage = requestBody.get("message");
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();

        String botReply = chatService.processChat(userMessage, sessionId);

        Map<String, Object> data = new HashMap<>();
        data.put("reply", botReply);
        data.put("provider", chatService.getActiveProviderName());
        data.put("sessionId", sessionId);

        responseEnvelope.put("success", true);
        responseEnvelope.put("data", data);
        responseEnvelope.put("reply", botReply); // Direct field convenience for simplified fetch clients
        responseEnvelope.put("error", null);

        return ResponseEntity.ok(responseEnvelope);
    }
}
