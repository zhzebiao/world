package createWorld.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

public class ZookeeperJavaApi {

    /**
     * 创建永久性节点
     *
     * @throws Exception
     */
    @Test
    public void testCreatePersistentNode() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);

        // 获取客户端对象
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.174.101:2181,192.168.174.102:2181," +
                "192.168.174.103:2181", 1000, 1000, retryPolicy);
        // 开启客户端
        client.start();

        // 通过create创建节点，并指定节点类型
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hello/world");

        // 关闭客户端
        client.close();

    }


    /**
     * 创建临时性节点
     *
     * @throws Exception
     */
    @Test
    public void testCreateEphemeralNode() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.174.101:2181",
                3000, 3000, retryPolicy);

        client.start();

        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/hello5/world3");

        Thread.sleep(5000);

        client.close();
    }


    @Test
    public void testSetNodeData() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
        CuratorFramework client = CuratorFrameworkFactory.newClient("node01:2181,node02:2181,node03:2181",
                3000, 3000, retryPolicy);

        client.start();
        client.setData().forPath("/hello5", "hello".getBytes());
        client.close();
    }

    @Test
    public void testGetNodeData() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
        CuratorFramework client = CuratorFrameworkFactory.newClient("node01:2181,node02:2181,node03:2181",
                3000, 3000, retryPolicy);

        client.start();

        byte[] forPath = client.getData().forPath("/hello5");

        System.out.println(new String(forPath));
        client.close();
    }

    @Test
    public void testWatchNode() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 1);
        CuratorFramework client = CuratorFrameworkFactory.newClient("node01:2181,node02:2181,node03:2181",
                3000, 3000, retryPolicy);

        client.start();

        // 设置节点的cache
        TreeCache treeCache = new TreeCache(client, "/hello5");

        treeCache.getListenable().addListener(
                new TreeCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                        ChildData data = treeCacheEvent.getData();
                        if (data != null) {
                            switch (treeCacheEvent.getType()) {
                                case NODE_ADDED: {
                                    System.out.println("NODE_ADDED:" +
                                            data.getPath() + " 数据: " + new String(data.getData()));
                                    break;
                                }
                                case NODE_REMOVED: {
                                    System.out.println("NODE_REMOVED: " +
                                            data.getPath() + " 数据: " + new String(data.getData()));
                                    break;
                                }
                                case NODE_UPDATED: {
                                    System.out.println("NODE_UPDATED: " +
                                            data.getPath() + " 数据: " + new String(data.getData()));
                                    break;
                                }
                                default:
                                    break;

                            }
                        } else {
                            System.out.println("data is null : " + treeCacheEvent.getType());
                        }
                    }
                }
        );
        treeCache.start();

        Thread.sleep(500000000);

        client.close();
    }
}

