package threadPool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * @author zhengzebiao
 * @date 2020/1/17 14:10
 */
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor executor, int bound) {
        this.exec = executor;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                }finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e){
            semaphore.release();
        }
    }
}