package test;

import util.StringUtil;
import util.WriteToFileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by MIC on 2018/11/15.
 */
// 异常信息三元组eg:（m1,e1,m2,m3）
// jdkInfo eg:(m1,m2)
public class ExceptionData {
    public static void main(String[] args) {
        String path = "Exception-1212.txt";
        Map<String,List> Bmap= new HashMap<String,List>();//被调用
        Map<String,List> map = new HashMap<String, List>();
        try{
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.contains(" -> ")){
                    Bmap.put(line.replace("\"",""),null);
                    map.put(line.replace("\"",""),null);
                }else{
                    String[] r=line.replace("\"","").split(" -> ");
                    List<String> list;
                    if(Bmap.get(r[0])!=null){
                        list = Bmap.get(r[0]);
                    }else list = new ArrayList<String>();
                    list.add(r[1]);
                    Bmap.put(r[0],list);

                    List<String> list1;
                    if(map.get(r[1])!=null){
                        list1 = map.get(r[1]);
                    }else list1 = new ArrayList<String>();
                    list1.add(r[0]);
                    map.put(r[1],list1);
                }
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> set = new TreeSet<String>();
        for(String key : map.keySet()){
            if(map.get(key)==null) {
//                List<String> temp = new ArrayList<String>();
//                temp.add(key.substring(0,key.lastIndexOf(" ")));//保留函数和异常信息
//                List<List<String>> result = new ArrayList<List<String>>();
//                getExceptionLink(key, Bmap, temp, result);
//                StringBuilder sb = new StringBuilder();
//                for(int i=0; i<result.size(); i++){
//                    for(String s : result.get(i)){
//                        sb.append(s+" ");
//                    }
//                    sb.append("\n");
//                }
//                WriteToFileUtil.appendWrite("DataSet-test1.txt",sb.toString());

                //单独保存jdk引起的异常链信息
                boolean flag = true;
                for(String pkg: SOURCE_PACKAGE){
                    if(key.startsWith(pkg)) {
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    String s1="";
                     s1 = key.substring(0,key.indexOf(')')+1);//函数调用图
//                     s1 = key.substring(0,key.indexOf('#'));//类调用
//                    String temp1 = key.substring(0,key.indexOf('#'));
//                    if(temp1.lastIndexOf('.')==-1) s1 = ".";
//                    else s1  = temp1.substring(0,temp1.lastIndexOf('.'));
                    set.add("\""+s1+"\"\n");
                    if(Bmap.get(key)!=null){
                        for(Object s: Bmap.get(key)){
                            String s2 = "";
                             s2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf(')')+1);
//                             s2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf('#'));
//                            String temp2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf('#'));
//                            if(temp2.lastIndexOf('.')==-1) s2 = ".";
//                            else s2 = temp2.substring(0,temp2.lastIndexOf('.'));
                            String news = "\""+s1+"\" -> \""+s2+"\"\n";
                            if(!stringBuilder.toString().contains(news))
                                stringBuilder.append(news);
                            System.out.println(s1+" "+s2);
                        }
                    }
                }
            }
        }
        String t ="";
        for(String s : set){
            t += s;
        }
        WriteToFileUtil.appendWrite("jdkInfo-1.txt",t);//端点信息
        WriteToFileUtil.appendWrite("jdkInfo-1.txt",stringBuilder.toString());
    }
//    递归
    public static void getExceptionLink(String key, Map<String,List> Bmap, List<String> temp, List<List<String>> result){
        if(Bmap.get(key)==null) {
            result.add(new ArrayList<String>(temp));
            return;
        }
        else{
            for(Object s : Bmap.get(key)){
                String s1 = String.valueOf(s);
                temp.add(s1.substring(0,s1.indexOf(')')+1));//保留函数信息
                getExceptionLink(String.valueOf(s),Bmap,temp,result);
                temp.remove(temp.size()-1);
            }
        }
    }

    private static final Set<String> SOURCE_PACKAGE = new HashSet<String>();
    static {
        SOURCE_PACKAGE.add(".");
        SOURCE_PACKAGE.add("async.");
        SOURCE_PACKAGE.add("cal.");
        SOURCE_PACKAGE.add("chat.");
        SOURCE_PACKAGE.add("checkbox.");
        SOURCE_PACKAGE.add("colors.");
        SOURCE_PACKAGE.add("compressionFilters.");
        SOURCE_PACKAGE.add("dates.");
        SOURCE_PACKAGE.add("error.");
        SOURCE_PACKAGE.add("examples.");
        SOURCE_PACKAGE.add("filters.");
        SOURCE_PACKAGE.add("javax.");
        SOURCE_PACKAGE.add("jsp2.");
        SOURCE_PACKAGE.add("listeners.");
        SOURCE_PACKAGE.add("num.");
        SOURCE_PACKAGE.add("org.");
        SOURCE_PACKAGE.add("sessions.");
        SOURCE_PACKAGE.add("util.");
        SOURCE_PACKAGE.add("validators.");
        SOURCE_PACKAGE.add("websocket.");
    }

}
