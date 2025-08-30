package com.rate_limiter.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rate_limiter.model.BlockedIp;

public interface BlockedIpRepository extends MongoRepository<BlockedIp, String> {
    Optional<BlockedIp> findByIpAddress(String ipAddress);
}
