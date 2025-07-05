package com.interview.unravel.memory;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "session.memory")
public class SessionMemoryProperties {
    private long ttlMs;
}
