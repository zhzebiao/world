package taskExecution;

import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zhengzebiao
 * @date 2020/1/3 15:00
 */
public class FutureRender {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    void renderPage(CharSequence source) {
        final List<String> imageInfos = new ArrayList<>(5);
        Callable<List<Integer>> task = new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() throws Exception {
                List<Integer> result = new ArrayList<>();
                for (String info : imageInfos) {
                    result.add(Integer.parseInt(info));
                }
                return result;
            }
        };
        Future<List<Integer>> future = executor.submit(task);

        try {
            List<Integer> imageData = future.get();
            for(Integer data:imageData){
                System.out.println(data);
            }
        } catch (InterruptedException e) {
            // 重新设置线程的中断状态
            Thread.currentThread().interrupt();
            // 由于不需要结果，因此取消任务
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}