package world.tutorialspoint;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhengzebiao
 * @date 2020/2/13 16:39
 */
public class CustomEvent extends ApplicationEvent {

    public CustomEvent(Object source) {
        super(source);
    }

    @Override
    public String toString(){
        return "My Custom Event";
    }
}