package chapter14_;

import javax.swing.*;
import java.time.Period;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhzeb
 * @date 2020/6/20 15:32
 */
public class SemphoreOnLock {
    private final Lock lock = new ReentrantLock();
    // 条件谓词： permitsAvailable(permits > 0)
    private final Condition permitsAvailable = lock.newCondition();

    private int permits;

    SemphoreOnLock(int initialPermits) {
        lock.lock();
        try {
            permits = initialPermits;
        } finally {
            lock.unlock();
        }
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits == 0)
                permitsAvailable.await();
            --permits;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            ++permits;
            permitsAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}