package world.tutorialspoint;

import org.springframework.stereotype.Component;

/**
 * @author zhengzebiao
 * @date 2020/2/12 23:29
 */
@Component
public class SpellChecker {

    public SpellChecker(){
        System.out.println("Inside SpellChecker constructor.");
    }

    public void checkSpelling(){
        System.out.println("Inside checkSpelling.");
    }
}