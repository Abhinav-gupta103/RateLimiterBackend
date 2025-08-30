package com.rate_limiter.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

@Document(collection = "blocked_ips")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockedIp {
    @Id
    private String id;
    @NotNull
    private String ipAddress;
    private LocalDateTime blockedUntil;
    private String reason;
    private LocalDateTime createdAt;
}
