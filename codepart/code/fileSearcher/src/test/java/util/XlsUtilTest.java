package util;

import cn.hutool.core.io.FileUtil;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class XlsUtilTest {

    @Test
    public void testSplit() {
        String name = "01-02-02-20180101.pdf";
        if(name.matches("[\\w|\\d|-]+-\\d{8}[\\w\\W]+")){
            int indexSplit = name.lastIndexOf('-');
            System.out.println(name.substring(0,indexSplit));
            System.out.println(name.substring(indexSplit+1,indexSplit+7));
        }
    }
}