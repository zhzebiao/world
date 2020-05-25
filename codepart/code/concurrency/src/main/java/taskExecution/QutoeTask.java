package taskExecution;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhengzebiao
 * @date 2020/1/6 20:48
 */
public class QutoeTask implements Callable {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public Object call() throws Exception {
        return null;
    }
    public void test(){
//        executor.invokeAll(task,time, TimeUnit.NANOSECONDS);
    }
}