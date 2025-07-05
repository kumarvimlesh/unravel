package com.interview.unravel.stream;

/**
 * @param priority lower -> higher priority
 */
public record LogTask(String message, int priority) implements Comparable<LogTask> {

    @Override
    public int compareTo(LogTask other) {
        return Integer.compare(this.priority, other.priority);
    }
}