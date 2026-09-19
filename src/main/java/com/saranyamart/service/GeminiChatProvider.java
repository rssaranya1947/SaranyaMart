package com.saranyamart.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gemini AI Implementation of ChatProvider per Section 17 & Section 11 of Project Specification.
 * Connects to Google Gemini API server-side when configured.
 * Wrapped in try-catch with graceful degraded fallback per Section 11 Rule 3.
 */
@Component("geminiChatProvider")
public class GeminiChatProvider implements ChatProvider {

    @Value("${ai.chatbot.api-key:}")
    private String apiKey;

    @Value("${ai.chatbot.model:gemini-1.5-flash}")
    private String model;

    @Override
    public String getReply(String userMessage, String context) {
        String envKey = System.getenv("GEMINI_API_KEY");
        String effectiveKey = (envKey != null && !envKey.trim().isEmpty()) ? envKey.trim() : apiKey;

        if (effectiveKey == null || effectiveKey.trim().isEmpty()) {
            return "🤖 **SaranyaMart Assistant**:\n*Note: Gemini API Key is not configured on server. Operating in fallback mode.*\n\n" +
                   "I can assist you with order status, returns, delivery options, and promo codes on SaranyaMart!";
        }

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + effectiveKey;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            conn.setDoOutput(true);

            String systemPrompt = "You are the official AI Assistant for SaranyaMart, a multi-seller e-commerce marketplace. " +
                                 "Answer helpful, polite questions restricted to products, shipping, orders, returns, payments, and shopping recommendations. " +
                                 "Keep responses concise (2-4 sentences max). " + context;

            String jsonPayload = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"%s\\n\\nUser Question: %s\"}]}]}",
                escapeJson(systemPrompt),
                escapeJson(userMessage)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String rawJson = response.toString();
                    String extracted = parseGeminiTextResponse(rawJson);
                    if (extracted != null && !extracted.trim().isEmpty()) {
                        return extracted;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AI Chatbot] Error communicating with Gemini API: " + e.getMessage());
        }

        // Fallback degraded response per Section 11 Rule 3
        return "🤖 **SaranyaMart Assistant**:\nOur live AI network connection is currently busy. " +
               "However, I can still help answer questions about shipping, returns, cart checkout, and promo codes!";
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", " ")
                    .replace("\r", " ");
    }

    private String parseGeminiTextResponse(String rawJson) {
        int textIdx = rawJson.indexOf("\"text\": \"");
        if (textIdx != -1) {
            int start = textIdx + 9;
            int end = rawJson.indexOf("\"", start);
            if (end != -1) {
                String val = rawJson.substring(start, end);
                return val.replace("\\n", "\n").replace("\\\"", "\"");
            }
        }
        return null;
    }
}
