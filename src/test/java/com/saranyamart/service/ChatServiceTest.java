package com.saranyamart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Unit Test suite for ChatService per Section 17 & Section 9 Testing Requirements.
 */
public class ChatServiceTest {

    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        MockChatProvider mockProvider = new MockChatProvider();
        GeminiChatProvider geminiProvider = new GeminiChatProvider();
        chatService = new ChatService(mockProvider, geminiProvider);
    }

    @Test
    public void testValidFAQQuestion() {
        String reply = chatService.processChat("What is your return policy?", "test-session-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("return") || reply.toLowerCase().contains("7 days"));
    }

    @Test
    public void testInputLengthCapGuardrail() {
        StringBuilder longQuery = new StringBuilder();
        for (int i = 0; i < 350; i++) {
            longQuery.append("a");
        }

        String reply = chatService.processChat(longQuery.toString(), "test-session-2");
        assertNotNull(reply);
        assertTrue(reply.contains("Question is too long") || reply.contains("300 characters"));
    }

    @Test
    public void testSessionRateLimitingGuardrail() {
        String sessionId = "rate-limit-session-99";
        for (int i = 0; i < 10; i++) {
            chatService.processChat("Shipping time " + i, sessionId);
        }

        String eleventhReply = chatService.processChat("Eleventh question?", sessionId);
        assertNotNull(eleventhReply);
        assertTrue(eleventhReply.contains("Rate Limit Exceeded") || eleventhReply.contains("10 msg/min"));
    }
}
