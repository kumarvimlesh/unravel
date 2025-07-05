package com.interview.unravel.concurrency;

/**
 * @param priority lower -> higher priority
 */
public record LogTask(String message, int priority) implements Comparable<LogTask> {

    @Override
    public int compareTo(LogTask other) {
        return Integer.compare(this.priority, other.priority);
    }
}