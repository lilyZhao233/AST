package test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.CommentException;
import bean.ExceptionBean;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import util.*;
import visitors.MethodDeclarationVisitor;

public class ASTTester {

    public static void main(String[] args) throws CoreException, SQLException, ClassNotFoundException {
//        tomcat: C:\Users\MIC\Documents\experiment project\tomcat\tomcatsrc\java
        String[] classpath = {"/Users/fudanlong/lilly/experiment project/tomcat/tomcatsrc/lib"};
//        String[] classpath = {""};
        File dir=new File("/Users/fudanlong/lilly/experiment project/tomcat/tomcatsrc/java" );
//       List<String> strings=new ArrayList<String>();
//       getSources(dir,strings);
       String [] sources={"/Users/fudanlong/lilly/experiment project/tomcat/tomcatsrc/java"};
//       strings.toArray(sources);
       List<File> fileList=new ArrayList<File>();
       FileFilter fileFilter=new Filterbyjava(".java");
       FileHandlers.getFileList(dir,fileList,fileFilter);

       List<ExceptionBean> exceptionBeans=new ArrayList<ExceptionBean>();
       List<CommentException> commentExceptions=new ArrayList<CommentException>();
       String sql="select * from e_comment";
       Map<String,String> comments=new HashMap<String, String>();
       Connection connection = SQLUtil.getCon("RH_Exception");
       ResultSet resultSet=SQLUtil.getCon("RH_Exception").createStatement().executeQuery(sql);
        while (resultSet.next()){
           comments.put(resultSet.getString("name"),resultSet.getString("comment"));
        }
       for(File file:fileList){
            parseJavaFile(file,exceptionBeans,commentExceptions,sources,classpath,comments);
        }
        String result="";
        String result1="";
        String result2="";
        String str="";
        List<String[]> data= new ArrayList<String[]>();
        String[] head = {"id","method","rMethod","rException","eType"};
        data.add(head);
        int i=0;
        for(ExceptionBean exceptionBean:exceptionBeans){
            String[] info = new String[5];
            info[0] = String.valueOf(i);
            info[1] = exceptionBean.getMethod();
            info[2] = exceptionBean.getRmethod();
            info[3] = exceptionBean.getThrown();
            info[4] = exceptionBean.getType();
            data.add(info);
            i++;

            String methodstr = exceptionBean.getMethod().substring(0,exceptionBean.getMethod().indexOf('('));
            String method = RegexUtil.splitName(methodstr).replace("."," ").replace("#"," ");
//            拆分函数名，类名
            String rmethodstr = exceptionBean.getRmethod().substring(0,exceptionBean.getRmethod().indexOf('('));
            String rmethod = RegexUtil.splitName(rmethodstr).replace("."," ").replace("#"," ");
            String thrown = exceptionBean.getThrown().replace("."," ");

            String exceptionComm = RegexUtil.splitComment(exceptionBean.getExceptionComment());
            String methodComm = RegexUtil.splitComment(exceptionBean.getMethodComment());
//
            if(exceptionBean.getType().equals("1") ){//|| exceptionBean.getType().equals("0")){
                str=
                        exceptionBean.getType()+"\t"
//                                +exceptionBean.getRmethod().substring(0,exceptionBean.getRmethod().indexOf('#')) +". "
//                            +rmethod+" "
//                                + exceptionBean.getMethod().substring(0,exceptionBean.getMethod().indexOf('#')) +". "
                            + method+" "+methodComm
                                + thrown+" "+exceptionBean.getParentException()+" "+exceptionComm+"\n";
                System.out.println(exceptionBean.getType());
                result2 +=
                        "ID "+i+"========================================================================="+
                                "type: "+exceptionBean.getType()+"\n"+

//                                "package: "+exceptionBean.getPackages()+"\n"+
                                "Method:"+exceptionBean.getMethod()+"\n"+
                                "Rmethod: "+exceptionBean.getRmethod()+"\n"+
//                                "hasForStatement: "+exceptionBean.isHasForStat()+"\n"+
                                "parentException: "+exceptionBean.getParentException()+"\n"+
                                "thrown: "+exceptionBean.getThrown()+"\n"+
                                "exception comment: "+exceptionBean.getExceptionComment()+"\n"+
//                                "method comment: "+exceptionBean.getMethodComment()+"\n"+
//                                "catch: \n"+exceptionBean.getCatched()+"\n"+
                                "block: \n"+exceptionBean.getBlock()+"\n";
            }


            result += str.replaceAll(" {2,}"," ");

        }
        connection.close();
//          WriteToFileUtil.appendWrite("Hadoop/hadoopTrain-0305.txt",result);

         WriteToFileUtil.appendWrite("Tomcat/TomcatOri.txt",result2);
//
//          WriteToExcelUtil.writeEx(data,"Hama/HamaEx-0226.xlsx");
//
    }

    private static void parseJavaFile(File file, List<ExceptionBean> exceptionBeans,List<CommentException> commentExceptions,String[] sources,String[] classpath,Map<String,String> comments) {
        String str = "";
        try {
            str = FileUtils.readFileToString(file);

            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setResolveBindings(true);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            parser.setBindingsRecovery(true);
            Map options = JavaCore.getOptions();
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
            parser.setCompilerOptions(options);

            parser.setProject(null);

            parser.setEnvironment(classpath, sources, null, true);
            parser.setSource(str.toCharArray());
            parser.setUnitName(file.getAbsolutePath());

            parser.setResolveBindings(true);

            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            if (cu.getAST().hasBindingsRecovery()) {
                System.out.println("Binding activated.");
            }
            MethodDeclarationVisitor v = new MethodDeclarationVisitor(exceptionBeans,commentExceptions,comments);
            cu.accept(v);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
