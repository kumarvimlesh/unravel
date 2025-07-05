package com.interview.unravel.stream;

public class LogProcessingApp {
	public static void main(String[] args) {
		LogProcessor processor = new LogProcessor();

		// Simulate producers
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				processor.produceLog("Log " + i, i % 5); // Varying priorities
			}
		}).start();

		// Start consumers
		processor.startProcessing(3); // 3 concurrent consumers
	}
}

