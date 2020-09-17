package createWorld.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MyRedisCluster {
    public void jedisClusterTest() throws IOException {
        Set<HostAndPort> portSet = new HashSet<HostAndPort>();
        portSet.add(new HostAndPort("node01", 7001));
        portSet.add(new HostAndPort("node01", 7002));
        portSet.add(new HostAndPort("node01", 7003));
        portSet.add(new HostAndPort("node01", 7004));
        portSet.add(new HostAndPort("node01", 7005));
        portSet.add(new HostAndPort("node01", 7006));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPoolConfig.setMaxTotal(30);
        jedisPoolConfig.setMinIdle(5);

        JedisCluster jedisCluster = new JedisCluster(portSet, jedisPoolConfig);

        jedisCluster.set("cluster", "clustervalue");

        jedisCluster.close();

    }
}
