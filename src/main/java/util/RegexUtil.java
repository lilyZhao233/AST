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
    public static String splitNameByshuxian(String s){
        String[] ss = s.split("(?<!^)(?=[A-Z])");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<ss.length; i++){
            if(ss[i].length()>1){
                if(i-1>0&&ss[i-1].length()<=1)
                    sb.append("|"+ss[i].toLowerCase()+"|");
                else sb.append(ss[i].toLowerCase()+"|");
            }
            else sb.append(ss[i].toLowerCase());

        }
        return sb.toString();
    }
    public static String splitNameByBlank(String s){
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
            comment = matcher.replaceAll("").replace("I/O","IO").replace("/"," ").replace("$","");
            comment = comment.replaceAll("[\\pP]"," ");//去除标点符号
            comment = comment.replaceAll("[\\d]","");//去除数字

            }
        return comment;
    }

    public static void main(String[] args){
//        System.out.println(splitComment(
//                "/** \n" +
//                " * Parses data from an HTML form that the client sends to  the server using the HTTP POST method and the  <i>application/x-www-form-urlencoded</i> MIME type. <p>The data sent by the POST method contains key-value pairs. A key can appear more than once in the POST data with different values. However, the key appears only once in  the hashtable, with its value being an array of strings containing the multiple values sent by the POST method. <p>The keys and values in the hashtable are stored in their decoded form, so any + characters are converted to spaces, and characters sent in hexadecimal notation (like <i>%xx</i>) are converted to ASCII characters.\n" +
//                " * @param len        an integer specifying the length,in characters, of the  <code>ServletInputStream</code> object that is also passed to this method\n" +
//                " * @param in        the <code>ServletInputStream</code>object that contains the data sent from the client\n" +
//                " * @return                a <code>HashTable</code> object builtfrom the parsed key-value pairs\n" +
//                " * @exception IllegalArgumentException        if the datasent by the POST method is invalid\n" +
//                " */"));

    }
}
