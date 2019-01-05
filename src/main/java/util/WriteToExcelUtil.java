package util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
/**
 * Created by MIC on 2018/5/29.
 */
public class WriteToExcelUtil {
    /**
     * 将字符串写入文件
     * @param data
     * @param writeUrl
     */
    public static void writeEx(List<String[]> data, String writeUrl){
        WritableWorkbook wwb = null;
        String file =writeUrl;
        try {
            // 创建可写入的工作簿对象
             wwb= Workbook.createWorkbook(new File(file));
            if (wwb != null) {
                // 在工作簿里创建可写入的工作表，第一个参数为工作表名，第二个参数为该工作表的所在位置
                WritableSheet ws = wwb.createSheet("test", 0);
                if (ws != null) {
                    /* 添加表结构 */
                    for (int i=0;i<data.size();i++) {// 行
                        for (int j=0;j<data.get(i).length;j++) { // 列
                            // Label构造器中有三个参数，第一个为列，第二个为行，第三个则为单元格填充的内容
                            Label label = new Label(j, i,data.get(i)[j] );
                            // 将被写入数据的单元格添加到工作表
                            ws.addCell(label);
                        }
                    }
                    // 从内存中写入到文件
                    wwb.write();
                }
                System.out.println("路径为：" + file + "的工作簿写入数据成功！");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                wwb.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
//    public static void main(String[] args){
//        String[] s = {"a","b","c","d"};
//        String[] s1 = {"a","b","c","d"};
//        List<String[]> data= new ArrayList<String[]>();
//        data.add(s);
//        if(!data.contains(s1)) {
//            System.out.println(data.contains(s1));
//            data.add(s1);
//        }
//        writeEx(data,"text.xls");
//    }
}
