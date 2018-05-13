package util;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class FileHandlers {
    public static void  getFileList(File dir, List<File> list, FileFilter fileFilter){
        if(dir==null){
            return;
        }
        File[] files=dir.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                getFileList(file,list,fileFilter);
            }else{
                if(fileFilter.accept(file)){
                    list.add(file);
                }
            }
        }

    }
    /**
     * 获取所有资源的路径
     * @param dir
     * @param paths
     */
    public static void getSources(File dir ,List<String> paths){
        File[] files=dir.listFiles();
        for(File file:files){
            if(file.isDirectory()&&file.getName().equals("main")){
                File []files1=file.listFiles();
                boolean ispass=true;
                for(File file1:files1){
                    if(file1.getName().equals("java")){
                        paths.add(file1.getAbsolutePath());
                        ispass=false;
                        break;
                    }
                    paths.add(file1.getAbsolutePath());
                }
                paths.add(file.getAbsolutePath());
                if(ispass){
                    getSources(file,paths);
                }
            }else if(file.isDirectory()&&(file.getName().equals(".git")||file.getName().equals(".idea"))){

            }else if(file.isDirectory()){
                paths.add(file.getAbsolutePath());
                getSources(file,paths);
            }
        }

    }

}
