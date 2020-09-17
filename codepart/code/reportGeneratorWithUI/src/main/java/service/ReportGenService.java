package service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import util.Constant;
import util.LogUtil;
import util.PDFUtil;
import util.XlsUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportGenService {

    private Log LOG = LogFactory.get();

    private static final String[] preHeaders = new String[]{"文件名", "文件名-编号", "文件名-日期"};

    private static List<String> headers;


    // 所有报告汇总文件夹
    private File reportsFloder;

    private Map<String, String> entities = Constant.applicationSetting.getMap("entity");

    public void setReportsFloder(File file) {
        this.reportsFloder = file;
    }

    public void execute() {

        LocalDate date = LocalDate.now();
//        if (date.isAfter(LocalDate.parse("20200820", DateTimeFormatter.ofPattern("yyyyMMdd")))) {
//            LogUtil.warn("使用期限超过有效期，请购买使用！！");
//        } else {
            new Thread(
                    () -> {
                        if (checkParam()) {
                            List<List<String>> resultCache = solveReportsFolder(reportsFloder);
                            XlsUtil.writeToXlsx(reportsFloder, resultCache);
                        } else {
                            String message = "参数检测异常，请重新填写参数！";
                            LogUtil.warn(message);
                            LOG.warn(message);
                        }
                    }
            ).start();
        }
//    }


    /**
     * 递归搜索文件夹中满足条件的文件
     *
     * @param reportFolder 待搜索文件夹
     * @return 满足条件的文件目录
     */
    private List<List<String>> solveReportsFolder(File reportFolder) {
        String message = "开始进行文件搜索,请耐心等待...";
        LogUtil.info(message);
        List<List<String>> resultCache = new ArrayList<>();
        // 增加表头信息
        headers = genHeaders(entities, preHeaders);
        resultCache.add(headers);


        // 遍历所有文件，读取文件中的相关信息，添加到content列表中
        Stack<File> files = new Stack<>();
        files.push(reportFolder);
        while (!files.isEmpty()) {
            File firstFile = files.pop();
            for (File file : firstFile.listFiles()) {
                if (file.isDirectory()) {
                    files.push(file);
                } else if (file.getName().toLowerCase().endsWith("pdf")) {

                    Map<String, String> rowMap = new HashMap<>();

                    List<String> rowCache = new ArrayList<>();
                    // 获取文件名称
                    String fileName = "", fileNumber = "", fileDate = "";
                    fileName = file.getName();
                    String[] fileNameSplit = fileName.split("-");
                    if (fileNameSplit.length >= 2) {
                        fileNumber = fileNameSplit[0];
                        // 去除文件名后缀信息
                        fileDate = fileNameSplit[1].split("\\.")[0];
                    }
                    rowMap.put("文件名", fileName.split("\\.")[0]);
                    rowMap.put("文件名-编号", fileNumber);
                    rowMap.put("文件名-日期", fileDate);
                    String[] lines = new String[0];
                    try {
                        lines = PDFUtil.getPDFContent(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogUtil.error("PDF文件: " + file.getName() + "解析异常,跳过解析！！异常信息为" + System.lineSeparator() + e.getMessage());
                        continue;
                    }
//                    // 依次通过遍历PDF内容获取每个Key的值
//                    for (String key : entities.keySet()) {
//                        for (String line : lines) {
//                            // 目前暂时处理startWith的逻辑
//                            if (line.startsWith(key)) {
//                                String value = line.substring(key.length() + 1).trim().split("\\s")[0];
//                                rowCache.add(convertValue(value, entities.get(key)));
//                                break;
//                            }
//                        }
//                    }

                    for (String line : lines) {
                        for (String key : entities.keySet()) {
                            // 目前暂时处理startWith的逻辑
                            //TODO:
//                            if (line.indexOf(key) != -1 && rowMap.containsKey(key)) {
//                                String innerFileKey = "文件内-" + key;
//                                String value;
//
//                            }
                            if (line.startsWith(key) && !rowMap.containsKey(key)) {
                                String innerFileKey = "文件内-" + key;
                                String value;
                                if (line.length() > key.length()) {
                                    LOG.info(fileName);
                                    value = line.substring(key.length() + 1).trim().split("\\s")[0];
                                } else {
                                    value = "";
                                    LOG.warn(fileName);
                                }
                                if (!rowMap.containsKey(innerFileKey)) {
                                    rowMap.put(innerFileKey, convertValue(value, entities.get(key)));
                                }
                            }
                        }
                        // 所有值都处理结束则跳过扫描其他行，直接跳过本文件
                        if (rowMap.size() >= entities.size() + preHeaders.length) {
                            break;
                        }
                    }
                    // 按照header顺序，组装rowcache
                    for (String column : headers) {
                        String value = rowMap.get(column);
                        rowCache.add(value == null ? "" : value);
                    }
                    // 将当前文件解析完的元素缓存到结果缓存中
                    resultCache.add(rowCache);
                    // 处理进度阶段性输出
                    if (resultCache.size() >= 100 && resultCache.size() % 100 == 1) {
                        LogUtil.info("已处理" + (resultCache.size() - 1) + "个文件,请耐心等待程序运行结束");
                    }
                }
            }
        }
        message = "所有PDF文件解析完成！即将生成Excel报告，请勿关闭窗口!";
        LogUtil.info(message);
        LOG.info(message);
        return resultCache;
    }

    /**
     * 解析值
     *
     * @param value 转换前的值
     * @param type  元素类型信息
     * @return
     */
    private String convertValue(String value, String type) {
        String[] typeSplit = type.split(Constant.ENTITY_CHAR);
        switch (typeSplit[0]) {
            case "String":
                return value;
            case "Date":
                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
                    LocalDate date = LocalDate.parse(value, dateTimeFormatter);
                    return date.toString();
                } catch (Exception e) {
                    return "";
                }
            default:
                return value;
        }
    }


    /**
     * 根据预定的表头信息和配置文件中的元素，生成最终的表头信息
     *
     * @param entities  配置文件中的元素信息
     * @param perHeader 预定表头信息
     * @return
     */
    private List<String> genHeaders(Map<String, String> entities, String... perHeader) {
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(perHeader));
        for (String key : entities.keySet()) {
            headers.add("文件内-" + key);
        }
        return headers;
    }

    /**
     * 运行参数检测
     *
     * @return
     */
    private boolean checkParam() {
        String message;
        if (!reportsFloder.exists()) {
            message = "文件夹不存在: " + reportsFloder.getAbsolutePath();
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        if (!reportsFloder.isDirectory()) {
            message = "请选择正确的文件夹路径: " + reportsFloder.getAbsolutePath();
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        return true;
    }
}
