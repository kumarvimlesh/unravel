package com.interview.unravel.deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockSimulator {
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    public void method1() {
        acquireLocks(lock1, lock2);
        try {
            System.out.println("Method1: Acquired lock1 and lock2");
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }

    public void method2() {
        acquireLocks(lock1, lock2);
        try {
            System.out.println("Method2: Acquired lock1 and lock2");
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }

    private void acquireLocks(Lock first, Lock second) {
        while (true) {
            boolean gotFirst = first.tryLock();
            boolean gotSecond = second.tryLock();

            if (gotFirst && gotSecond) {
                return;
            }
            if (gotFirst) first.unlock();
            if (gotSecond) second.unlock();

            try {
                Thread.sleep(10); // avoid busy wait
            } catch (InterruptedException ignored) {
            }
        }
    }


    public static void main(String[] args) {
        DeadlockSimulator simulator = new DeadlockSimulator();
        Thread t1 = new Thread(simulator::method1);
        Thread t2 = new Thread(simulator::method2);
        t1.start();
        t2.start();
    }
}