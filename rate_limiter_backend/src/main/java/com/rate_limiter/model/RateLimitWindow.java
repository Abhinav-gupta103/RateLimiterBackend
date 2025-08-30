package com.rate_limiter.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rate_limit_windows")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitWindow {
    @Id
    private String id;
    private String apiKeyId;
    private LocalDateTime windowStart;
    private Integer requestsCount;
    private String windowType; // e.g., "minute", "day"
}
