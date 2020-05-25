package taskExecution;

import java.util.concurrent.*;

/**
 * 需求：当任务获取数据超时后取消任务
 * @author zhengzebiao
 * @date 2020/1/6 20:38
 */
public class RenderPageWithAd {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    void renderPageWithAd(){
        Future<String> f = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "page";
            }
        });
        long timeLeft = 10L;
        String ad;
        try {
             ad = f.get(timeLeft,TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            ad = "default ad";
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            ad = "default ad";
        } catch (TimeoutException e) {
            ad = "default ad";
            f.cancel(true);
        }
    }
}