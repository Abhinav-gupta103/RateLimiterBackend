package com.rate_limiter.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rate_limiter.model.RequestLog;
import com.rate_limiter.repository.RequestLogRepository;

import java.time.LocalDateTime;

@Service
public class RequestLogService {

    private final RequestLogRepository repo;

    public RequestLogService(RequestLogRepository repo) {
        this.repo = repo;
    }

    @Async
    public void log(String apiKeyId, String endpoint, String ip, boolean allowed) {
        RequestLog log = new RequestLog();
        log.setApiKeyId(apiKeyId);
        log.setEndpoint(endpoint);
        log.setIpAddress(ip);
        log.setTimestamp(LocalDateTime.now());
        log.setWasAllowed(allowed);
        repo.save(log);
    }
}
