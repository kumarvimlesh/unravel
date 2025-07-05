package com.interview.unravel.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class SessionRepository {

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final Duration TTL = Duration.ofHours(1);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveSession(String userId, String sessionId) {
        redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + userId, sessionId, TTL);
    }

    public String getSession(String userId) {
        return redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + userId);
    }

    public boolean deleteSession(String userId) {
        return redisTemplate.delete(SESSION_KEY_PREFIX + userId);
    }
}
