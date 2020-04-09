package main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author zhzeb
 * @date 2020/4/5 17:40
 */
public class Main {

    private static final String xlsxFloderString = "D:\\xlsx";
    private static final String patientFloderString = "D:\\\\patientFloder";
    private static final String outPath = "D\\out";

    public static void main(String[] args) throws IOException {
        File outPathFile = FileUtil.file(outPath);
        if (!outPathFile.exists()) {
            outPathFile.mkdir();
        }
        File xlsxFloder = FileUtil.file(xlsxFloderString);
        Stack<File> files = new Stack<>();

        // 读取xlsx文件
        List<Map<String, Object>> records = new ArrayList<>();
        if (xlsxFloder.exists() && xlsxFloder.isDirectory()) {
            files.push(xlsxFloder);
            while (!files.isEmpty()) {
                File firstFile = files.pop();
                if (firstFile.isDirectory()) {
                    for (File file : firstFile.listFiles()) {
                        files.push(file);
                    }
                } else {
                    ExcelReader reader = ExcelUtil.getReader(firstFile);
                    records.addAll(reader.readAll());
                }
            }
        } else {
            System.exit(1);
        }
        //整理病人文件
        File patientFloder = FileUtil.file(patientFloderString);
        files.push(patientFloder);
        while (!files.isEmpty()) {
            File firstFile = files.pop();
            if (firstFile.isDirectory()) {
                for (File file : firstFile.listFiles()) {
                    files.push(file);
                }
            } else {
//                patientFileList.add(firstFile);
                String patientName = firstFile.getName().split("_")[0];
                for (Map<String, Object> record : records) {
                    if (patientName.equals(record.get("姓名"))) {
//                        FileUtil.copy(firstFile.getAbsolutePath(), outPathFile + "\\\\" + record.get("药物号") + "+" + patientName + "\\\\" + firstFile.getName(), false);
                        pdf2png(firstFile.getAbsolutePath(), outPathFile + "\\\\" + record.get("药物号") + "+" + patientName + "\\\\", "png");
                    }
                }
            }
        }

        //

    }


    public static void pdf2png(String filepath, String imagepath, String type) {

        File destFloder = FileUtil.file(imagepath);
        if (!destFloder.exists()) {
            destFloder.mkdirs();
        }
        // 将pdf装图片 并且自定义图片得格式大小
        File file = FileUtil.file(filepath);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, type, new File(imagepath + new File(filepath).getName().split("_")[0] + "_" + i + "." + type));
            }
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        System.out.println("转换完成");
    }
//    public static void tranfer(String filepath, String imagepath, float zoom) throws PDFException, PDFSecurityException, IOException {
//        Document document = null;
//        float rotation = 0f;
//        document = new Document();
//        document.setFile(filepath);
//        int maxPages = document.getPageTree().getNumberOfPages();
//        String FILETYPE_JPG = "jpg";
//        File destFloder = FileUtil.file(imagepath);
//        if(!destFloder.exists()){
//            destFloder.mkdirs();
//        }
//
//        for (int i = 0; i < maxPages; i++) {
//            BufferedImage img = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, zoom);
//            Iterator iter = ImageIO.getImageWritersBySuffix(FILETYPE_JPG);
//            ImageWriter writer = (ImageWriter) iter.next();
//            File outFile = new File(imagepath + new File(filepath).getName().split("_")[0] + "_" + i + "." + FILETYPE_JPG);
//            FileOutputStream out = new FileOutputStream(outFile);
//            ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
//            writer.setOutput(outImage);
//            writer.write(new IIOImage(img, null, null));
//        }
//        System.out.println("转换完成");
//    }
    }