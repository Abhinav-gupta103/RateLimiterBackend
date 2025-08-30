package com.rate_limiter.service;

import org.springframework.stereotype.Service;

import com.rate_limiter.model.ApiKey;
import com.rate_limiter.repository.ApiKeyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repo;

    public ApiKeyService(ApiKeyRepository repo) {
        this.repo = repo;
    }

    public ApiKey create(ApiKey payload) {
        ApiKey k = new ApiKey();
        k.setKeyValue(UUID.randomUUID().toString());
        k.setAppName(payload.getAppName());
        k.setRateLimitPerMinute(payload.getRateLimitPerMinute());
        k.setDailyQuota(payload.getDailyQuota());
        k.setIsActive(true);
        k.setCreatedAt(LocalDateTime.now());
        return repo.save(k);
    }

    public List<ApiKey> list() {
        return repo.findAll();
    }

    public Optional<ApiKey> getById(String id) {
        return repo.findById(id);
    }

    public Optional<ApiKey> getByValue(String keyValue) {
        return repo.findByKeyValue(keyValue);
    }

    public ApiKey updateLimits(String id, Integer perMinute, Integer dailyQuota) {
        ApiKey existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("API key not found"));
        if (perMinute != null)
            existing.setRateLimitPerMinute(perMinute);
        if (dailyQuota != null)
            existing.setDailyQuota(dailyQuota);
        return repo.save(existing);
    }

    public void revoke(String id) {
        repo.deleteById(id);
    }

    public ApiKey updateEndpointLimits(String id, Map<String, Integer> endpointLimits) {
        ApiKey existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("API key not found"));
        existing.setEndpointLimits(endpointLimits);
        return repo.save(existing);
    }
}
