//package createWorld.utils;
//
//import com.github.junrar.Junrar;
//import net.sf.sevenzipjbinding.*;
//import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
//import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
//import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
//
//import java.io.*;
//import java.util.Arrays;
//
///**
// * @author zhzeb
// * @date 2020/6/13 8:07
// */
//public class UnpackUtil {
//
//
//    //解压7z
//    public static void un7ZipFile(String filepath, final String targetFilePath) {
//        final File file = new File(targetFilePath);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        RandomAccessFile randomAccessFile = null;
//        IInArchive inArchive = null;
//
//        try {
//            randomAccessFile = new RandomAccessFile(filepath, "r");
//            inArchive = SevenZip.openInArchive(null,
//                    new RandomAccessFileInStream(randomAccessFile));
//
//            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
//
//            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
//                final int[] hash = new int[]{0};
//                if (!item.isFolder()) {
//                    ExtractOperationResult result;
//
//                    final long[] sizeArray = new long[1];
//                    result = item.extractSlow(new ISequentialOutStream() {
//                        public int write(byte[] data) throws SevenZipException {
//
//                            FileOutputStream fos = null;
//                            try {
//                                File tarFile = new File(file + File.separator + item.getPath());
//                                if (!tarFile.getParentFile().exists()) {
//                                    tarFile.getParentFile().mkdirs();
//                                }
//                                tarFile.createNewFile();
//                                fos = new FileOutputStream(tarFile.getAbsolutePath());
//                                fos.write(data);
//                                fos.close();
//
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//
//                                e.printStackTrace();
//                            }
//
//                            hash[0] ^= Arrays.hashCode(data);
//                            sizeArray[0] += data.length;
//                            return data.length;
//                        }
//                    });
//                    if (result == ExtractOperationResult.OK) {
//                        // System.out.println(String.format("%9X | %10s | %s", //
//                        //  hash[0], sizeArray[0], item.getPath()));
//                    } else {
//                        // System.err.println("Error extracting item: " + result);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(1);
//        } finally {
//            if (inArchive != null) {
//                try {
//                    inArchive.close();
//                } catch (SevenZipException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (randomAccessFile != null) {
//                try {
//                    randomAccessFile.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static void unRar(File sourceRar, File destDir) throws Exception {
//        Junrar.extract(sourceRar, destDir);
//    }
//}