package com.interview.unravel.session;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionManager {

    @Value("${session.key.prefix}")
    private String sessionKeyPrefix;

    @Value("${session.lock.wait-time}")
    private long lockWaitTime;

    @Value("${session.lock.lease-time}")
    private long lockLeaseTime;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SessionRepository sessionRepository; // Redis-backed repo

    public String login(String userId) {
        RLock lock = redissonClient.getLock("lock:" + userId);
        boolean locked = false;
        try {
            locked = lock.tryLock(lockWaitTime, lockLeaseTime, TimeUnit.SECONDS);
            if (!locked) {
                return "Unable to acquire lock. Please try again.";
            }

            String existingSession = sessionRepository.getSession(userId);
            if (existingSession != null) {
                return "User already logged in. Session ID: " + existingSession;
            }

            String sessionId = "SESSION_" + UUID.randomUUID();
            sessionRepository.saveSession(userId, sessionId);
            return "Login successful. Session ID: " + sessionId;
        } catch (Exception e) {
            return "Login failed: " + e.getMessage();
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public String logout(String userId) {
        RLock lock = redissonClient.getLock("lock:" + userId);
        boolean locked = false;
        try {
            locked = lock.tryLock(lockWaitTime, lockLeaseTime, TimeUnit.SECONDS);
            if (!locked) {
                return "Unable to acquire lock. Please try again.";
            }

            boolean removed = sessionRepository.deleteSession(userId);
            return removed ? "Logout successful." : "User not logged in.";
        } catch (Exception e) {
            return "Logout failed: " + e.getMessage();
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public String getSessionDetails(String userId) {
        String sessionId = sessionRepository.getSession(userId);
        if (sessionId == null) {
            throw new SessionNotFoundException("Session not found for user " + userId);
        }
        return "Session ID for user " + userId + ": " + sessionId;
    }
}
