package TaskCancel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author zhengzebiao
 * @date 2020/1/7 17:37
 */
public class PrimeGenerator implements Runnable {

    private final List<BigInteger> primes = new ArrayList<>();
    /**
     * 中断标签，volatile保证其可见性
     */
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    public static void main(String[] args) throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try{
            SECONDS.sleep(1);
        }finally {
            generator.cancel();
        }
    }

}