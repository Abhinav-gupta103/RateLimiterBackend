package com.rate_limiter.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.rate_limiter.model.RateLimitWindow;
import com.rate_limiter.model.RequestLog;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RateLimitWindowService {

    private final MongoTemplate mongoTemplate;

    public RateLimitWindowService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public int incrementMinute(String apiKeyId) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return incrementWindow(apiKeyId, start, "minute");
    }

    public int incrementDay(String apiKeyId) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        return incrementWindow(apiKeyId, start, "day");
    }

    public int getMinuteCount(String apiKeyId) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        RateLimitWindow w = find(apiKeyId, start, "minute");
        return w == null ? 0 : w.getRequestsCount();
    }

    public int getDayCount(String apiKeyId) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        RateLimitWindow w = find(apiKeyId, start, "day");
        return w == null ? 0 : w.getRequestsCount();
    }

    private int incrementWindow(String apiKeyId, LocalDateTime windowStart, String type) {
        Query q = Query.query(Criteria.where("apiKeyId").is(apiKeyId)
                .and("windowStart").is(windowStart)
                .and("windowType").is(type));

        Update u = new Update().inc("requestsCount", 1)
                .setOnInsert("apiKeyId", apiKeyId)
                .setOnInsert("windowStart", windowStart)
                .setOnInsert("windowType", type);

        var res = mongoTemplate.upsert(q, u, RateLimitWindow.class);
        // After upsert, fetch to return current count
        RateLimitWindow w = mongoTemplate.findOne(q, RateLimitWindow.class);
        return w == null ? 1 : w.getRequestsCount();
    }

    private RateLimitWindow find(String apiKeyId, LocalDateTime windowStart, String type) {
        return mongoTemplate.findOne(
                Query.query(Criteria.where("apiKeyId").is(apiKeyId)
                        .and("windowStart").is(windowStart)
                        .and("windowType").is(type)),
                RateLimitWindow.class);
    }

    public int incrementEndpoint(String apiKeyId, String endpoint) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return incrementCustomWindow(apiKeyId, start, "endpoint:" + endpoint);
    }

    public int getEndpointCount(String apiKeyId, String endpoint) {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        RateLimitWindow w = find(apiKeyId, start, "endpoint:" + endpoint);
        return w == null ? 0 : w.getRequestsCount();
    }

    private int incrementCustomWindow(String apiKeyId, LocalDateTime windowStart, String type) {
        Query q = Query.query(Criteria.where("apiKeyId").is(apiKeyId)
                .and("windowStart").is(windowStart)
                .and("windowType").is(type));

        Update u = new Update().inc("requestsCount", 1)
                .setOnInsert("apiKeyId", apiKeyId)
                .setOnInsert("windowStart", windowStart)
                .setOnInsert("windowType", type);

        mongoTemplate.upsert(q, u, RateLimitWindow.class);
        RateLimitWindow w = mongoTemplate.findOne(q, RateLimitWindow.class);
        return w == null ? 1 : w.getRequestsCount();
    }

    public int getSlidingWindowCount(String apiKeyId, int seconds) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(seconds);
        Query q = Query.query(Criteria.where("apiKeyId").is(apiKeyId)
                .and("timestamp").gte(since));
        return (int) mongoTemplate.count(q, RequestLog.class);
    }
}
