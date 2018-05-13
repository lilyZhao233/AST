
package test;

import file.Apple;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by jiaxy on 2018/4/29.
 */
public class MyApp {

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<String> al = null;
        Apple apple=new Apple();
        apple.foo();
        apple.bar();
        new Orange();
        System.out.println("end");
    }
}
