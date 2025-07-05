package com.interview.unravel.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PoolMonitor {

    @Autowired
    private HikariDataSource dataSource;

    @Scheduled(fixedRate = 60000) // every 1 min
    /**
     * Monitors HikariCP usage every 1 minute and dynamically adjusts pool size based on usage.
     */
    public void monitorPoolUsage() {
        int active = dataSource.getHikariPoolMXBean().getActiveConnections();
        int idle = dataSource.getHikariPoolMXBean().getIdleConnections();
        int total = dataSource.getHikariPoolMXBean().getTotalConnections();
        int waiting = dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
        int maxPoolSize = dataSource.getMaximumPoolSize();

        log.info("HikariCP Stats - Active: {}, Idle: {}, Total: {}, Waiting: {}, MaxSize: {}%n",
                active, idle, total, waiting, maxPoolSize);

        // Log warnings
        if (waiting > 5) {
            log.warn("High contention:  {} threads waiting for DB connections.", waiting);
        }
        if (idle >= total * 0.8) {
            log.warn("Underutilized pool: Over 80% of connections are idle.");
        }

        // Auto-adjust pool size based on usage
        if (waiting > 10 && maxPoolSize < 100) {
            int newSize = Math.min(maxPoolSize + 10, 100);
            dataSource.setMaximumPoolSize(newSize);
            log.info("Increased pool size to: {}", newSize);
        } else if (idle > 20 && maxPoolSize > 20) {
            int newSize = Math.max(maxPoolSize - 10, 20);
            dataSource.setMaximumPoolSize(newSize);
            log.info("Decreased pool size to: {}", newSize);
        }
    }
}
