package redis;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author zhengzebiao
 * @date 2019/12/22 14:11
 */
public class RedisStringJava {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.set("string","value");
        System.out.println(jedis.get("string"));

        jedis.lpush("list","value1");
        jedis.lpush("list","value2");
        jedis.lpush("list","value3");
        List<String> list = jedis.lrange("list",0,2);
    }
}