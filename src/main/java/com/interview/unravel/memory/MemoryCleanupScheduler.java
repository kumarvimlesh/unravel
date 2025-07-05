package com.interview.unravel.memory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MemoryCleanupScheduler {

    private final MemoryManager memoryManager;

    public MemoryCleanupScheduler(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    @Scheduled(fixedRateString = "${session.cleanup.interval.ms:300000}") // default 5 min
    public void cleanup() {
        memoryManager.cleanupExpiredSessions();
    }
}
