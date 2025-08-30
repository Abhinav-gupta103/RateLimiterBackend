package com.rate_limiter.service;

import org.springframework.stereotype.Service;

import com.rate_limiter.model.ApiKey;
import com.rate_limiter.repository.ApiKeyRepository;

@Service
public class RateLimitingService {

    private final ApiKeyRepository apiKeyRepo;
    private final BlockedIpService blockedIpService;
    private final TokenBucketManager tokenBucketManager;
    private final RateLimitWindowService windowService;
    private final RequestLogService requestLogService;

    private static final double BURST_MULTIPLIER = 2.0;

    public RateLimitingService(ApiKeyRepository apiKeyRepo,
            BlockedIpService blockedIpService,
            TokenBucketManager tokenBucketManager,
            RateLimitWindowService windowService,
            RequestLogService requestLogService) {
        this.apiKeyRepo = apiKeyRepo;
        this.blockedIpService = blockedIpService;
        this.tokenBucketManager = tokenBucketManager;
        this.windowService = windowService;
        this.requestLogService = requestLogService;
    }

    public CheckResult checkAndRecord(String apiKeyValue, String endpoint, String ip) {
        ApiKey key = apiKeyRepo.findByKeyValue(apiKeyValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid API key"));

        if (!Boolean.TRUE.equals(key.getIsActive())) {
            requestLogService.log(key.getId(), endpoint, ip, false);
            return CheckResult.denied("API key inactive");
        }

        if (blockedIpService.isBlocked(ip)) {
            requestLogService.log(key.getId(), endpoint, ip, false);
            return CheckResult.denied("IP blocked");
        }

        // Token bucket (burst handling)
        boolean tokenOk = tokenBucketManager.allow(key.getId(), endpoint, key.getRateLimitPerMinute(),
                BURST_MULTIPLIER);
        if (!tokenOk) {
            requestLogService.log(key.getId(), endpoint, ip, false);
            return CheckResult.denied("Burst limit exceeded");
        }

        // Sliding window for last 60 seconds
        int slidingCount = windowService.getSlidingWindowCount(key.getId(), 60);
        if (slidingCount >= key.getRateLimitPerMinute()) {
            requestLogService.log(key.getId(), endpoint, ip, false);
            return CheckResult.denied("Per-minute limit exceeded (Sliding Window)");
        }

        // Daily quota check
        int dayCount = windowService.getDayCount(key.getId());
        if (dayCount >= key.getDailyQuota()) {
            requestLogService.log(key.getId(), endpoint, ip, false);
            return CheckResult.denied("Daily quota exceeded");
        }

        // Endpoint-specific limit check
        if (endpoint != null && key.getEndpointLimits() != null && key.getEndpointLimits().containsKey(endpoint)) {
            int endpointLimit = key.getEndpointLimits().get(endpoint);
            int endpointCount = windowService.getEndpointCount(key.getId(), endpoint);
            if (endpointCount >= endpointLimit) {
                requestLogService.log(key.getId(), endpoint, ip, false);
                return CheckResult.denied("Endpoint-specific limit exceeded");
            }
        }

        // Increment counters
        windowService.incrementMinute(key.getId());
        windowService.incrementDay(key.getId());
        if (endpoint != null) {
            windowService.incrementEndpoint(key.getId(), endpoint);
        }

        requestLogService.log(key.getId(), endpoint, ip, true);
        return CheckResult.allowed(slidingCount + 1, key.getRateLimitPerMinute(), dayCount + 1, key.getDailyQuota());
    }

    public ApiKey getApiKey(String apiKeyValue) {
        return apiKeyRepo.findByKeyValue(apiKeyValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid API key"));
    }

    public RateLimitWindowService getWindowService() {
        return windowService;
    }

    public record CheckResult(boolean allowed, String reason,
            int minuteUsed, int minuteLimit,
            int dayUsed, int dayLimit) {
        public static CheckResult allowed(int mu, int ml, int du, int dl) {
            return new CheckResult(true, "OK", mu, ml, du, dl);
        }

        public static CheckResult denied(String reason) {
            return new CheckResult(false, reason, 0, 0, 0, 0);
        }
    }
}
