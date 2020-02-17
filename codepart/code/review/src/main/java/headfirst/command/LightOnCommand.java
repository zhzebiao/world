package headfirst.command;

/**
 * @author zhengzebiao
 * @date 2020/1/30 15:43
 */
public class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.up();
    }

    private class Light {
        void up() {
            System.out.println("Light is up!");
        }
    }
}