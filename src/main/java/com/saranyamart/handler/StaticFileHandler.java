package com.saranyamart.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Java HTTP Handler to serve frontend static assets (HTML, CSS, JavaScript, Images) for SaranyaMart.
 */
public class StaticFileHandler implements HttpHandler {

    private final String publicDirPath;

    public StaticFileHandler(String publicDirPath) {
        this.publicDirPath = publicDirPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        
        if (requestPath.equals("/") || requestPath.isEmpty()) {
            requestPath = "/index.html";
        }

        // Prevent directory traversal attacks
        String normalizedPath = Paths.get(requestPath).normalize().toString();
        File file = new File(publicDirPath, normalizedPath);

        if (!file.exists() || file.isDirectory()) {
            // Fallback to index.html for single-page app behavior
            file = new File(publicDirPath, "index.html");
        }

        if (!file.exists()) {
            String notFoundMsg = "404 Not Found - Static resource missing";
            exchange.sendResponseHeaders(404, notFoundMsg.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFoundMsg.getBytes());
            }
            return;
        }

        String mimeType = getMimeType(file.getName());
        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private String getMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "text/plain; charset=UTF-8";
    }
}
