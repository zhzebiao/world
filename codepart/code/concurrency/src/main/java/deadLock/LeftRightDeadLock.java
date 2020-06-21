package deadLock;

/**
 * @author zhzeb
 * @date 2020/6/9 6:18
 */

/**
 * 两个线程试图以不同的顺序来获得相同的锁，可能就会出现循环的锁依赖。
 */
public class LeftRightDeadLock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                // doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
//                doSomething();
            }
        }
    }
}