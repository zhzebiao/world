package createworld.springboot.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhzeb
 * @date 2020/5/6 23:12
 */
@Component
@ConfigurationProperties(prefix = "home")
public class HomeProperties {

    private String province;

    private String city;
    private String desc;
}