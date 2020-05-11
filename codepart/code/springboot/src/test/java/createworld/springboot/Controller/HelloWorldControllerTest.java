package createworld.springboot.Controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void sayHello() {
        assertEquals("Hello World!",new HelloWorldController().sayHello());
    }
}