package com.rate_limiter.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "request_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestLog {
    @Id
    private String id;
    private String apiKeyId;
    private String endpoint;
    private String ipAddress;
    private LocalDateTime timestamp;
    private Boolean wasAllowed;
    private boolean rateLimitExceeded;
    private boolean dailyQuotaExceeded;

    public boolean isRateLimitExceeded() {
        return rateLimitExceeded;
    }

    public boolean isDailyQuotaExceeded() {
        return dailyQuotaExceeded;
    }

}
