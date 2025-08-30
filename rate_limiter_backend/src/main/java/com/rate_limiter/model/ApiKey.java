package com.rate_limiter.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "api_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey {
    @Id
    private String id;
    private String keyValue;
    private String appName;
    private Integer rateLimitPerMinute;
    private Integer dailyQuota;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Map<String, Integer> endpointLimits;
}