import cn.hutool.core.io.FileUtil;
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
    public void getLastCelllNum(){
        File file = FileUtil.file("E:\\1-20200326黄佩瑜修课题三入组情况登记表.xlsx");
        try {
            BufferedInputStream bufferInput = new BufferedInputStream(new FileInputStream(file));
            Workbook workbook = new XSSFWorkbook(bufferInput);
            Sheet sheet = workbook.getSheetAt(0);
            Row row0 = sheet.getRow(0);
            int firstRowCellNum = row0.getLastCellNum();
            // 增加PDF，PDX，Excel三列
            row0.createCell(firstRowCellNum+0).setCellValue("PDF路径");
            row0.createCell(firstRowCellNum+1).setCellValue("PDX路径");
            row0.createCell(firstRowCellNum+2).setCellValue("Excel路径");

//            bufferInput.close();

            OutputStream out = new FileOutputStream(file);
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}