package createWorld.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyRedis {
    private static JedisPoolConfig config;
    private static JedisPool jedisPool;

    public static void init() {
        config = new JedisPoolConfig();
        config.setMaxIdle(10);
        config.setMaxWaitMillis(5000);
        config.setMaxTotal(50);
        config.setMinIdle(5);
    }

    public static void main(String[] args) {
        stringOp();
        hashOp();
        listOp();
        setOp();

    }

    private static void setOp() {
        Jedis jedis = jedisPool.getResource();
        jedis.sadd("setKey", "setValue1", "setValue2", "setValue3");
        Set<String> setKey = jedis.smembers("setKey");
        Long srem = jedis.srem("setKey", "setValue1");
        jedis.close();

    }

    private static void listOp() {
        Jedis jedis = jedisPool.getResource();
        Long lpush = jedis.lpush("listKey", "listValue1", "listValue2", "listValue3");
        String listKey1 = jedis.lpop("listKey");// listValue3
        List<String> listKey = jedis.lrange("listKey", 0, -1);
        jedis.close();
    }

    private static void hashOp() {
        Jedis jedis = jedisPool.getResource();
        jedis.hset("hsetKey", "mapKey", "mapValue");
        jedis.hset("hsetKey", "mapKey2", "mapValue2");
        Map<String, String> hsetKey = jedis.hgetAll("hsetKey");
        Set<String> keys = jedis.keys("hsetKey");
        jedis.hdel("hsetKey", "mapKey");
        jedis.del("hsetKey");
        jedis.close();
    }

    private static void stringOp() {
        Jedis jedis = jedisPool.getResource();
        jedis.set("key", "value");
        jedis.get("key");
        jedis.set("key", "newValue");
        jedis.del("key");
        jedis.incr("intKey");
        jedis.incrBy("intKey", 3);
        jedis.close();
    }

    public static void closePool() {
        jedisPool.close();
    }

}
