package headfirst.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

/**
 * @author zhengzebiao
 * @date 2020/1/30 15:45
 */
public class SimpleRemoteControl {
    Command slot;

    public SimpleRemoteControl() {
    }

    public SimpleRemoteControl(Command command) {
        slot = command;
    }

    public void buttonWasPressed() {
        slot.execute();
    }

    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < 10; i++) {
            stack.push(i + "");
        }
        Enumeration<String> elements = stack.elements();
        while (elements.hasMoreElements()) {
            System.out.println(elements.nextElement());

            ArrayList
        }
    }
}