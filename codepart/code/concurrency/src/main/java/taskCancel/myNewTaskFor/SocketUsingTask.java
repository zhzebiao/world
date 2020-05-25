package taskCancel.myNewTaskFor;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * @author zhengzebiao
 * @date 2020/1/15 14:51
 */
public abstract class SocketUsingTask<T> implements CancellableTask<T> {

    private Socket socket;

    protected synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }

    public static void main(String[] args) {

    }
}