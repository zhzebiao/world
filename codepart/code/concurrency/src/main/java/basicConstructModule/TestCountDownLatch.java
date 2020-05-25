package basicConstructModule;

import java.util.concurrent.CountDownLatch;

/**
 * 闭锁的初体验
 * 主要用在资源的初始化和参与者的就绪状态保证
 *
 * @author zhengzebiao
 * @date 2020/1/2 14:32
 */
public class TestCountDownLatch {

    /**
     * CountDownLatch() : 表示一个事件已经发生
     * await() : 阻塞方法，等待计数器达到零，表示所有需要等待的事件都已经发生
     *
     * @param nThreads 需要执行的总线程数
     * @param task 所有线程需要执行的操作
     * @return 所有线程执行完操作后花费时间
     * @throws InterruptedException 中断异常
     */
    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for(int i=0; i < nThreads; i++){
            Thread t = new Thread(() -> {
                try {
                    // 阻塞，等到开始执行的指令，保证所有线程都同时开始
                    startGate.await();
                    try {
                        // 资源初始化操作 or 参与者就绪初始化
                        task.run();
                    } finally {
                        // 标明当前线程初始化结束
                        endGate.countDown();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });

            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();
        long end = System.nanoTime();
        return end - start;
    }
}