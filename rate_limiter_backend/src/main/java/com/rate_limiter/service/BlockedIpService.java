package com.rate_limiter.service;

import org.springframework.stereotype.Service;

import com.rate_limiter.model.BlockedIp;
import com.rate_limiter.repository.BlockedIpRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlockedIpService {

    private final BlockedIpRepository repo;

    public BlockedIpService(BlockedIpRepository repo) {
        this.repo = repo;
    }

    public BlockedIp block(String ip, LocalDateTime until, String reason) {
        Optional<BlockedIp> existing = repo.findByIpAddress(ip);
        BlockedIp b = existing.orElseGet(BlockedIp::new);
        b.setIpAddress(ip);
        b.setBlockedUntil(until);
        b.setReason(reason);
        if (b.getCreatedAt() == null)
            b.setCreatedAt(LocalDateTime.now());
        return repo.save(b);
    }

    public void unblock(String ip) {
        repo.findByIpAddress(ip).ifPresent(found -> repo.deleteById(found.getId()));
    }

    public boolean isBlocked(String ip) {
        return repo.findByIpAddress(ip)
                .filter(b -> b.getBlockedUntil() == null || b.getBlockedUntil().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public List<BlockedIp> listActive() {
        LocalDateTime now = LocalDateTime.now();
        return repo.findAll().stream()
                .filter(b -> b.getBlockedUntil() == null || b.getBlockedUntil().isAfter(now))
                .toList();
    }
}
