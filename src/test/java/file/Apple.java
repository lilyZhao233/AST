package file;

import java.io.*;

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