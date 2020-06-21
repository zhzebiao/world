package createWorld.service;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * @author zhzeb
 * @date 2020/6/12 22:43
 */
public class IDReaderService {

    private final String filePath;

    private final Queue<File> fileQueue;


    public IDReaderService(String filePath) {
        this.filePath = filePath;
        this.fileQueue = new LinkedList<>();
    }

    public void service() {
        fileQueue.add(FileUtil.file(filePath));

        File topFile;
        while (fileQueue.size() != 0) {
            topFile = fileQueue.poll();

            if (topFile.isDirectory()) {
                fileQueue.addAll(Arrays.asList(Objects.requireNonNull(topFile.listFiles())));
            } else {
                String fileType = topFile.getName().substring(topFile.getName().lastIndexOf("."));
                if (isValid(fileType)) {
                    // TODO::

                }
            }
        }
    }

    public boolean isValid(String fileType) {
        String fileTypeWithoutCase = fileType.toLowerCase();
        return "pdf".equals(fileTypeWithoutCase);
    }
}