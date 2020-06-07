package taskCancel;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhzeb
 * @date 2020/6/6 7:12
 */
public class LogServiceBetter {
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private static final int TIMEOUT = 30;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private final PrintWriter writer;

    public LogServiceBetter(PrintWriter writer) {
        this.writer = writer;
    }

    public void start() {

    }

    public void stop() throws InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination(TIMEOUT, UNIT);
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            exec.execute(new WriteTask(msg));
        } catch (RejectedExecutionException ignored) {
        }
    }
}

class WriteTask implements Runnable {

    private final String msg;
    private PrintWriter writer;

    public WriteTask(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        writer.println(msg);
    }
}