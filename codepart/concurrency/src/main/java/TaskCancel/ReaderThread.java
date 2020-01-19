package TaskCancel;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author zhengzebiao
 * @date 2020/1/15 14:32
 */
public class ReaderThread extends Thread {

    private final Socket socket;
    private final InputStream in;

    private static final int BUFSZ = 1024;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ingored) {
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        byte[] buf = new byte[BUFSZ];
        try {
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                }
                processBuffer(buf, count);
            }
        } catch (IOException e) {
            this.interrupt();
            /*允许线程退出*/
        }
    }

    private void processBuffer(byte[] buf, int count) {
        /* 对buf结果进行处理 */
    }
}