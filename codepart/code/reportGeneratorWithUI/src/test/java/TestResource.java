import cn.hutool.setting.Setting;
import org.junit.Test;

import java.util.Map;

public class TestResource {

    @Test
    public void test1() {
        Setting setting = new Setting("application.setting");
        Map<String, String> entity = setting.getMap("entity");
        for (String key : entity.keySet()) {
            System.out.println(key + " :: " + entity.get(key));
        }
    }

    @Test
    public void test2(){
        String line = "123456";
        String str = "78";
        System.out.println(line.indexOf(str));
        System.out.println(line.substring(line.indexOf(str)+str.length()));
    }
}
