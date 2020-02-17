package world.tutorialspoint;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.StringJoiner;

/**
 * @author zhengzebiao
 * @date 2020/2/10 17:09
 */
public class MainApp {

    public static void main(String[] args) {
//        1.
//        AbstractApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//        HelloWorld obj = (HelloWorld) context.getBean("helloWorld");
//        obj.getMessage();
//        context.registerShutdownHook();
//        2.
//        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//        TextEditor textEditor = (TextEditor)context.getBean("textEditor3");
//        textEditor.spellcheck();

//        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//        JavaCollection jc = (JavaCollection) context.getBean("javaCollection");
//        jc.getAddressList();
//        jc.getAddressSet();
//        jc.getAddressMap();
//        jc.getAddressProp();

//        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//        TextEditor textEditor = (TextEditor) context.getBean("textEditor");
//        textEditor.spellcheck();

//        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
//        Student student = (Student)context.getBean("student");
//        System.out.println(student.getName());
//
//        TextEditor textEditor = (TextEditor) context.getBean("textEditor");
//        textEditor.spellcheck();

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        CustomEventPublisher cvp = (CustomEventPublisher) context.getBean("customEventPublisher");
        cvp.publish();
        cvp.publish();
    }


}