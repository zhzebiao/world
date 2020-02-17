package world.tutorialspoint;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author zhengzebiao
 * @date 2020/2/10 17:09
 */
public class HelloWorld {
    private String message;

    public void getMessage() {
        System.out.println("Your Message:" + message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @PostConstruct
    public void init(){
        System.out.println("Bean is going through init.");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("Bean will destroy now.");
    }
}