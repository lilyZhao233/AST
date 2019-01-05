package util;

import java.io.*;

public class WriteToFileUtil {

    /**
     * 将字符串写入文件
     * @param filePath
     * @param str
     */
    public static void write(String filePath,String str){
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void appendWrite(String fileName, String content) {
        try {
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(content+"\n");
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void writeFile(String filename, CharSequence content) {
//        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")) {
//            writer.append(content);
//            writer.close();
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
}
