package com.saranyamart.service;

/**
 * Interface for AI Chatbot Providers per Section 17 of Project Specification.
 * Allows swappable AI providers (Mock vs Gemini) via configuration.
 */
public interface ChatProvider {
    /**
     * Generate a chat response for the user message within marketplace context.
     *
     * @param userMessage The question or prompt sent by the user
     * @param context Domain context / background info
     * @return Chatbot reply string
     */
    String getReply(String userMessage, String context);
}
