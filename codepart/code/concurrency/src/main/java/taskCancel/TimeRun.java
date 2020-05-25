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
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);


        taskThread.join(unit.toMillis(timeout));  // 阻塞一段时间,等待程序到时返回
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
            // 如果任务已经结束，那么执行取消操作也不会带来任何影响
            // 如果任务正在运行，那么将被中断
            task.cancel(true);
        }
    }

    public static void main(String[] args) {
    }
}