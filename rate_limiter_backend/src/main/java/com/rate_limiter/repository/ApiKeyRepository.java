package com.rate_limiter.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rate_limiter.model.ApiKey;

public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {
    Optional<ApiKey> findByKeyValue(String keyValue);
}
