package jvm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * @author zhengzebiao
 * @date 2020/1/21 10:51
 */
public class MyClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) {
        try {
            String path = "D:\\";
            FileInputStream in = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            in.close();
            byte[] classBytes = baos.toByteArray();
            return defineClass(classBytes, 0, classBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}