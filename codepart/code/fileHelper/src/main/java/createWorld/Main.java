package createWorld;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import createWorld.service.CopyFileService;
import createWorld.service.IDReaderService;

import java.io.File;

/**
 * @author zhzeb
 * @date 2020/6/12 21:40
 */
public class Main {

    private static final String COPY_FILE = "copy_file";
    private static final String ID_READER = "id_reader";
    private static Setting applicationSetting = new Setting("application.setting");

    private static final Log LOG = LogFactory.get();

    public static void main(String[] args) {
        if (args.length > 0) {
            String runPart = args[0];

            if (COPY_FILE.equals(runPart)) {
//                String currentDir = "F:\\测试文件夹\\fileHelper";
                String currentDir = System.getProperty("user.dir");
                currentDir = currentDir.substring(0, currentDir.lastIndexOf(File.separator));
                LOG.info("待处理的文件夹目录为: {}", currentDir);
                String targetFolder = applicationSetting.get("targetFolder");
                LOG.info("开始进行文件移动...");
                CopyFileService service = new CopyFileService(currentDir, targetFolder);
                service.service();
                LOG.info("文件移动结束，程序正常退出..");
            } else if (ID_READER.equals(runPart)) {
                String currentDir = System.getProperty("user.dir");
                IDReaderService service = new IDReaderService(currentDir);
                service.service();
            }
        } else {
            LOG.info("请配置启动参数");
        }
    }


}