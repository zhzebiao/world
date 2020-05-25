package objectShared;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author zhengzebiao
 * @date 2019/12/22 16:25
 */
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger i, BigInteger[] factors){
        lastNumber = i;
        lastFactors = Arrays.copyOf(factors,factors.length);
    }
    public BigInteger[] getLastFactors(BigInteger i){
        if(lastFactors==null || !lastNumber.equals(i)){
            return null;
        }else {
            return Arrays.copyOf(lastFactors,lastFactors.length);
        }
    }
}