package com.interview.unravel.concurrency;

public class Producer implements Runnable {
    private final LogProcessor processor;

    public Producer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            int priority = i % 5;
            processor.produceLog("Log " + i, priority);
        }
    }
}