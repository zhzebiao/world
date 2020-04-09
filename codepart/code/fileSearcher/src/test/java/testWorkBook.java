import cn.hutool.core.io.FileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

/**
 * @author zhzeb
 * @date 2020/4/8 20:21
 */
public class testWorkBook {

    @Test
    public void getLastCelllNum() {
        File file = FileUtil.file("E:\\2连平-20200326-黄佩瑜修16连平中心·金康速力入组登记表(1)(1).xlsx");
        try {
            BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
            Workbook workbook = new XSSFWorkbook(bufferInput);
            Sheet sheet = workbook.getSheetAt(0);
            Row row0 = sheet.getRow(0);
            int firstRowCellNum = row0.getLastCellNum();
            // 增加PDF，PDX，Excel三列
            row0.createCell(firstRowCellNum + 0).setCellValue("PDF路径");
            row0.createCell(firstRowCellNum + 1).setCellValue("PDX路径");
            row0.createCell(firstRowCellNum + 2).setCellValue("Excel路径");

//            bufferInput.close();

            OutputStream out = new FileOutputStream(file);
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void printCell() {
        File file = FileUtil.file("E:\\2连平-20200326-黄佩瑜修16连平中心·金康速力入组登记表(1)(1).xlsx");
        try {
            BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
            Workbook workbook = new XSSFWorkbook(bufferInput);
            Sheet sheet = workbook.getSheetAt(0);
            Row row0 = sheet.getRow(197);
            for (int i = 0; i < row0.getLastCellNum(); i++) {
                Cell cell = row0.getCell(i);
                if(cell!=null) {
                    System.out.println(i + ":" +cell.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}