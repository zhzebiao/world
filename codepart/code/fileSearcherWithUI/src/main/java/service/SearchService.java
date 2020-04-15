package service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import util.LogUtil;
import util.XlsUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author zhzeb
 * @date 2020/4/14 23:18
 */
public class SearchService {

    private Log LOG = LogFactory.get();

    // 查询时间范围
    private Set<String> monthSet = new HashSet<>();

    // 查询开始时间
    private String startMonth;

    // 查询结束时间
    private String endMonth;

    // 待查询xlsx文件
    private File xlsxFile;

    // 所有报告汇总文件夹
    private File reportsFloder;

    private Map<String, List<String>> reportsMatchMonthsMap;

    public void setXlsxFile(File file) {
        this.xlsxFile = file;
    }

    public void setReportsFloder(File file) {
        this.reportsFloder = file;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public void execute() {
        if (LocalDate.now().isAfter(LocalDate.parse("20200419", DateTimeFormatter.ofPattern("yyyyMMdd")))) {
            String message = "使用期限超过有效期，请购买使用！！";
            LogUtil.warn(message);
            LOG.warn(message);
        }

        new Thread(
                () -> {
                    if (checkParam()) {
                        solveSearchMonths(startMonth, endMonth);
                        reportsMatchMonthsMap = solveReportsFolder(reportsFloder);
                        XlsUtil.writeToXlsx(xlsxFile, reportsMatchMonthsMap);
                    } else {
                        String message = "参数检测异常，请重新填写参数！";
                        LogUtil.warn(message);
                        LOG.warn(message);
                    }
                }
        ).start();

    }

    /**
     * 将查询时间解析成查询时间集合
     *
     * @param startMonthString 查询开始月份
     * @param endMonthString   查询结束月份
     */
    private void solveSearchMonths(String startMonthString, String endMonthString) {
        String message = "开始解析搜索时间条件...";
        LogUtil.info(message);
        LOG.info(message);


        try {
            LocalDate startMonth = LocalDate.parse(startMonthString + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
            monthSet.add(startMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));
            // 如果存在多个日期，则表示查询时间范围
            if (endMonthString.length() > 1) {
                LocalDate endMonth = LocalDate.parse(endMonthString + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
                while (startMonth.isBefore(endMonth)) {
                    startMonth = startMonth.plusMonths(1);
                    monthSet.add(startMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));
                }
            }
        } catch (Exception e) {
            message = "时间解析出错，请检查时间格式。" + e.toString();
            LogUtil.error(message);
            LOG.error(message);

        }
        StringJoiner sj = new StringJoiner(",");
        for (String month : monthSet) {
            sj.add(month);
        }
        message = "查询月份有:" + sj.toString();
        LogUtil.info(message);
        LOG.info(message);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {

        }
    }

    /**
     * 递归搜索文件夹中满足条件的文件
     *
     * @param reportFolder 待搜索文件夹
     * @return 满足条件的文件目录
     */
    private Map<String, List<String>> solveReportsFolder(File reportFolder) {
        String message = "开始进行文件搜索,请耐心等待...";
        LogUtil.info(message);
        LOG.info(message);

        Map<String, List<String>> reportsMatchMonthsMap = new HashMap<>();
        Stack<File> files = new Stack<>();
        files.push(reportFolder);
        while (!files.isEmpty()) {
            File firstFile = files.pop();
            for (File file : firstFile.listFiles()) {
                if (file.isDirectory()) {
                    files.push(file);

                } else {
                    String fileName = file.getName();
                    if (fileName.matches("[\\w|\\d|-]+-\\d{8}[\\w\\W]+")) {
                        int indexSplit = fileName.lastIndexOf('-');
                        String id = fileName.substring(0, indexSplit).toLowerCase();
                        String date = fileName.substring(indexSplit + 1, indexSplit + 7);
                        if (monthSet.contains(date)) {
                            if (!reportsMatchMonthsMap.containsKey(id)) {
                                List<String> filePathList = new ArrayList<>();
                                reportsMatchMonthsMap.put(id, filePathList);
                            }
                            reportsMatchMonthsMap.get(id).add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        message = "文件搜索完成！";
        LogUtil.info(message);
        LOG.info(message);
        return reportsMatchMonthsMap;
    }

    /**
     * 运行参数检测
     *
     * @return
     */
    private boolean checkParam() {
        String message;
        if (!xlsxFile.exists()) {
            message = "文件不存在: " + xlsxFile.getAbsolutePath();
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        if (!xlsxFile.getName().endsWith("xlsx") && !xlsxFile.getName().endsWith("xls")) {
            message = "所选文件不是Excel文件: " + xlsxFile.getAbsolutePath();
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
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
        if (startMonth == null || !startMonth.matches("\\d{6}")) {
            message = "查询开始时间格式错误，正确格式应为: yyyyMM，例如:202004";
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        if (endMonth == null || !endMonth.matches("\\d{6}")) {
            message = "查询结束时间格式错误，正确格式应为: yyyyMM，例如:202004";
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        if (startMonth.compareTo(endMonth) > 0) {
            message = "查询开始时间应该比查询结束时间小";
            LogUtil.error(message);
            LOG.error(message);
            return false;
        }
        return true;
    }

}