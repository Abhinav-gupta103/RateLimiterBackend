package com.rate_limiter.service;

import org.springframework.stereotype.Service;

import com.rate_limiter.repository.RequestLogRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class HealthService {

    private final RequestLogRepository repo;

    public HealthService(RequestLogRepository repo) {
        this.repo = repo;
    }

    public Map<String, Object> snapshot() {
        LocalDateTime now = LocalDateTime.now();
        long last1m = repo.findAll().stream().filter(r -> r.getTimestamp().isAfter(now.minusMinutes(1))).count();
        long last5m = repo.findAll().stream().filter(r -> r.getTimestamp().isAfter(now.minusMinutes(5))).count();
        long last1h = repo.findAll().stream().filter(r -> r.getTimestamp().isAfter(now.minusHours(1))).count();
        long denied5m = repo.findAll().stream()
                .filter(r -> r.getTimestamp().isAfter(now.minusMinutes(5)) && Boolean.FALSE.equals(r.getWasAllowed()))
                .count();

        return Map.of(
                "uptime", "ok",
                "req_per_min", last1m,
                "req_last_5m", last5m,
                "req_last_1h", last1h,
                "denied_last_5m", denied5m);
    }
}
