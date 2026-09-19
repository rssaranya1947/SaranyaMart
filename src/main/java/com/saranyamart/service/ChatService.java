package com.saranyamart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service managing AI Chatbot operations, rate limits, input validation, session caching, and provider routing.
 * Adheres strictly to Section 11 & Section 17 of Project Requirements Specification.
 */
@Service
public class ChatService {

    @Value("${ai.chatbot.provider:mock}")
    private String providerConfig;

    private final ChatProvider mockProvider;
    private final ChatProvider geminiProvider;

    // Per-Session Rate Limiter: Map<SessionID, List<TimestampMs>> (Max 10 messages per 60 seconds)
    private final Map<String, List<Long>> rateLimitMap = new ConcurrentHashMap<>();

    // Per-Session Question Cache: Map<SessionID + ":" + Query, Response> (Section 17 Rule 4)
    private final Map<String, String> cacheMap = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(
            @Qualifier("mockChatProvider") ChatProvider mockProvider,
            @Qualifier("geminiChatProvider") ChatProvider geminiProvider) {
        this.mockProvider = mockProvider;
        this.geminiProvider = geminiProvider;
    }

    /**
     * Processes incoming user message with session guardrails, rate limiting, and caching.
     *
     * @param userMessage Message string from user
     * @param sessionId Current HTTP session ID
     * @return Processed chatbot response string
     */
    public String processChat(String userMessage, String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "default-session";
        }

        // 1. Input Length Cap Guardrail (Max 300 chars, Section 17 Rule 3)
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please type a valid question about SaranyaMart products or services.";
        }
        if (userMessage.length() > 300) {
            return "⚠️ Question is too long. Please shorten your message to under 300 characters.";
        }

        String cleanedQuery = userMessage.trim();

        // 2. Per-Session Rate Limiting Guardrail (Max 10 msgs/min, Section 17 Rule 3)
        long now = System.currentTimeMillis();
        List<Long> timestamps = rateLimitMap.computeIfAbsent(sessionId, k -> new ArrayList<>());
        synchronized (timestamps) {
            timestamps.removeIf(t -> now - t > 60000); // remove timestamps older than 60s
            if (timestamps.size() >= 10) {
                return "⏱️ **Rate Limit Exceeded**: You have sent too many messages. Please wait a minute before asking another question (Limit: 10 msg/min).";
            }
            timestamps.add(now);
        }

        // 3. Repeated Question In-Memory Caching per session (Section 17 Rule 4)
        String cacheKey = sessionId + ":" + cleanedQuery.toLowerCase();
        if (cacheMap.containsKey(cacheKey)) {
            return cacheMap.get(cacheKey) + "\n\n*(Cached Response)*";
        }

        // 4. Select AI Provider via Config Flag (Section 17 Rule 2)
        ChatProvider activeProvider = "gemini".equalsIgnoreCase(providerConfig) ? geminiProvider : mockProvider;

        String domainContext = "SaranyaMart Marketplace context: Multi-seller platform selling Laptops, Mobiles, Electronics, and Fashion with standard 3-5 day shipping and 7-day returns.";
        String reply = activeProvider.getReply(cleanedQuery, domainContext);

        // Cache response for session
        cacheMap.put(cacheKey, reply);

        return reply;
    }

    public String getActiveProviderName() {
        return providerConfig;
    }
}
