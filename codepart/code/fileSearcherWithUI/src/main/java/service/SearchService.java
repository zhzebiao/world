package service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author zhzeb
 * @date 2020/4/14 23:18
 */
public class SearchService {

    private static Log LOG = LogFactory.get();

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


        solveSearchMonths(startMonth, endMonth);

        reportsMatchMonthsMap = solveReportsFolder(reportsFloder);

//        XlsUtil.writeToXlsx(xlsxFile, reportsMatchMonthsMap);
    }

    /**
     * 将查询时间解析成查询时间集合
     *
     * @param startMonthString 查询开始月份
     * @param  endMonthString 查询结束月份
     */
    private void solveSearchMonths(String startMonthString, String endMonthString) {
        LOG.info("开始解析搜索时间条件...");

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
            LOG.error("时间解析出错，请检查时间格式", e);
        }
        StringJoiner sj = new StringJoiner(",");
        for (String month : monthSet) {
            sj.add(month);
        }
        LOG.info("查询月份有:" + sj.toString());
    }

    /**
     * 递归搜索文件夹中满足条件的文件
     *
     * @param reportFolder 待搜索文件夹
     * @return 满足条件的文件目录
     */
    private Map<String, List<String>> solveReportsFolder(File reportFolder) {
        LOG.info("开始进行文件搜索,请耐心等待...");
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
        LOG.info("文件搜索完成！");
        return reportsMatchMonthsMap;
    }

}