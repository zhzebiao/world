package basicConstructModule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 信号量初体验
 * 主要用来控制同时访问某个特定资源的操作数量，可以用来实现资源池
 * @author zhengzebiao
 * @date 2020/1/2 15:15
 */
public class TestSemaphore<T> {

    private final Set<T> set;
    private final Semaphore sem;

    public TestSemaphore(int count){
        this.set = Collections.synchronizedSet(new HashSet<T>());
        sem = new Semaphore(count);
    }

    public boolean add(T o) throws InterruptedException {
        sem.acquire();
        boolean wasAdded = false;
        try{
            wasAdded = set.add(o);
            return wasAdded;
        }finally {
            if(!wasAdded){
                sem.release();
            }
        }
    }

    public boolean remove(T o){
        boolean wasRemoved = set.remove(o);
        if(wasRemoved) {
            sem.release();
        }
        return wasRemoved;
    }
}