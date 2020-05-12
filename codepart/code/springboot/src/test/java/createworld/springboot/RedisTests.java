//package createworld.springboot;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
///**
// * @author zhengzebiao
// * @date 2019/12/19 14:39
// */
//@SpringBootTest
//public class RedisTests {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    public void testRedis() {
//
//
//        String key = "createWorld";
//        String value = "true";
//        Long time = 60L;
//
//        if (stringRedisTemplate.opsForValue().get(key) != null) {
//            stringRedisTemplate.delete(key);
//        }
//
//        //
//        stringRedisTemplate.opsForValue().set(key, value, time);
//
//        System.out.println(stringRedisTemplate.opsForValue().get(key));
//
//    }
//}