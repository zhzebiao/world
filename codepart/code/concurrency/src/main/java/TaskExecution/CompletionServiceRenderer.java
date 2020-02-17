package TaskExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * CompletionService的作用相当于一组计算的句柄
 * @author zhengzebiao
 * @date 2020/1/6 19:53
 */
public class CompletionServiceRenderer {
    private final ExecutorService executor;

    public CompletionServiceRenderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        List<String> imageInfo = new ArrayList<>(5);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        for (final String info : imageInfo) {
            completionService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return Integer.parseInt(info);
                }
            });
            System.out.println("Image Info");
            try {
                for (int t = 0, n = imageInfo.size(); t < n; t++) {
                    Future<Integer> f = completionService.take();
                    Integer imageData = f.get();
                    System.out.println(imageData);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}