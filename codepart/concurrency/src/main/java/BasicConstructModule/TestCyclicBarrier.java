package BasicConstructModule;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 栅栏初体验。
 * 栅栏用来等待所有线程执行完毕。
 * 当所有线程到达了栅栏位置，那么栅栏将打开，此时所有线程都被释放，而栅栏将被重置以便下次使用
 *
 * @author zhengzebiao
 * @date 2020/1/2 15:29
 */
public class TestCyclicBarrier {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public TestCyclicBarrier(Board board) {
        this.mainBoard = board;
        int count = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(count,
                // 所有线程通过barrier的时候执行这个操作
                new Runnable() {
                    @Override
                    public void run() {
                        mainBoard.commitNewValue();
                    }
                });
        this.workers = new Worker[count];
        for (int i = 0; i < count; i++) {
            workers[i] = new Worker(mainBoard.getSubBoard(count, i));
        }
    }


    public void start() {
        for (int i = 0; i < workers.length; i++) {
            new Thread(workers[i]).start();
        }
        mainBoard.waitForConvergence();
    }

    private class Worker implements Runnable {
        private final Board board;

        public Worker(Board board) {
            this.board = board;
        }

        @Override
        public void run() {
            while (!board.hasConverged()) {
                for (int x = 0; x < board.getMaxX(); x++) {
                    for (int y = 0; y < board.getMaxY(); y++) {
                        board.setNewValue(x, y, x + y);
                    }
                }

                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Board {
        private int x, y, value;

        public boolean hasConverged() {
            return true;
        }

        public int getMaxX() {
            return x;
        }

        public int getMaxY() {
            return y;
        }

        public void setNewValue(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        public void commitNewValue() {

        }

        public Board getSubBoard(int x, int y) {
            return new Board();
        }

        public void waitForConvergence() {
        }
    }
}

