package threadSafe;

import java.math.BigInteger;

/**
 * @author zhengzebiao
 * @date 2019/12/22 15:58
 */
public class CacheFactorizer {
    private BigInteger lastNumber;
    private BigInteger[] lastFactors;
    private long hits;
    private long cacheHits;

    public synchronized long getHits() {
        return hits;
    }

    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits;
    }

    public void service(BigInteger number) {
        BigInteger i = number;
        BigInteger[] factors = null;

        synchronized (this) {
            ++hits;
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors;
            }
        }
        if (factors == null) {
            factors = factor(i);
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors;
            }
        }
    }

    private BigInteger[] factor(BigInteger number) {
        return new BigInteger[]{};
    }
}