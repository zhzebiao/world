package createworld.springboot.controller;

import createworld.springboot.web.HelloController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void sayHello() {
        assertEquals("Hello World!",new HelloController().sayHello());
    }
}