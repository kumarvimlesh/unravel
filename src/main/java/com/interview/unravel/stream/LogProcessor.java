package com.interview.unravel.stream;

import java.util.Comparator;
import java.util.concurrent.*;

public class LogProcessor {
    private final PriorityBlockingQueue<LogTask> taskQueue = new PriorityBlockingQueue<>(100, Comparator.comparingInt(LogTask::priority).reversed());

    /**
     * Adds a log message with the given priority to the queue.
     * Lower priority number -> higher processing priority.
     */
    public void produceLog(String message, int priority) {
        taskQueue.put(new LogTask(message, priority));
    }

    /**
     * Consumes the highest-priority log message.
     * Blocks if queue is empty until a log becomes available.
     */
    public LogTask consumeLog() throws InterruptedException {
        return taskQueue.take();
    }

    /**
     * Returns the current size of the log queue.
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    public void startProcessing(int consumerCount) {
        ExecutorService consumers = Executors.newFixedThreadPool(consumerCount);
        for (int i = 0; i < consumerCount; i++) {
            consumers.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        LogTask task = taskQueue.take();
                        System.out.println("Processed: " + task.message() + " [Priority: " + task.priority() + "]");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }
}
