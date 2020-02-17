package TaskExecution;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author zhengzebiao
 * @date 2020/1/2 23:08
 */
public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;
    private static final Executor exec =
            Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while(true){
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    System.out.println("running task!");
                }
            };
            // 提交线程
            exec.execute(task);
        }
    }

}