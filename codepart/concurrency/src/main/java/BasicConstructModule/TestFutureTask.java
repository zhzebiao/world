package BasicConstructModule;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * FutureTask初体验
 * 主要用来获取有返回值的线程的计算结果
 * @author zhengzebiao
 * @date 2020/1/2 14:48
 */
public class TestFutureTask {

    private final FutureTask<String> future = new FutureTask<String>(
            new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "a method ran";
                }
            }
    );

    private final Thread thread = new Thread(future);

    public void start() {
        thread.start();
    }

    public String get(){

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // Callable表示的任务可以抛出受检查的或未检查的异常,甚至是Error。
            // 所有异常都会被封装到一个ExecutionException中
            e.printStackTrace();
        }
        return null;
    }
}