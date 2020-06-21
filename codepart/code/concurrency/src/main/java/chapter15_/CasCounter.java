package chapter15_;

/**
 * @author zhzeb
 * @date 2020/6/20 16:50
 */
public class CasCounter {

    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));
        return v + 1;
    }


    private class SimulatedCAS {
        private int value;

        public synchronized int get() {
            return value;
        }

        public synchronized int compareAndSwap(int expectedValue, int newValue) {
            int oldValue = value;
            if (oldValue == expectedValue)
                value = newValue;
            return oldValue;
        }

        public synchronized boolean compareAndSet(int expectedValue, int newValue) {
            return expectedValue == compareAndSwap(expectedValue, newValue);
        }

    }
}