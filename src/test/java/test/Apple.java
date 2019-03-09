package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Apple {

    public void foo() throws FileNotFoundException {
        for(int i=0;i<10;i++){
            bar();
        }
    }

    public void bar() throws FileNotFoundException {
        File f=new File("XXX");
        FileReader reader=new FileReader(f);
        foo();
    }
}