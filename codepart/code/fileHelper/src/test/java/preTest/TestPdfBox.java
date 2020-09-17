package preTest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestPdfBox {


    private static final String[] selectedText = new String[]{"姓名:", "HIS检查ID:", "Level Date"};

    @Test
    public void test1() throws Exception {
        PDDocument pdDocument = PDDocument.load(new File("E:\\肺功能样本\\HZDZ002-20181025.pdf"));
        if (pdDocument.isEncrypted()) {//加密
            System.out.println("pdDocument.isEncrypted");
            return;
        }
        PDPage page = pdDocument.getPage(0);//第一页
        PDFTextStripperByArea pdfTextStripper = new PDFTextStripperByArea();//区域文本剥离器
        pdfTextStripper.addRegion("region1", new Rectangle(0, 0, (int) page.getMediaBox().getWidth(), (int) page.getMediaBox().getHeight()));//区域大小
        pdfTextStripper.extractRegions(page);//设置页
        pdfTextStripper.setSortByPosition(true);//排序
        String text = pdfTextStripper.getTextForRegion("region1");//剥离文本

        String[] lines = text.split(System.lineSeparator());
        for (String line : lines) {
            String key = selectedKey(line);
            if (key != null) {
                String value = line.substring(key.length()).trim().split("\\s")[0];
                System.out.println(value);
            }
        }
        pdDocument.close();

    }

    public static String selectedKey(String line) {
        for (String selected : selectedText) {
            if (line.startsWith(selected)) {
                return selected;
            }
        }
        return null;
    }



    @Test
    public void testConvertDate(){
        String beforeDate = "10/25/18";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDate date = LocalDate.parse(beforeDate,dateTimeFormatter);
        System.out.println(date.toString());
    }

    @Test
    public void test2() throws IOException {
        //Loading an existing document
        File file = new File("E:\\肺功能样本\\HZDZ002-20181025.pdf");
        PDDocument document = PDDocument.load(file);
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();
        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);
        System.out.println(text);
        //Closing the document
        document.close();
    }
}
