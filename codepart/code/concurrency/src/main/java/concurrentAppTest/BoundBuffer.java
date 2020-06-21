package concurrentAppTest;

import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * @author zhzeb
 * @date 2020/6/12 6:45
 */
public class BoundBuffer<E> {

    private final Semaphore availableItems, availableSpace;

    private final E[] items;
    private int putPostion = 0, takePosition = 0;

    public BoundBuffer(int capacity) {
        availableItems = new Semaphore(0);
        availableSpace = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpace.availablePermits() == 0;
    }

    public void put(E x) throws InterruptedException {
        availableSpace.acquire();
        doInsert(x);
        availableItems.release();
    }

    public E take() throws InterruptedException {
        availableItems.acquire();
        E item = doExtract();
        availableSpace.release();
        return item;
    }

    private synchronized void doInsert(E x) {
        int i = putPostion;
        items[i] = x;
        putPostion = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E x = items[i];
        items[i] = null;
        takePosition = (++i == items.length) ? 0 : 1;
        return x;
    }

}