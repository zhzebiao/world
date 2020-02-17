import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author zhengzebiao
 * @date 2019/12/17 23:52
 */
public class SecureRandomTest {
    public static void main(String[] args) {
        SecureRandom sr = null;
        try{
            sr = SecureRandom.getInstanceStrong();  // 获取高强度安全随机数生成器
        } catch (NoSuchAlgorithmException e) {
            sr = new SecureRandom();    // 获取普通的安全随机数生成器
        }
        byte[] buffer = new byte[16];
        sr.nextBytes(buffer); // 用安全随机数填充buffer
        System.out.println(Arrays.toString(buffer));
        System.out.println(sr.nextInt(100));
    }

}