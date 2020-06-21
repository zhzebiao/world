package chapter14_;

import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;

/**
 * @author zhzeb
 * @date 2020/6/20 16:20
 */
public class OneShotLatch {

    private final Sync sync = new Sync();

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireInterruptibly(0);
    }

    private class Sync extends AbstractQueuedLongSynchronizer {
        protected int tryAcquireShared(int ignored) {
            // 如果闭锁是开的(state == 1)，那么这个操作将成功，否则将失败
            return (getState() == 1) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int ignored) {
            setState(1);
            return true;
        }
    }
}