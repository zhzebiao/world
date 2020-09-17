package createWorld.hadoop;

import com.google.inject.internal.cglib.core.$Local;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Test;


import java.io.*;
import java.net.URI;


import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.server.ExportException;


public class HdfsFileAccess {

    /**
     * 使用url方式访问HDFS数据
     */
    @Test
    public void urlAccess() throws Exception {
        // 注册 hdfs 的url
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        // 获取文件输入流
        InputStream inputStream = new URL("hdfs://node01:8020/wordcount/wordcount.txt").openStream();
        // 获取文件输出流
        FileOutputStream outputStream = new FileOutputStream(new File("d:/hello.txt"));
        // 实现文件的拷贝
        IOUtils.copy(inputStream, outputStream);

        // 关闭流
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
    }

    @Test
    public void listMyFiles() throws IOException, URISyntaxException, InterruptedException {
        Configuration configuration = new Configuration();
//        configuration.set("fs.defaultFS", "hdfs://node01:8020/");
        //设置通过域名访问datanode
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020/"),configuration,"root");

        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fileSystem.listFiles(new Path("/"), true);
        while (locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus next = locatedFileStatusRemoteIterator.next();
            System.out.println(next.getPath().toString());
        }
        fileSystem.close();

    }


    @Test
    public void mkdirs() throws Exception {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://192.168.174.101:8020/"), new Configuration(),"root");
        boolean mkdirs = fileSystem.mkdirs(new Path("hdfs://node01:8020/hello"));
        fileSystem.close();
    }


    @Test
    public void getFileSystem1() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://node01:8020/");

        FileSystem fileSystem = FileSystem.newInstance(configuration);
//        FSDataInputStream in = null;
//        in = fileSystem.open(new Path("/a.txt"));
//        //InputStreamReader istr = new InputStreamReader(in);
//        //BufferedReader br = new BufferedReader(istr);
//        long id;
//        while (id=in.readLong()>0L){
//            docID.add(id);

        System.out.println(fileSystem.toString());
    }


//    public static void main(String[] args) throws IOException {
//        HdfsFileAccess access = new HdfsFileAccess();
//        access.listMyFiles();
//    }

    @Test
    public void getConfig() throws Exception {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"), new Configuration(), "hadoop");
        fileSystem.copyToLocalFile(new Path("/config/core-site.xml"), new Path("file:///d:/core-site.xml"));
        fileSystem.close();
    }
}
