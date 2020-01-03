package TaskExecution;

import java.util.concurrent.Executors;

/**
 * @author zhengzebiao
 * @date 2020/1/3 11:15
 */
public class ThreadPool {
    private final static int NTHREAD = 10;
    public static void main(String[] args) {
        // 固定长度的线程池
        Executors.newFixedThreadPool(NTHREAD);
        // 不固定长度的缓存线程池
        Executors.newCachedThreadPool();
        // 单线程Executor
        Executors.newSingleThreadExecutor();
        // 固定长度线程池，以延迟或者定时的方式执行任务
        Executors.newScheduledThreadPool(NTHREAD);

    }
}