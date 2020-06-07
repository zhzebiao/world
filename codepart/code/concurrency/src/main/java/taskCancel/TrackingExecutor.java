package taskCancel;

import taskExecution.ThreadPool;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author zhengzebiao
 * @date 2020/1/15 17:09
 */
public class TrackingExecutor extends AbstractExecutorService {

    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    @Override
    public void shutdown() {
        exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated()) {
            throw new IllegalStateException("线程池正常运行中");
        }
        return new ArrayList<>(tasksCancelledAtShutdown);
    }

    @Override
    public void execute(Runnable command) {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    command.run();
                } finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted()) {
                        tasksCancelledAtShutdown.add(command);
                    }
                }
            }
        });
    }

}