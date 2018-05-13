package util;

import java.io.BufferedWriter;
import java.io.FileWriter;

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
}
