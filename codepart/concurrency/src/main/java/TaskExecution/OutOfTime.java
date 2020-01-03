package TaskExecution;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 错误的Timer行为
 * @author zhengzebiao
 * @date 2020/1/3 11:41
 */
public class OutOfTime {
    public static void main(String[] args) throws Exception{
        Timer timer = new Timer();

        timer.schedule(new ThrowTask(),1);

        SECONDS.sleep(1);

        timer.schedule(new ThrowTask(),1);

        SECONDS.sleep(5);
    }

    static class ThrowTask extends TimerTask{
        @Override
        public void run(){
            throw new RuntimeException();
        }
    }
}