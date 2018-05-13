package test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

import bean.ExceptionBean;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import util.FileHandlers;
import util.Filterbyjava;
import util.WriteToFileUtil;
import visitors.MethodDeclarationVisitor;

public class ASTTester {




    public static void main(String[] args) throws CoreException {

        String[] classpath = {"C:\\Users\\13422\\Documents\\Git\\tomcatsrc\\lib"};
       File dir=new File("C:\\Users\\13422\\Documents\\Git\\tomcatsrc");
//       List<String> strings=new ArrayList<String>();
//       getSources(dir,strings);
       String [] sources={"C:\\Users\\13422\\Documents\\Git\\tomcatsrc\\java"};
//       strings.toArray(sources);
       List<File> fileList=new ArrayList<File>();
       FileFilter fileFilter=new Filterbyjava(".java");
       FileHandlers.getFileList(dir,fileList,fileFilter);
       List<ExceptionBean> exceptionBeans=new ArrayList<ExceptionBean>();
       for(File file:fileList){
            parseJavaFile(file,exceptionBeans,sources,classpath);
        }
        String result="";
        String str="";
        int i=0;
        for(ExceptionBean exceptionBean:exceptionBeans){

            str="ID "+i+"========================================================================="+
                    "type: "+exceptionBean.getType()+"\n"+
                    "package: "+exceptionBean.getPackages()+"\n"+
                    "method: "+exceptionBean.getMethod()+"\n"+
                    "statement: "+exceptionBean.getStatement()+"\n"+
                    "thrown: "+exceptionBean.getThrown()+"\n"+
                    "comment: "+exceptionBean.getComment()+"\n"+
                    "catch: \n"+exceptionBean.getCatched()+"\n"+
                    "block: \n"+exceptionBean.getBlock()+"\n";
            result+=str;
            i++;
        }
        WriteToFileUtil.write("tomcat0509.txt",result);
    }

    private static void parseJavaFile(File file, List<ExceptionBean> exceptionBeans,String sources[],String []classpath) {
        String str = null;
        try {
            str = FileUtils.readFileToString(file);
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setResolveBindings(true);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            parser.setBindingsRecovery(true);

            Map options = JavaCore.getOptions();
            parser.setCompilerOptions(options);

            parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
            parser.setSource(str.toCharArray());
            parser.setUnitName(file.getName());

            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            if (cu.getAST().hasBindingsRecovery()) {
                System.out.println("Binding activated.");
            }
            MethodDeclarationVisitor v = new MethodDeclarationVisitor(exceptionBeans);
            cu.accept(v);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
