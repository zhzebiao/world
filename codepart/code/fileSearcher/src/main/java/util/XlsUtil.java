package util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author zhzeb
 * @date 2020/4/8 21:10
 */
public class XlsUtil {

    private static Log LOG = LogFactory.get();

    private static String id = "ID号";

    private static String name = "姓名";


    public static void writeToXlsx(File file, Map<String, List<String>> reportsMatchMonthsMap) {

        LOG.info("开始补充Excel档案数据...");
        // 先备份文件
        File backupFile = backupXlsx(file);

        // 文件输出流对象
        OutputStream out = null;
        try {
            BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
            Workbook workbook = new XSSFWorkbook(bufferInput);
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                Sheet sheet = workbook.getSheetAt(index);
                Row row0 = sheet.getRow(0);
                int firstRowCellNum = row0.getLastCellNum();

                // 预生成之后需要使用到的cell坐标
                int pdfIndex = firstRowCellNum, pdxIndex = firstRowCellNum + 1, excelIndex = firstRowCellNum + 2;
                int idIndex = getCellNum(id, row0);
                int nameIndex = getCellNum(name, row0);

                // 增加PDF，PDX，Excel三列
                row0.createCell(pdfIndex).setCellValue("PDF路径");
                row0.createCell(pdxIndex).setCellValue("PDX路径");
                row0.createCell(excelIndex).setCellValue("Excel路径");

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                    boolean isInvalidRow = false;
                    StringJoiner pdfPaths = new StringJoiner(",");
                    StringJoiner pdxPaths = new StringJoiner(",");
                    StringJoiner xlsPaths = new StringJoiner(",");
                    Row currentRow = sheet.getRow(i);
                    List<Cell> cellsForMatch = new ArrayList<>();
                    cellsForMatch.add(currentRow.getCell(idIndex));
                    cellsForMatch.add(currentRow.getCell(nameIndex));
                    for (Cell currentCell : cellsForMatch) {
                        if (currentCell != null) {
                            isInvalidRow = true;
                            List<String> list = reportsMatchMonthsMap.get(currentCell.getStringCellValue().trim().toLowerCase());
                            if (list != null) {
                                for (String path : list) {
                                    String lowerCasePath = path.toLowerCase();
                                    if (lowerCasePath.endsWith("pdf")) {
                                        pdfPaths.add(path);
                                    } else if (lowerCasePath.endsWith("pdx")) {
                                        pdxPaths.add(path);
                                    } else if (lowerCasePath.endsWith("xls") || lowerCasePath.endsWith("xlsx")) {
                                        xlsPaths.add(path);
                                    }
                                }
                            }
                        }
                    }
                    if (isInvalidRow) {
                        currentRow.createCell(pdfIndex).setCellValue(pdfPaths.length() > 0 ? pdfPaths.toString() : "缺失");
                        currentRow.createCell(pdxIndex).setCellValue(pdxPaths.length() > 0 ? pdxPaths.toString() : "缺失");
                        currentRow.createCell(excelIndex).setCellValue(xlsPaths.length() > 0 ? xlsPaths.toString() : "缺失");
                    }
                }
            }
            out = new FileOutputStream(file);
            workbook.write(out);
            LOG.info("Excel档案数据生成结束！");
        } catch (IOException e) {
            LOG.error("Excel文件生成时发生错误", e);
        }

    }


    private static int getCellNum(String value, Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (value.trim().equals(row.getCell(i).getStringCellValue().trim())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 备份文件，程序出错后恢复源文件
     *
     * @param file 源文件
     * @return 备份文件
     */
    private static File backupXlsx(File file) {
        if (file.exists()) {
            String srcPath = file.getAbsolutePath();
            String destPath = srcPath.substring(0, srcPath.indexOf(".") + 1) + "tmp";
            File backupFile = FileUtil.copy(srcPath, destPath, true);
            LOG.info("文件备份成功");
            return backupFile;
        }
        return null;
    }
}