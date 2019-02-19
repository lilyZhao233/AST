package test;

import util.StringUtil;
import util.WriteToExcelUtil;
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
        String path = "Tomcat\\TomcatEx-0218.txt";
        Map<String,List> Bmap= new HashMap<String,List>();//节点被调用
        Map<String,List> map = new HashMap<String, List>();//节点调用了哪些节点；当value值为null表示该节点为底层节点
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
        Map<String,Integer> LabelS = new HashMap<String, Integer>();
        Map<String,List<Map<String,Integer>>> Statistic = new HashMap<String, List<Map<String, Integer>>>();
        List<String[]> data= new ArrayList<String[]>();
        String[] head = {"exception","p1","p1","location","p2","p2","location","p3","p3","location"};
        data.add(head);
        for(String key : map.keySet()){
            if(map.get(key)==null) {
                List<String> temp = new ArrayList<String>();
                String[] info = key.split("/");
                if(!info[0].equals("only_throws") && Bmap.get(key)!=null) {
                    continue;//保证异常的正常传播
                }

//                temp.add(info[0].substring(0,info[0].indexOf('#')));//类结构
                temp.add(key);

                List<List<String>> result = new ArrayList<List<String>>();
                getExceptionLink(key, Bmap, temp, result);
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<result.size(); i++){
                    String lastHandler = result.get(i).get(result.get(i).size()-1).split("/")[0];
                    if(lastHandler.equals("log and throw")
                            ||lastHandler.equals("catch and Ignore")){//过滤反模式异常处理方式
                        break;
                    }
                    List<String> r = new ArrayList<String>();
//                    String str = info[2].split("#")[0];
                    boolean flag3 = false;
                    for(String key1 : SOURCE_PACKAGE){
                        if(info[2].startsWith(key1)){
                            flag3 = true;
                            break;
                        }
                    }
                    String pc,pack;
                    if(flag3) {
                        r.add(result.get(i).get(1));
                        pc = result.get(i).get(1).split("/")[2].split("#")[0];
                        pack = pc.substring(0,pc.lastIndexOf(".")+1);
                    }else {
                        r.add(result.get(i).get(1));
                        pc = result.get(i).get(0).split("/")[2].split("#")[0];
                        pack = pc.substring(0,pc.lastIndexOf(".")+1);
                    }

                    int j=1;
                    while(j<result.get(i).size()) {//提取每条链中的信息
                        String pc1 = result.get(i).get(j).split("/")[2].split("#")[0];
                        String pack1 = pc1.substring(0,pc1.lastIndexOf(".")+1);
                        if(!pack.equals(pack1)){
                            pack = pack1;
                            r.add(result.get(i).get(j-1));//保存同一包下的始末信息
                            r.add(result.get(i).get(j));
                        }
                        j++;
                    }
                    r.add(result.get(i).get(result.get(i).size()-1));
                    String[] data1 = new String[30];
                    data1[0] = info[1].replace("."," ");
                    int n = 1;
                    if(r.size()==1){
                        data1[1] = r.get(0).split("/")[2];
                        data1[2] = data1[1];
                        if(r.get(0).split("/")[0].equals("only_throws")) data1[3]="0";
                        else data1[3]="1";
                    }
                    else {
                        for(int k=0; k<r.size()-1; k+=2){
                            data1[n] = r.get(k).split("/")[2];
                            String[] info1 = r.get(k+1).split("/");
                            data1[n+1] = info1[2];
                            if(info1[0].equals("only_throws")) data1[n+2] = "0";
                            else {
                                if(info1[2].equals(r.get(k).split("/")[2])) data1[n+2]="1";//方法中立即处理
                                else if(data1[n].equals(r.get(k).split("/")[2].split("#")[0])) data1[n+2] = "2";//同一类中处理
                                else data1[n+2] = "3";
                            }
                            n = n+3;
                        }
                    }
                    data.add(data1);
//                    for(String s : result.get(i)){
//                        sb.append(s+" ");
//                    }
//                    sb.append(info[1].replace("."," ")+"\n");//添加异常


                    //统计每个异常在哪一层处理
//                    List<Map<String,Integer>> newAdd = new LinkedList<Map<String, Integer>>();
//                    if(Statistic.containsKey(info[1])){
//                        newAdd = Statistic.get(info[1]);
//                        for(Map<String,Integer> smap: newAdd){
//                            if(smap.containsKey(label)){
//                                smap.put(label,smap.get(label)+1);
//                            }else smap.put(label,1);
//                        }
//                        Statistic.put(info[1],newAdd);
//                    }else {
//                        Map<String,Integer> firstnew = new HashMap<String, Integer>();
//                        firstnew.put(label,1);
//                        newAdd.add(firstnew);
//                        Statistic.put(info[1],newAdd);
//                    }
                }//for

//                WriteToFileUtil.appendWrite("HBaseExCallGraph-0116.txt",sb.toString());

                //单独保存jdk引起的异常链信息
//                boolean flag = false;
//                for(String pkg: SOURCE_PACKAGE){
//                    if(key.startsWith(pkg)) {
//                        flag=true;
//                        break;
//                    }
//                }
//                if(flag){
//                    String s1="";
////                     s1 = key.substring(0,key.indexOf(')')+1);//函数调用图
//                     s1 = key.substring(0,key.indexOf('#'));//类调用
////                    String temp1 = key.substring(0,key.indexOf('#'));
////                    if(temp1.lastIndexOf('.')==-1) s1 = ".";
////                    else s1  = temp1.substring(0,temp1.lastIndexOf('.'));
//                    set.add("\""+s1+"\"\n");
//                    if(Bmap.get(key)!=null){
//                        for(Object s: Bmap.get(key)){
//                            String s2 = "";
////                             s2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf(')')+1);
//                             s2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf('#'));
////                            String temp2 = String.valueOf(s).substring(0,String.valueOf(s).indexOf('#'));
////                            if(temp2.lastIndexOf('.')==-1) s2 = ".";
////                            else s2 = temp2.substring(0,temp2.lastIndexOf('.'));
//                            String news = "\""+s1+"\" -> \""+s2+"\"\n";
//                            if(!stringBuilder.toString().contains(news))
//                                stringBuilder.append(news);
//                        }
//                    }
//                }
            }//if(map.get(key)==null)
        }//for

        WriteToExcelUtil.writeEx(data,"Tomcat\\tomcatStatic-0218.xlsx");
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
//                if(temp.contains(s1.substring(0,s1.indexOf('#'))+" "+s1.split(" ")[2])) continue;//避免环
//                temp.add(s1.substring(0,s1.indexOf('#'))+" "+s1.split(" ")[2]);//保留函数信息
                if(temp.contains(s1)) continue;//避免环
                temp.add(s1);
                getExceptionLink(s1,Bmap,temp,result);
                temp.remove(temp.size()-1);
            }
        }
    }
    private static final Set<String> Down = new HashSet<String>();
    static {
        Down.add("org.apache.catalina");
        Down.add("org.apache.tomcat.util.buf");
        Down.add("javax.el");
        Down.add("org.apache.naming");
        Down.add("org.apache.catalina.tribes.group");
        Down.add("org.apache.tomcat.util.http.fileupload");
        Down.add("org.apache.tomcat.util.http.fileupload.util");
        Down.add("org.apache.jasper.tagplugins.jstl");
        Down.add("javax.servlet");
        Down.add("org.apache.catalina.manager.util");
        Down.add("org.apache.tomcat.util");
        Down.add("org.apache.tomcat.util.bcel.classfile");
        Down.add("org.apache.catalina.ant");
        Down.add("org.apache.el.util");
        Down.add("org.apache.jasper.el");
        Down.add("org.apache.tomcat.util.modeler");
        Down.add("org.apache.tomcat.websocket.pojo");

    }
    private static final Set<String> Top = new HashSet<String>();
    static {
        Top.add("org.apache.catalina.manager");
        Top.add("org.apache.catalina.ant.jmx");
        Top.add("org.apache.tomcat.websocket.server");
        Top.add("org.apache.tomcat.util.net.jsse");
        Top.add("org.apache.catalina.ha.jmx");
        Top.add("org.apache.tomcat.util.modeler.modules");
        Top.add("org.apache.catalina.manager.host");
        Top.add("org.apache.catalina.ha.deploy");
        Top.add("org.apache.catalina.users");
        Top.add("org.apache.catalina.tribes.transport.bio");
        Top.add("org.apache.coyote.ajp");
        Top.add("jsp2.examples.simpletag");
        Top.add("org.apache.naming.factory");
        Top.add("org.apache.catalina.ha.backend");
        Top.add("org.apache.catalina.websocket");
        Top.add("org.apache.catalina.servlets");
        Top.add("javax.websocket.server");
        Top.add("org.apache.tomcat.util.bcel.classfile");
        Top.add("org.apache.tomcat.util.scan,websocket");
        Top.add("async");
        Top.add("org.apache.tomcat.util");
        Top.add("org.apache.catalina.ssi");
        Top.add("org.apache.naming.factory.webservices");
        Top.add("compressionFilters");
        Top.add("org.apache.catalina.filters");
        Top.add("org.apache.tomcat.util.descriptor");
        Top.add("org.apache.tomcat.util.http.fileupload.disk");
        Top.add("org.apache.juli");
        Top.add("org.apache.tomcat.buildutil");
        Top.add("chat");
        Top.add("validators");
        Top.add("websocket.drawboard");
        Top.add("org.apache.tomcat.util.http.fileupload.servlet");
        Top.add("websocket.chat");
        Top.add("websocket.snake");

    }
    private static final Set<String> SOURCE_PACKAGE = new HashSet<String>();
    static {
        SOURCE_PACKAGE.add("java.");
        SOURCE_PACKAGE.add("javax.management.");
        SOURCE_PACKAGE.add("javax.naming");
        SOURCE_PACKAGE.add("javax.xml.parsers.");
        SOURCE_PACKAGE.add("javax.xml.transform.");
        SOURCE_PACKAGE.add("javax.imageio.");
        SOURCE_PACKAGE.add("javax.net.");
        SOURCE_PACKAGE.add("javax.security.");
        SOURCE_PACKAGE.add("javax.sql.");
//        SOURCE_PACKAGE.add("async.");
//        SOURCE_PACKAGE.add("cal.");
//        SOURCE_PACKAGE.add("chat.");
//        SOURCE_PACKAGE.add("checkbox.");
//        SOURCE_PACKAGE.add("colors.");
//        SOURCE_PACKAGE.add("compressionFilters.");
//        SOURCE_PACKAGE.add("dates.");
//        SOURCE_PACKAGE.add("error.");
//        SOURCE_PACKAGE.add("examples.");
//        SOURCE_PACKAGE.add("filters.");
//        SOURCE_PACKAGE.add("javax.");
//        SOURCE_PACKAGE.add("jsp2.");
//        SOURCE_PACKAGE.add("listeners.");
//        SOURCE_PACKAGE.add("num.");
//        SOURCE_PACKAGE.add("org.");
//        SOURCE_PACKAGE.add("sessions.");
//        SOURCE_PACKAGE.add("util.");
//        SOURCE_PACKAGE.add("validators.");
//        SOURCE_PACKAGE.add("websocket.");
    }

}
