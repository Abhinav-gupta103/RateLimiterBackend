package com.rate_limiter.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationResponse {
    private String apiKey;
    private String reason;
    private String timestamp;
}
