package com.rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rate_limiter.model.ApiKey;
import com.rate_limiter.service.ApiKeyService;

import java.util.List;

@RestController
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    public ResponseEntity<ApiKey> createKey(@RequestBody ApiKey payload) {
        return ResponseEntity.ok(apiKeyService.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<ApiKey>> listKeys() {
        return ResponseEntity.ok(apiKeyService.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiKey> updateKey(@PathVariable String id,
            @RequestParam(required = false) Integer rateLimitPerMinute,
            @RequestParam(required = false) Integer dailyQuota) {
        return ResponseEntity.ok(apiKeyService.updateLimits(id, rateLimitPerMinute, dailyQuota));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKey(@PathVariable String id) {
        apiKeyService.revoke(id);
        return ResponseEntity.noContent().build();
    }
}
