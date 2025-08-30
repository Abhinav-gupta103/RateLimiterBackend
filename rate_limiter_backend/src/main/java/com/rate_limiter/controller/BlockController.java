package com.rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rate_limiter.model.BlockedIp;
import com.rate_limiter.service.BlockedIpService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BlockController {

    private final BlockedIpService blockedIpService;

    public BlockController(BlockedIpService blockedIpService) {
        this.blockedIpService = blockedIpService;
    }

    @PostMapping("/block-ip")
    public ResponseEntity<BlockedIp> blockIp(@RequestParam String ip,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long durationMinutes) {
        LocalDateTime until = durationMinutes != null ? LocalDateTime.now().plusMinutes(durationMinutes)
                : LocalDateTime.now().plusHours(1);
        BlockedIp blocked = blockedIpService.block(ip, until, reason != null ? reason : "Suspicious activity");
        return ResponseEntity.ok(blocked);
    }

    @DeleteMapping("/block-ip/{ip}")
    public ResponseEntity<Void> unblockIp(@PathVariable String ip) {
        blockedIpService.unblock(ip);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked-ips")
    public ResponseEntity<List<BlockedIp>> listBlockedIps() {
        return ResponseEntity.ok(blockedIpService.listActive());
    }
}
