package logging;

import java.util.logging.Logger;

/**
 * @author zhengzebiao
 * @date 2019/12/19 21:59
 */
public class JDKLogging {
    public static void main(String[] args) {
        Logger logger = Logger.getGlobal();
        logger.info("start process...");
        logger.warning("memory is running out...");
        logger.fine("ingored...");
        logger.severe("process will be terminated...");
    }
}