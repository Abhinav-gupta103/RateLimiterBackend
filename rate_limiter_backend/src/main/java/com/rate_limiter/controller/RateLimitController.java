package com.rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rate_limiter.service.RateLimitingService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RateLimitController {

    private final RateLimitingService rateService;

    public RateLimitController(RateLimitingService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/check-limit")
    public ResponseEntity<Map<String, Object>> checkLimit(@RequestHeader("X-API-KEY") String apiKey,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String ip) {
        var result = rateService.checkAndRecord(apiKey, endpoint, ip);
        return ResponseEntity.ok(Map.of(
                "allowed", result.allowed(),
                "reason", result.reason(),
                "minuteUsed", result.minuteUsed(),
                "minuteLimit", result.minuteLimit(),
                "dayUsed", result.dayUsed(),
                "dayLimit", result.dayLimit()));
    }

    /**
     * Explicit record request (if needed separately from check).
     * Most cases you use checkLimit which already records.
     */
    @PostMapping("/record-request")
    public ResponseEntity<Map<String, Object>> recordRequest(@RequestHeader("X-API-KEY") String apiKey,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String ip) {
        var result = rateService.checkAndRecord(apiKey, endpoint, ip); // reuse same logic
        return ResponseEntity.ok(Map.of(
                "allowed", result.allowed(),
                "reason", result.reason(),
                "minuteUsed", result.minuteUsed(),
                "minuteLimit", result.minuteLimit(),
                "dayUsed", result.dayUsed(),
                "dayLimit", result.dayLimit()));
    }

    @GetMapping("/limits/{apiKey}")
    public ResponseEntity<Map<String, Object>> getCurrentUsage(@PathVariable("apiKey") String apiKeyValue) {
        var key = rateService.getApiKey(apiKeyValue); // Create this helper in RateLimitingService
        int minuteUsed = rateService.getWindowService().getMinuteCount(key.getId());
        int dayUsed = rateService.getWindowService().getDayCount(key.getId());

        return ResponseEntity.ok(Map.of(
                "apiKey", apiKeyValue,
                "minuteUsed", minuteUsed,
                "minuteLimit", key.getRateLimitPerMinute(),
                "dayUsed", dayUsed,
                "dayLimit", key.getDailyQuota()));
    }
}
