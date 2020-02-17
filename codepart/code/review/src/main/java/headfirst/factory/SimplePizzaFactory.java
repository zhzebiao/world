package headfirst.factory;

import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;

/**
 * @author zhengzebiao
 * @date 2020/1/28 21:50
 */
public class SimplePizzaFactory {
    public Pizza createPizza(String type) {
        Pizza pizza = null;
        switch (type) {
            case "cheese":
                pizza = new Pizza();
                break;
            default:
                pizza = new Pizza();
                break;
        }
        return pizza;
    }

}