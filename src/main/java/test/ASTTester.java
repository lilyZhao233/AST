package test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import bean.CommentException;
import bean.ExceptionBean;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import util.FileHandlers;
import util.Filterbyjava;
import util.SQLUtil;
import util.WriteToFileUtil;
import visitors.MethodDeclarationVisitor;

public class ASTTester {




    public static void main(String[] args) throws CoreException, SQLException, ClassNotFoundException {

        String[] classpath = {"C:\\Users\\13422\\Documents\\Git\\tomcatsrc\\lib"};
       File dir=new File("C:\\Users\\13422\\Documents\\Git\\tomcatsrc\\java");
//       List<String> strings=new ArrayList<String>();
//       getSources(dir,strings);
       String [] sources={"C:\\Users\\13422\\Documents\\Git\\tomcatsrc\\java"};
//       strings.toArray(sources);
       List<File> fileList=new ArrayList<File>();
       FileFilter fileFilter=new Filterbyjava(".java");
       FileHandlers.getFileList(dir,fileList,fileFilter);
       List<ExceptionBean> exceptionBeans=new ArrayList<ExceptionBean>();
       List<CommentException> commentExceptions=new ArrayList<CommentException>();
       String sql="select * from e_comment";
       Map<String,String> comments=new HashMap<String, String>();
       ResultSet resultSet=SQLUtil.getCon("RH_Exception").createStatement().executeQuery(sql);
        while (resultSet.next()){
           comments.put(resultSet.getString("name"),resultSet.getString("comment"));
        }
       for(File file:fileList){
            parseJavaFile(file,exceptionBeans,commentExceptions,sources,classpath,comments);
        }
        String result="";
        String str="";
        int i=0;
        for(ExceptionBean exceptionBean:exceptionBeans){

            str="ID "+i+"========================================================================="+
                    "type: "+exceptionBean.getType()+"\n"+
                    "package: "+exceptionBean.getPackages()+"\n"+
                    "method: "+exceptionBean.getMethod()+"\n"+
                    "hasForStatement: "+exceptionBean.isHasForStat()+"\n"+
                    "parentException: "+exceptionBean.getParentException()+"\n"+
                    "thrown: "+exceptionBean.getThrown()+"\n"+
                    "exception comment: "+exceptionBean.getExceptionComment()+"\n"+
                    "method comment: "+exceptionBean.getMethodComment()+"\n"+
                    "catch: \n"+exceptionBean.getCatched()+"\n"+
                    "block: \n"+exceptionBean.getBlock()+"\n";
            result+=str;
            i++;
        }
        WriteToFileUtil.write("tomcat0519_2.txt",result);
//        PreparedStatement psql;
//        psql = SQLUtil.getCon("RH_Exception").prepareStatement("insert into e_comment (name,comment) "
//                + "values(?,?)");
//        for(CommentException exception:commentExceptions){
//            psql.setString(1,exception.getExceptionName());
//            psql.setString(2,exception.getExceptionComment());
//            psql.executeUpdate();
//        }
    }

    private static void parseJavaFile(File file, List<ExceptionBean> exceptionBeans,List<CommentException> commentExceptions,String sources[],String []classpath,Map<String,String> comments) {
        String str = null;
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
            parser.setProject(null);

            parser.setCompilerOptions(options);

            parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
            parser.setSource(str.toCharArray());
            parser.setUnitName(file.getName());

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
