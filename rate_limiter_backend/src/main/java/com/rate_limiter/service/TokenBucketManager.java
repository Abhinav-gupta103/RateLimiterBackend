package com.rate_limiter.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory token bucket per (apiKeyId + endpoint).
 * Refill rate: ratePerMinute / 60 tokens per second.
 * Capacity: burstMultiplier * ratePerMinute.
 */
@Component
public class TokenBucketManager {

    private static final class Bucket {
        double tokens;
        long lastRefillNanos;
        int capacity;
        double refillPerSecond;
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private String key(String apiKeyId, String endpoint) {
        return apiKeyId + "|" + (endpoint == null ? "*" : endpoint);
    }

    public boolean allow(String apiKeyId, String endpoint, int ratePerMinute, double burstMultiplier) {
        if (ratePerMinute <= 0)
            return false;
        long now = System.nanoTime();

        Bucket b = buckets.computeIfAbsent(key(apiKeyId, endpoint), k -> {
            Bucket nb = new Bucket();
            nb.capacity = (int) Math.max(ratePerMinute * burstMultiplier, ratePerMinute);
            nb.refillPerSecond = ratePerMinute / 60.0;
            nb.tokens = nb.capacity; // start full
            nb.lastRefillNanos = now;
            return nb;
        });

        synchronized (b) {
            // refill
            double elapsedSec = (now - b.lastRefillNanos) / 1_000_000_000.0;
            if (elapsedSec > 0) {
                b.tokens = Math.min(b.capacity, b.tokens + elapsedSec * b.refillPerSecond);
                b.lastRefillNanos = now;
            }
            if (b.tokens >= 1.0) {
                b.tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    public void reset(String apiKeyId) {
        buckets.keySet().removeIf(k -> k.startsWith(apiKeyId + "|"));
    }
}
