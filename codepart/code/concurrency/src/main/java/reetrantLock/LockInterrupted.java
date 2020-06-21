package reetrantLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhzeb
 * @date 2020/6/16 6:28
 */
public class LockInterrupted {

    private Lock lock = new ReentrantLock();
    ReadWriteLock

    public boolean sendOnSharedLine(String message) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            cancellableSendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
        return false;
    }

    private boolean cancellableSendOnSharedLine(String message) throws InterruptedException {

    }
}