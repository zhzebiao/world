package world.tutorialspoint;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author zhengzebiao
 * @date 2020/2/13 16:46
 */
public class CustomEventHandler implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println(event.toString());
    }
}