package visitors;

import bean.ExceptionBean;
import org.eclipse.jdt.core.dom.*;

import java.util.List;

public class MethodDeclarationVisitor extends ASTVisitor{
    List<ExceptionBean> exceptionBeanList;

    public MethodDeclarationVisitor(List<ExceptionBean> exceptionBeans) {
        exceptionBeanList=exceptionBeans;
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
            node.accept(anotv);
            if (!anotv.test) {
                //接下来遍历所有的方法调用和新建对象的语句，判断他们是不是抛出异常
                MethodInvocationVisitor methodInvocationVisitor=new MethodInvocationVisitor(exceptionBeanList,node);
                node.accept(methodInvocationVisitor);
            }

        }
        return super.visit(node);
    }


}
