package logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhengzebiao
 * @date 2019/12/19 22:02
 */
public class CommonsLogging {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(CommonsLogging.class);
        log.info("start...");
        log.warn("end...");
    }
}