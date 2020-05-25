package taskCancel.myNewTaskFor;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * @author zhengzebiao
 * @date 2020/1/15 14:45
 */
public interface CancellableTask<T> extends Callable<T> {
    void cancel();
    RunnableFuture<T> newTask();
}
