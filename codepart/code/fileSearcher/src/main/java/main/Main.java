package main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import util.XlsUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author zhzeb
 * @date 2020/4/7 19:48
 */
public class Main {

    private static Log LOG = LogFactory.get();

    // 总配置文件地址
    private static Setting applicationSetting;

    // 查询时间范围
    private static Set<String> monthSet = new HashSet<>();


    // 待查询xlsx文件
    private static File xlsxFile;

    // 所有报告汇总文件夹
    private static File reportsFloder;

    private static Map<String, List<String>> reportsMatchMonthsMap;

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        if(date.isAfter(LocalDate.parse("20200416",DateTimeFormatter.ofPattern("yyyyMMdd")))){
            LOG.warn("使用期限超过有效期，请购买使用！！");
            System.exit(1);
        }
        // 初始化配置内容
        initParams();

        reportsMatchMonthsMap = solveReportsFolder(reportsFloder);

        XlsUtil.writeToXlsx(xlsxFile, reportsMatchMonthsMap);

    }


    /**
     * 解析配置项，并检测配置项正确性
     */
    private static void initParams() {

        LOG.info("开始记载配置文件内容");
        applicationSetting = new Setting("application.setting");
        // 待查询xlsx文件
        String xlsxFilePath = applicationSetting.get("xlsxFilePath").trim();
        // 所有报告汇总文件夹
        String reportsPath = applicationSetting.get("reportsPath").trim();
        // 查询日期
        String searchMonths = applicationSetting.get("searchMonths").trim();

        if (searchMonths.length() > 0) {
            // 解析查询时间
            solveSearchMonths(searchMonths);
        } else {
            LOG.error("请检查查询时间是否正确！");
            System.exit(1);
        }

        try {
            xlsxFile = FileUtil.file(xlsxFilePath);
            if(!xlsxFile.exists()){
                LOG.error("Excel文件不存在");
                System.exit(1);
            }
        } catch (Exception e) {
            LOG.error("请检查Excel文件路径是否正确！");
            System.exit(1);
        }

        try {
            reportsFloder = FileUtil.file(reportsPath);
        } catch (Exception e) {
            LOG.error("请检查报告汇总文件夹路径是否正确！");
            System.exit(1);
        }

    }

    /**
     * 将查询时间解析成查询时间集合
     *
     * @param searchMonths 查询时间值
     */
    private static void solveSearchMonths(String searchMonths) {
        LOG.info("开始解析搜索时间条件...");
        String[] dates = searchMonths.split("\\s+");
        try {
            LocalDate startMonth = LocalDate.parse(dates[0] + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
            monthSet.add(startMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));
            // 如果存在多个日期，则表示查询时间范围
            if (dates.length > 1) {
                LocalDate endMonth = LocalDate.parse(dates[1] + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
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
    private static Map<String, List<String>> solveReportsFolder(File reportFolder) {
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
                        String date = fileName.substring(indexSplit+1,indexSplit+7);
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

