package com.rate_limiter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rate_limiter.model.RequestLog;

public interface RequestLogRepository extends MongoRepository<RequestLog, String> {

}
