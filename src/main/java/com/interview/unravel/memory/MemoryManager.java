package com.interview.unravel.memory;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryManager {

    private final long ttlMs;
    private final Map<String, SessionMemory> sessionCache = new ConcurrentHashMap<>();

    public MemoryManager(SessionMemoryProperties properties) {
        this.ttlMs = properties.getTtlMs();
    }

    public void addSessionData(String sessionId) {
        sessionCache.put(sessionId, new SessionMemory(new byte[10 * 1024 * 1024], System.currentTimeMillis()));
    }

    public void removeSessionData(String sessionId) {
        sessionCache.remove(sessionId);
    }

    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessionCache.entrySet().removeIf(entry -> now - entry.getValue().timestamp() > ttlMs);
    }
}