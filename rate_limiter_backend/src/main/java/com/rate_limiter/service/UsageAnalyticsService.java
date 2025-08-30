package com.rate_limiter.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.rate_limiter.model.RequestLog;
import com.rate_limiter.model.DTO.ViolationResponse;
import com.rate_limiter.repository.RequestLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsageAnalyticsService {

    private final RequestLogRepository repo;

    public UsageAnalyticsService(RequestLogRepository repo) {
        this.repo = repo;
    }

    /** Basic stats for last N hours grouped by minute */
    public Map<String, Long> perMinute(String apiKeyId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return repo.findAll().stream()
                .filter(r -> apiKeyId.equals(r.getApiKeyId()))
                .filter(r -> r.getTimestamp().isAfter(since))
                .collect(Collectors.groupingBy(
                        r -> r.getTimestamp().withSecond(0).withNano(0).toString(),
                        Collectors.counting()));
    }

    public List<ViolationResponse> recentViolations(int limit) {
        return repo.findAll(PageRequest.of(0, Math.max(5, limit), Sort.by(Sort.Direction.DESC, "timestamp")))
                .stream()
                .filter(r -> Boolean.FALSE.equals(r.getWasAllowed()))
                .map(r -> new ViolationResponse(
                        r.getApiKeyId(),
                        getReason(r),
                        r.getTimestamp().toString()))
                .toList();
    }

    private String getReason(RequestLog r) {
        if (r.isRateLimitExceeded())
            return "Exceeded per-minute limit";
        if (r.isDailyQuotaExceeded())
            return "Exceeded daily quota";
        return "Request blocked by policy";
    }

    public List<ViolationResponse> recentViolations(int limit, String appNameOrKey) {
        List<RequestLog> logs = repo
                .findAll(PageRequest.of(0, Math.max(5, limit), Sort.by(Sort.Direction.DESC, "timestamp"))).getContent();

        return logs.stream()
                .filter(r -> Boolean.FALSE.equals(r.getWasAllowed()))
                .filter(r -> appNameOrKey == null || r.getApiKeyId().equalsIgnoreCase(appNameOrKey))
                .map(r -> new ViolationResponse(
                        r.getApiKeyId(),
                        getReason(r),
                        r.getTimestamp() != null ? r.getTimestamp().toString() : "N/A"))
                .toList();
    }

}
