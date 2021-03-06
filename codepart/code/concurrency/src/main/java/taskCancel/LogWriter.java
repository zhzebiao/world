package taskCancel;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhengzebiao
 * @date 2020/1/15 15:12
 */
public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingQueue<>();
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    // 这种直接关闭的做法会丢失那些正在等待被写入到日志的消息。
    // 当取消一个生产者-消费者操作时，需要同时取消生产者和消费者。
    public void stop() {
        logger.interrupt();
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        private LoggerThread(Writer writer) {
            this.writer = (PrintWriter) writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    writer.println(queue.take());
                }
            } catch (InterruptedException ignored) {
            } finally {
                writer.close();
            }
        }
    }
}