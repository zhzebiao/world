package world.tutorialspoint;

import org.aspectj.lang.annotation.*;

/**
 * @author zhengzebiao
 * @date 2020/2/13 17:53
 */
@Aspect
public class Logging {


    @Pointcut("execution(* world.tutorialspoint.*.*(..))")
    private void selectAll(){}
    /**
     * This is the method which I would like to execute
     * before a selected method execution.
     */
    @Before("selectAll()")
    public void beforeAdvice(){
        System.out.println("Going to setup student profile.");
    }
    /**
     * This is the method which I would like to execute
     * after a selected method execution.
     */
    @After("selectAll()")
    public void afterAdvice(){
        System.out.println("Student profile has been setup.");
    }
    /**
     * This is the method which I would like to execute
     * when any method returns.
     */
    @AfterReturning(pointcut = "selectAll()",returning = "retVal")
    public void afterReturningAdvice(Object retVal){
        System.out.println("Returning:" + retVal.toString() );
    }
    /**
     * This is the method which I would like to execute
     * if there is an exception raised.
     */
    @AfterThrowing(pointcut = "selectAll()",throwing = "ex")
    public void AfterThrowingAdvice(IllegalArgumentException ex){
        System.out.println("There has been an exception: " + ex.toString());
    }
}