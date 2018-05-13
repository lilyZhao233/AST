package util;


import java.io.File;
import java.io.FileFilter;

public class Filterbyjava implements FileFilter {

    public String suffix;

    public Filterbyjava(String suffix) {
        super();
        this.suffix = suffix;
    }

    @Override
    public boolean accept(File pathname) {
        // TODO Auto-generated method stub
        return pathname.getName().endsWith(suffix);
    }


}