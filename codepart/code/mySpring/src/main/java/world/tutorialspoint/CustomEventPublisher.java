package world.tutorialspoint;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author zhengzebiao
 * @date 2020/2/13 16:41
 */
public class CustomEventPublisher implements ApplicationEventPublisherAware {


    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(){
        CustomEvent ce = new CustomEvent(this);
        applicationEventPublisher.publishEvent(ce);
    }
}