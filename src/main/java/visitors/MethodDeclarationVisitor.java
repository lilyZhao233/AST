package visitors;

import bean.CommentException;
import bean.ExceptionBean;
import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDeclarationVisitor extends ASTVisitor{
    List<ExceptionBean> exceptionBeanList;

    List<CommentException> commentExceptions;

    Map<String,String> comments=new HashMap<String, String>();

    public MethodDeclarationVisitor(List<ExceptionBean> exceptionBeans,List<CommentException> commentExceptions,Map<String,String> comments) {
        this.commentExceptions=commentExceptions;
        this.exceptionBeanList=exceptionBeans;
        this.comments=comments;
    }

    /**
     * 首先寻找有Exception的所有方法
     * @param node
     * @return
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        String methodbody =" ";
        methodbody=node.toString();
        if (methodbody.contains("Exception")) {

            //判断是不是测试文件
            MarkerAnnotationVisitor anotv = new MarkerAnnotationVisitor();
            if (!anotv.test) {
                //接下来遍历所有的方法调用和新建对象的语句，判断他们是不是抛出异常
                MethodInvocationVisitor methodInvocationVisitor=new MethodInvocationVisitor(exceptionBeanList,node,comments);
                node.accept(methodInvocationVisitor);
            }

        }
        return super.visit(node);
    }
//    @Override
//    public boolean visit(CompilationUnit node){
//
//        CommentException commentException=new CommentException();
//        List types = node.types();
//        if(types.size()>0) {
//            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
//            if (typeDec.getName().getFullyQualifiedName().endsWith("Exception")) {
//                commentException.setExceptionName(typeDec.getName().getFullyQualifiedName());
//                int i=0;
//                while(i<node.getCommentList().size()) {
//                    if (node.getCommentList().get(i) instanceof Javadoc &&
//                            node.getCommentList().get(i).toString().contains("@")) {
//                        commentException.setExceptionComment(node.getCommentList().get(i).toString());
//                        System.out.println(commentException.getExceptionName());
//                        System.out.println(commentException.getExceptionComment());
//                        commentExceptions.add(commentException);
//                        break;
//                    }
//                    i++;
//                }
//
//            }
//        }
//        return super.visit(node);
//    }


}
