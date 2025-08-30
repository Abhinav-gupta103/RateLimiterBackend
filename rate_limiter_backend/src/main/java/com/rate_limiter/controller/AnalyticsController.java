package com.rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rate_limiter.service.UsageAnalyticsService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final UsageAnalyticsService analyticsService;

    public AnalyticsController(UsageAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/usage/{apiKeyId}")
    public ResponseEntity<Map<String, Long>> usageStats(@PathVariable String apiKeyId,
            @RequestParam(defaultValue = "1") int hours) {
        return ResponseEntity.ok(analyticsService.perMinute(apiKeyId, hours));
    }

    @GetMapping("/violations")
    public ResponseEntity<Object> recentViolations(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.recentViolations(limit));
    }

    @GetMapping("/violations/by-app")
    public ResponseEntity<Object> recentViolations(@RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String appName) {
        return ResponseEntity.ok(analyticsService.recentViolations(limit, appName));
    }
}
