package taskCancel;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * @author zhengzebiao
 * @date 2020/1/15 15:26
 */
public class LogService {

    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;

    private boolean isShutdown;
    private int reservations;

    public LogService(BlockingQueue<String> queue, PrintWriter writer) {
        this.queue = queue;
        loggerThread = new LoggerThread();
        this.writer = writer;
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("logger is shutdown");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        /* retry */
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}