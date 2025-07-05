# Unravel WebApp - Backend Improvements

This project implements critical backend improvements to enhance **concurrency**, **session management**, **memory handling**, and **database pooling** under high load.

## Features & Fixes

---

### Session Management

- **Distributed Locking with Redisson**  
  Prevents concurrent login/logout conflicts across nodes using `ReentrantLock` via `RedissonClient.getLock("lock:" + userId)`.

- **Redis-Backed Session Repository**  
  Centralized and persistent session storage using Redis, abstracted through a `SessionRepository` interface.

- **Custom Exception Handling**  
  Added `SessionNotFoundException` to handle null session reads. Lock and Redis access wrapped in `try-catch-finally`.

- **Layered Design (Service → Repository)**  
  Ensures separation of concerns for better maintainability and unit testing.

---

###  Memory Management

- **Thread-Safe Session Map**  
  Switched to `ConcurrentHashMap` to avoid concurrency issues in session storage.

- **TTL-Based Expiry**  
  Each session entry has a timestamp. Sessions expire automatically to prevent memory leaks.

- **Scheduled Cleanup Task**  
  Periodically clears expired sessions with `cleanupExpiredSessions()`.

---

### Concurrency Optimization

- **Replaced Synchronized Blocks**  
  Migrated from manual `synchronized/wait/notify` to high-performance `PriorityBlockingQueue`.

- **Priority-Based Task Execution**  
  Tasks sorted by priority with `LogTask implements Comparable<LogTask>`.

- **Producer-Consumer Architecture**  
  Scalable thread pool using `ExecutorService`. Easily configurable via `startProcessing(int consumers)`.

- **Modular Design**
    - `LogTask`: Encapsulates log data and priority.
    - `Producer` / `Consumer`: Task input/output logic.
    - `LogProcessor`: Central queue and orchestration.
    - `LogProcessingApp`: Bootstrap class.

---

### Deadlock Resolution

- **Switched to ReentrantLock with tryLock()**  
  Eliminated circular waiting using non-blocking lock acquisition in `acquireLocks()`.

- **Lock Retry Strategy**  
  Releases acquired lock if both can't be obtained, then retries after short delay to prevent starvation.

- **Encapsulated Locking Logic**  
  Reusable and readable `acquireLocks()` method ensures consistent lock ordering and handling.

---

### Database Connection Pooling (HikariCP)

- **Custom Monitoring & Logging**  
  Logs connection pool stats every 10s:
    - Active
    - Idle
    - Total
    - Waiting threads

- **Warning Logs**
    - Waiting threads > 5 → high contention
    - Idle connections > 80% of pool → underutilization

- **Dynamic Pool Resizing**
    - Scales up when waiting threads > 10 (up to max 100)
    - Scales down when idle > 20 and pool size > 20

---

## Tech Stack

- Java 17+
- Spring Boot
- Redis
- Redisson
- HikariCP
- Concurrent Utilities (`ExecutorService`, `PriorityBlockingQueue`, `ReentrantLock`)

---

## How to Run

```bash
# Clone the repo
git clone https://github.com/yourname/unravel-backend.git
cd unravel-backend

# Build and run the app
./mvnw spring-boot:run
