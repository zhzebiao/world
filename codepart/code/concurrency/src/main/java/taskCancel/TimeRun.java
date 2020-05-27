package taskCancel;

import java.util.concurrent.*;

/**
 * @author zhengzebiao
 * @date 2020/1/8 16:15
 */
public class TimeRun {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(10);


    public static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null) {
                    throw new RuntimeException(t);
                }
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        // 任务线程启动
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            // 需要在taskThread中运行的Task能够响应中断
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);

        // 阻塞一段时间，在一段时间内监控taskThread的状态
        // join会在taskThread执行完成之后，或者超时的时候返回
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }

    public static void timedRun1(Runnable r, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        ExecutorService taskExec = Executors.newSingleThreadExecutor();
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (ExecutionException e) {
            throw e;
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            // mayInterruptIfRunning: true:如果运行则中断 false:如果运行则不进行任何操作
            // 如果任务已经结束，那么执行取消操作也不会带来任何影响
            // 如果任务正在运行，那么将被中断
            task.cancel(true);
        }
    }

    public static void main(String[] args) {
    }
}