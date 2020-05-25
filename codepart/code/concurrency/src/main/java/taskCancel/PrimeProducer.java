package taskCancel;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * @author zhengzebiao
 * @date 2020/1/7 21:32
 */
public class PrimeProducer extends Thread {

    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException e) {

        }
    }

    public void cancel() {
        interrupt();
    }

}