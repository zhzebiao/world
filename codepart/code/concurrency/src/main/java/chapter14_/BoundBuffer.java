package chapter14_;

/**
 * @author zhzeb
 * @date 2020/6/18 7:53
 */
public class BoundBuffer<V> extends BaseBoundedBuffer<V> {

    public BoundBuffer(int capacity) {
        super(capacity);
    }

    public synchronized void put(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        doPut(v);
        notifyAll();
    }

    public synchronized V take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        V v = doTake();
        notifyAll();
        return v;
    }
}