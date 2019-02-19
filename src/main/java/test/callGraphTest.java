package test;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import util.WriteToFileUtil;

import java.io.*;
import java.util.*;

/**
 * Created by MIC on 2018/11/9.
 */
//涉及异常处理的函数调用图
public class callGraphTest {
    public static void main(String[] args) {
        callGraphTest obj = new callGraphTest();
        File file = new File("Tomcat\\TomcatEx-0218.xlsx");
        WriteToFileUtil.write("Tomcat\\TomcatEx-0218.txt",obj.readExcel(file));
    }

    public String readExcel(File file) {
        String str = "";
//                "digraph \"DirectedGraph\" {\n";
//        str += ("graph [label = \"" + "test" + "\", labelloc=t, concentrate = true];\n");
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
//            int sheet_size = wb.getNumberOfSheets();
            Sheet sheet = wb.getSheet(0);
            int n = sheet.getRows();
            Set<String> set = new HashSet<String>();
            StringBuilder all = new StringBuilder();
            for(int i=1;i<n;i++){
                String MethodInfo = sheet.getCell(1, i).getContents();
                String rMethodInfo = sheet.getCell(2, i).getContents();
                String rException = sheet.getCell(3, i).getContents();
                String rType = sheet.getCell(4, i).getContents();
                set.add(rType+"/"+rException+"/"+MethodInfo);
                boolean flag = false;
                String str1="";
                for (int j = 1; j < n; j++) {//遍历得到rMethod对于异常的处理方式。
                    String MethodInfo1 = sheet.getCell(1, j).getContents();
                    String rException1 = sheet.getCell(3, j).getContents();
                    if (rMethodInfo.equals(MethodInfo1) && rException.equals(rException1)) {
                        flag = true;
                        String rType1 = sheet.getCell(4,j).getContents();
                        set.add(rType1+"/"+rException+"/"+rMethodInfo);
                        str1 = "\""+rType1+"/"+rException+"/"+rMethodInfo+"\" -> \""
                                +rType+"/"+rException+"/"+MethodInfo+"\"\n";
                        break;
                    }
                }
                if(!flag){
                    set.add("only_throws"+"/"+rException+"/"+rMethodInfo);
                    str1 = "\""+"only_throws"+"/"+rException+"/"+rMethodInfo+"\" -> \""
                            +rType+"/"+rException+"/"+MethodInfo+"\"\n";
                }
                String[] info = str1.replace("\n","").split(" -> ");

                if(!info[0].equals(info[1]) && !all.toString().contains(str1)){
                    all.append(str1);
                }
            }
            StringBuilder builder = new StringBuilder();
            for(String key : set){
                builder.append("\""+key+"\"\n");
            }
            str += builder;
            str += all;
//            for (int i = 1; i < n; i++) {
//                String rMethodInfo = sheet.getCell(1, i).getContents();
//                String rException = sheet.getCell(3, i).getContents();
//                StringBuilder all = new StringBuilder();
//                for (int j = 1; j < sheet.getRows(); j++) {
//                    String MethodInfo = sheet.getCell(2, j).getContents();
//                    String rException1 = sheet.getCell(3, j).getContents();
//                    if (rMethodInfo.equals(MethodInfo) && rException.equals(rException1)) {
//                        String str1 =  rMethodInfo+" "+rException+" "+sheet.getCell(4,j).getContents() + " -> " + sheet.getCell(1,j).getContents() +" "+rException1+" "+sheet.getCell(4,j).getContents()+ "\n";
//                        if(!all.toString().contains(str1)){
//                            all.append(str1);
//                        }
//                    }
//                }
//                str += all;
//            }
//            str += "}\n";

            return str;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
