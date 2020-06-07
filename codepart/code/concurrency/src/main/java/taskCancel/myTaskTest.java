package taskCancel;

import javax.sound.midi.Soundbank;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhzeb
 * @date 2020/6/5 6:27
 */
public class myTaskTest implements Runnable {


    @Override
    public void run() {
        while (true) {
            if (!Thread.interrupted()) {
                System.out.println("i'm running 1");
            } else {
                break;
            }
        }
        while (true) {
            if (!Thread.currentThread().isInterrupted()) {
                System.out.println("i'm running 2");
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(new myTaskTest());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        future.cancel(true);
        executorService.shutdown();
    }
}