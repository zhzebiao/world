package util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @author zhzeb
 * @date 2020/4/8 21:10
 */
public class XlsUtil {

    private static final Log LOG = LogFactory.get();

    private static final String id = "ID号";

    private static final String name = "姓名";

    private static final String pdf = "PDF路径";

    private static final String pdx = "PDX路径";

    private static final String excel = "Excel路径";


    public static void writeToXlsx(File targetFolder, List<List<String>> resultCache) {
        LogUtil.info("创建结果文件夹");
        LOG.info("创建结果文件夹");

        String targetFolderPath = targetFolder.getAbsolutePath() + File.separator + "肺功能PDF报告生成器运行结果";
        String targetFilePath = targetFolderPath + File.separator + "肺功能信息.xls";
        FileUtil.del(targetFolderPath);
        FileUtil.mkdir(targetFolderPath);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter(targetFilePath);

        //一次性写出内容，强制输出标题
        writer.write(resultCache, true);
        //关闭writer，释放内存
        writer.close();
        String message = "Excel档案数据生成结束！文件路径为：" + targetFilePath;
        LogUtil.info(message);
        LOG.info(message);
    }


    public static void writeToXlsx(File srcfile, Map<String, List<String>> reportsMatchMonthsMap) {

        LogUtil.info("创建结果文件夹");
        LOG.info("创建结果文件夹");

        String targetFolderPath = srcfile.getParent() + "\\FileSearcher运行结果";
        FileUtil.del(targetFolderPath);
        FileUtil.mkdir(targetFolderPath);

        LogUtil.info("复制结果Excel文件到结果文件夹中");
        LOG.info("复制结果Excel文件到结果文件夹中");
        File targetExcelFile = FileUtil.touch(targetFolderPath, srcfile.getName());
        FileUtil.copy(srcfile, targetExcelFile, true);

        // 文件输出流对象
        OutputStream out;
        try {
            BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(targetExcelFile));

            Workbook workbook;
            try {
                workbook = new XSSFWorkbook(bufferInput);
            } catch (Exception e) {
                workbook = new HSSFWorkbook(bufferInput);
            }

            // 遍历Excel表格内的所有sheet
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                LogUtil.info(String.format("开始处理第%d个sheet", index + 1));
                LOG.info(String.format("开始处理第%d个sheet", index + 1));
                Sheet sheet = workbook.getSheetAt(index);

                Row row0 = sheet.getRow(0);
                // 判断是否是空白sheet，如果是，则跳过不处理
                if (row0 == null) {
                    continue;
                }
                int firstRowCellNum = row0.getLastCellNum();

                // 预生成之后需要使用到的cell坐标
//                int pdfIndex = firstRowCellNum, pdxIndex = firstRowCellNum + 1, excelIndex = firstRowCellNum + 2;
                int idIndex = getCellNum(id, row0);
                int nameIndex = getCellNum(name, row0);
                int searchIndex;
                searchIndex = getCellNum(pdf, row0);
                int pdfIndex = (searchIndex == -1 ? firstRowCellNum++ : searchIndex);
                searchIndex = getCellNum(pdx, row0);
                int pdxIndex = (searchIndex == -1 ? firstRowCellNum++ : searchIndex);
                searchIndex = getCellNum(excel, row0);
                int excelIndex = (searchIndex == -1 ? firstRowCellNum : searchIndex);

                // 增加PDF，PDX，Excel三列
                setCellValue(row0, pdfIndex, "PDF路径");
                setCellValue(row0, pdxIndex, "PDX路径");
                setCellValue(row0, excelIndex, "Excel路径");

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row currentRow = sheet.getRow(i);
                    // 将文件复制到目标文件夹中
                    Map<String, String> pathMap = copyFileToTargetFolder((idIndex == -1 ? null : currentRow.getCell(idIndex)),
                            (nameIndex == -1 ? null : currentRow.getCell(nameIndex)), reportsMatchMonthsMap, targetFolderPath);
                    if (pathMap != null) {
                        setCellValue(currentRow, pdfIndex, pathMap.get(pdf).length() > 0 ? pathMap.get(pdf) : "缺失");
                        setCellValue(currentRow, pdxIndex, pathMap.get(pdx).length() > 0 ? pathMap.get(pdx) : "缺失");
                        setCellValue(currentRow, excelIndex, pathMap.get(excel).length() > 0 ? pathMap.get(excel) : "缺失");
                    }
                }
            }
            out = new FileOutputStream(targetExcelFile);
            workbook.write(out);
            LogUtil.info("Excel档案数据生成结束！");
            LOG.info("Excel档案数据生成结束！");
        } catch (IOException e) {
            LogUtil.error("Excel文件生成时发生错误" + e.toString());
            LOG.error("Excel文件生成时发生错误" + e.toString());
        }

    }

    /**
     * 查找字符串在行内位置，默认返回-1
     *
     * @param value 待查找字符串
     * @param row   Excel行
     * @return 字符串所在行的位置
     */
    private static int getCellNum(String value, Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (value.trim().equals(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取Excel行的指定位置的Cell对象。若Cell对象为空，则创建一个Cell
     *
     * @param row   Excel行
     * @param index Cell位置
     * @return Cell对象
     */
    private static Cell getCell(Row row, int index) {
        Cell targetCell = row.getCell(index);
        if (targetCell == null) {
            targetCell = row.createCell(index);
        }
        return targetCell;
    }

    /**
     * 设置Excel行的指定位置的Cell值。
     *
     * @param row   Excel行
     * @param index Cell位置
     * @param value 字段值
     */
    private static void setCellValue(Row row, int index, String value) {
        Cell targetCell = row.getCell(index);
        if (targetCell == null) {
            targetCell = row.createCell(index);
        }
        targetCell.setCellValue(value);
    }


    private static Map<String, String> copyFileToTargetFolder(Cell idCell, Cell nameCell, Map<String, List<String>> reportsMatchMonthsMap, String targetFolderPath) {
        if (idCell == null) {
            return null;
        }
        String subFolderName = null;
        CellType type1 = idCell.getCellTypeEnum();
        switch (type1) {
            case STRING:
                subFolderName = idCell.getStringCellValue().trim();
                break;
            case NUMERIC:
                subFolderName = (idCell.getNumericCellValue() + "");
        }
        if (subFolderName == null) return null;
        String folder = targetFolderPath + "\\" + subFolderName;
        Map<String, String> pathMap = new HashMap<>(3);
        StringJoiner pdfPaths = new StringJoiner(",");
        StringJoiner pdxPaths = new StringJoiner(",");
        StringJoiner excelPaths = new StringJoiner(",");
        Set pdfSet = new HashSet();
        Set pdxSet = new HashSet();
        Set excelSet = new HashSet();

        List<Cell> cellsForMatch = new ArrayList<>();
        cellsForMatch.add(idCell);
        cellsForMatch.add(nameCell);


        for (Cell currentCell : cellsForMatch) {
            if (currentCell != null) {
                String cellValue = null;
                CellType type = currentCell.getCellTypeEnum();
                switch (type) {
                    case STRING:
                        cellValue = currentCell.getStringCellValue().trim().toLowerCase();
                        break;
                    case NUMERIC:
                        cellValue = (currentCell.getNumericCellValue() + "");
                }
                if (cellValue == null) continue;

                List<String> list = reportsMatchMonthsMap.get(cellValue);
                // 对特殊ID号进行处理
                if (cellValue.matches("\\d+-*\\d+")) {
                    list = (list != null ? list : new ArrayList<>());
                    for (String key : reportsMatchMonthsMap.keySet()) {
                        if (key.contains(cellValue)) {
                            list.addAll(reportsMatchMonthsMap.get(key));
                        }
                    }
                }
                if (list != null) {
                    File tempFile;
                    for (String path : list) {
                        String lowerCaseFileName = FileUtil.file(path).getName().toLowerCase();
                        if (lowerCaseFileName.endsWith("pdf")) {
                            if (!pdfSet.contains(lowerCaseFileName)) {
                                pdfPaths.add(lowerCaseFileName);
                                pdfSet.add(lowerCaseFileName);
                            }
                        } else if (lowerCaseFileName.endsWith("pdx")) {
                            if (!pdxSet.contains(lowerCaseFileName)) {
                                pdxPaths.add(lowerCaseFileName);
                                pdxSet.add(lowerCaseFileName);
                            }
                        } else if (lowerCaseFileName.endsWith("xls") || lowerCaseFileName.endsWith("xlsx")) {
                            if (!excelSet.contains(lowerCaseFileName)) {
                                excelPaths.add(lowerCaseFileName);
                                excelSet.add(lowerCaseFileName);
                            }
                        }
                        tempFile = FileUtil.file(path);
                        FileUtil.copy(path, folder + "\\" + tempFile.getName(), false);
                    }
                }
            }
        }
        pathMap.put(pdf, pdfPaths.toString());
        pathMap.put(pdx, pdxPaths.toString());
        pathMap.put(excel, excelPaths.toString());
        return pathMap;
    }

}