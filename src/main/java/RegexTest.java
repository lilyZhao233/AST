/**
 * Created by MIC on 2019/2/22.
 */
public class RegexTest {
    public static void main(String[] args) {
        String s = "org.apache.tomcat.util.scan";
//        String[] ss = s.split("(?<!^)(?=[A-Z])");
//        for(int k = 0 ;k < ss.length; k ++){
//            System.out.println(ss[k]);
//        }

        System.out.println(s.substring(0,s.lastIndexOf(".")).replace("."," "));
    }

}
