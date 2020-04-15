package util;


import javafx.scene.control.TextArea;

/**
 * @author zhzeb
 * @date 2020/4/15 19:22
 */
public class LogUtil {

    private static TextArea logTextArea;

    public static void setLogTextArea(TextArea logTextArea1) {
        logTextArea = logTextArea1;
    }


    private static void writeLog(String log) {
        logTextArea.appendText(log + "\n");
    }

    public static void info(String log) {
        writeLog("INFO: " + log);
    }

    public static void warn(String log) {
        writeLog("WARN: " + log);
    }

    public static void error(String log) {
        writeLog("ERROR: " + log);
    }
}