package createWorld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author zhzeb
 * @date 2020/6/12 21:49
 */
public class CopyFileService {


    private final String filePath;
    private final String targetFoloder;
    private final String tempFloder;
    private int fileCount = 0;

    private final Queue<File> fileQueue;

    private static final Log LOG = LogFactory.get();

    public CopyFileService(String filePath, String targetFoloder) {
        this.filePath = filePath;
        this.targetFoloder = filePath + File.separator + targetFoloder;
        this.tempFloder = filePath + File.separator + "temp";
        this.fileQueue = new LinkedList<>();
    }

    public void service() {
        fileQueue.add(FileUtil.file(filePath));
        File targetFile = FileUtil.file(targetFoloder);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        File tempFile = FileUtil.file(tempFloder);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }

        File topFile;
        while (fileQueue.size() != 0) {
            topFile = fileQueue.poll();
            if (!topFile.equals(targetFile) && topFile.exists()) {
                if (topFile.isDirectory()) {
                    fileQueue.addAll(Arrays.asList(Objects.requireNonNull(topFile.listFiles())));
                } else {
                    fileOperate(topFile, targetFile, tempFile);
                }
            }
        }
        LOG.info("共计{}个文件进行了文件移动操作..", fileCount);
    }

    private void fileOperate(File srcFile, File targetFile, File tempFile){
        String fileType = srcFile.getName().substring(srcFile.getName().lastIndexOf(".") + 1).toLowerCase();
        switch (fileType) {
            case "pdf":
            case "xlsx":
            case "xls":
            case "zip":
            case "7z":
            case "rar":
                FileUtil.move(srcFile, targetFile, false);
                fileCount++;
                LOG.info("成功将{}移动到{}下..", srcFile.getName(), targetFile.getAbsolutePath());
                break;
            default:
                break;
//
//                ZipUtil.unzip(srcFile, tempFile);
//                LOG.info("正在解压{}", srcFile.getName());
//                break;
//
//                break;
//
//                List<File> extractFiles = Junrar.extract(srcFile, tempFile);
//                fileQueue.addAll(extractFiles);
//                break;
        }
    }
}