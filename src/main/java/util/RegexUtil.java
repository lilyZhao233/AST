package util;

import org.eclipse.jdt.internal.core.SourceType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fudanlong on 2019/2/28.
 */
public class RegexUtil {
    /**
     * 将字符串相连的单词根据首字母大写拆开
     * @param s
     */
    public static String splitName(String s){
        String[] ss = s.split("(?<!^)(?=[A-Z])");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<ss.length; i++){
            if(ss[i].length()>1){
                if(i-1>0&&ss[i-1].length()<=1)
                    sb.append(" "+ss[i].toLowerCase()+" ");
                else sb.append(ss[i].toLowerCase()+" ");
            }
            else sb.append(ss[i].toLowerCase());

        }
        return sb.toString();
    }

    /**
     * 将注解内容分解
     * @param str
     */
    public static String splitComment(String str){
        String comment = "";
        if(str!=null) {
                comment = str.split("\n")[1];
                Pattern pattern = Pattern.compile("<[^>]+>");//去除html标签
                Matcher matcher = pattern.matcher(comment);
                comment = matcher.replaceAll("").replace("/","");
                comment = comment.replaceAll("[\\pP]"," ").toLowerCase();//去除标点符号

            }
        return comment;
    }

    public static void main(String[] args){
        System.out.println(splitComment("/** \n" +
                " * Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.\n" +
                " * @author  unascribed\n" +
                " * @see java.io.InputStream\n" +
                " * @see java.io.OutputStream\n" +
                " * @since   JDK1.0\n" +
                " */"));
        System.out.println(splitName("an.IO.exception"));
    }
}
