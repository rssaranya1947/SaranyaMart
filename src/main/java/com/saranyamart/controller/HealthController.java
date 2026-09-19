package com.saranyamart.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saranyamart.db.DatabaseManager;

/**
 * Health & Observability REST Controller per Section 18 Rule 1 of Project Specification.
 * GET /api/v1/health returns { "status": "UP", "db": "UP" }.
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        boolean dbAlive = DatabaseManager.isDatabaseHealthy();

        health.put("status", dbAlive ? "UP" : "DOWN");
        health.put("db", dbAlive ? "UP" : "DOWN");
        health.put("service", "SaranyaMart");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}
