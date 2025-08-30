package com.rate_limiter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rate_limiter.model.RateLimitWindow;

public interface RateLimitWindowRepository extends MongoRepository<RateLimitWindow, String> {
    List<RateLimitWindow> findByApiKeyIdAndWindowType(String apiKeyId, String windowType);
}
