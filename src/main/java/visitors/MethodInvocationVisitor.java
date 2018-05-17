package visitors;

import bean.ExceptionBean;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;
import util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInvocationVisitor extends ASTVisitor{
    private List<ExceptionBean> exceptionBeanList;
    MethodDeclaration methodDeclaration;
    Map<String,String> comments=new HashMap<String, String>();

    public MethodInvocationVisitor(List<ExceptionBean> exceptionBeans,MethodDeclaration node,Map<String,String> comments){
        this.exceptionBeanList=exceptionBeans;
        this.methodDeclaration=node;
        this.comments=comments;
    }

    /**
     * 遍历方法调用
     * @param node
     */
    @Override
    public void endVisit(MethodInvocation node) {
        IMethodBinding m = node.resolveMethodBinding();
        if(m!=null){
            //判断是否原方法是不是抛出异常
            ITypeBinding exceptionTypes[]= m.getExceptionTypes();
            if(exceptionTypes.length>0){
                for(ITypeBinding iTypeBinding:exceptionTypes){
                    String thrown=iTypeBinding.getPackage()+iTypeBinding.getName();
                    boolean hasType=false;
                    ExceptionBean exceptionBean=new ExceptionBean();
                    if(comments.containsKey(iTypeBinding.getName())){
                        System.out.println(thrown);
                        exceptionBean.setExceptionComment(comments.get(iTypeBinding.getName()));
                    }
                    exceptionBean.setThrown(thrown);
                    exceptionBean.setMethod(node.getName().toString());
                    exceptionBean.setBlock(methodDeclaration.toString());
                    ASTNode node1=node.getParent();
                    //判断离这个语句最近的语句的类型
                    while (!(node1 instanceof TryStatement
                            ||node1 instanceof MethodDeclaration)){
                        node1=node1.getParent();
                    }
                    //如果是包含在try catch块中则统计最近的catch块里的内容
                    if(node1 instanceof TryStatement){
                        List catchCauses=((TryStatement) node1).catchClauses();
                        for (Object catchClause : catchCauses) {
                            CatchClause catchClause1= (CatchClause) catchClause;
                            if (catchClause.toString().contains(thrown)) {
                                hasType = true;
                                exceptionBean.setCatched(catchClause.toString());
                                if (catchClause1.getBody().toString().contains("throw new")) {
                                    exceptionBean.setType("Rethrow");
                                } else if (catchClause1.getBody()==null||
                                        catchClause1.getBody().toString().contains("log") ||
                                        catchClause.toString().contains("Log")
                                        || StringUtil.replaceBlank(catchClause1.getBody().toString()).equals("{}")) {
                                    exceptionBean.setType("Ignore_Log");
                                } else {
                                    exceptionBean.setType("Recover");
                                }
                            }
                        }
                        node1=node1.getParent();
                        while (!(node1 instanceof TryStatement
                                ||node1 instanceof ForStatement
                                ||node1 instanceof WhileStatement
                                ||node1 instanceof DoStatement
                                ||node1 instanceof SwitchStatement
                                ||node1 instanceof ThrowStatement
                                ||node1 instanceof IfStatement
                                ||node1 instanceof MethodDeclaration)){
                            node1=node1.getParent();
                        }
                    }

                    if(!hasType){
                        exceptionBean.setType("only_throws");
                        node1=node.getParent();
                        while (!(node1 instanceof TryStatement
                                ||node1 instanceof ForStatement
                                ||node1 instanceof WhileStatement
                                ||node1 instanceof DoStatement
                                ||node1 instanceof SwitchStatement
                                ||node1 instanceof ThrowStatement
                                ||node1 instanceof IfStatement
                                ||node1 instanceof MethodDeclaration)){
                            node1=node1.getParent();
                        }
                    }
                    if(node1 instanceof ForStatement){
                        exceptionBean.setHasForStat(true);
                    }else{
                        exceptionBean.setHasForStat(false);
                    }

                    exceptionBean.setPackages(methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName());
                    //判断是不是有注释
                    if(methodDeclaration.getJavadoc()!=null){
                        exceptionBean.setMethodComment(methodDeclaration.getJavadoc().toString());
                    }

                    exceptionBeanList.add(exceptionBean);
                }


            }
        }

        super.endVisit(node);
    }

    /**
     * 遍历改方法里面所有的new 对象的语句
     *  @param node
     */
    @Override
    public void endVisit(ClassInstanceCreation node){
        IMethodBinding method = node.resolveConstructorBinding();
        if (method != null) {
            ITypeBinding exceptionTypes[] = method.getExceptionTypes();
            if (exceptionTypes.length > 0) {
                for (ITypeBinding iTypeBinding : exceptionTypes) {
                    String thrown=iTypeBinding.getPackage()+"."+iTypeBinding.getName();

                    boolean hasType = false;
                    ExceptionBean exceptionBean = new ExceptionBean();
                    IJavaElement iJavaElement=iTypeBinding.getJavaElement();
                    IType iType= (IType) iJavaElement;
                    if(iType!=null) {
                        try {
                            if (iType.getAttachedJavadoc(null) != null) {
                                exceptionBean.setExceptionComment(iType.getAttachedJavadoc(null));
                            }
                        } catch (JavaModelException e) {
                            e.printStackTrace();
                        }
                    }

                    exceptionBean.setThrown(thrown);
                    exceptionBean.setMethod(node.toString());
                    exceptionBean.setBlock(methodDeclaration.toString());
                    ASTNode node1 = node.getParent();
                    //判断离这个语句最近的语句的类型
                    while (!(node1 instanceof TryStatement
                            || node1 instanceof MethodDeclaration)) {
                        node1 = node1.getParent();
                    }
                    //如果是包含在try catch块中则统计最近的catch块里的内容
                    if (node1 instanceof TryStatement) {
                        List catchCauses = ((TryStatement) node1).catchClauses();
                        for (Object catchClause : catchCauses) {
                            CatchClause catchClause1= (CatchClause) catchClause;
                            if (catchClause.toString().contains(thrown)) {
                                hasType = true;
                                exceptionBean.setCatched(catchClause.toString());
                                if (catchClause1.getBody().toString().contains("throw new")) {
                                    exceptionBean.setType("Rethrow");
                                } else if (catchClause1.getBody()==null||
                                        catchClause1.getBody().toString().contains("log") ||
                                        catchClause.toString().contains("Log")
                                        ||StringUtil.replaceBlank(catchClause1.getBody().toString()).equals("{}")) {
                                    exceptionBean.setType("Ignore_Log");
                                } else {

                                    exceptionBean.setType("Recover");
                                }
                            }
                        }
                        node1 = node1.getParent();
                        while (!(node1 instanceof TryStatement
                                || node1 instanceof ForStatement
                                || node1 instanceof WhileStatement
                                || node1 instanceof DoStatement
                                || node1 instanceof SwitchStatement
                                || node1 instanceof ThrowStatement
                                || node1 instanceof IfStatement
                                || node1 instanceof MethodDeclaration)) {
                            node1 = node1.getParent();
                        }
                    }

                    if (!hasType) {
                        exceptionBean.setType("only_throws");
                        node1 = node.getParent();
                        while (!(node1 instanceof TryStatement
                                || node1 instanceof ForStatement
                                || node1 instanceof WhileStatement
                                || node1 instanceof DoStatement
                                || node1 instanceof SwitchStatement
                                || node1 instanceof ThrowStatement
                                || node1 instanceof IfStatement
                                || node1 instanceof MethodDeclaration)) {
                            node1 = node1.getParent();
                        }

                    }

                    if(node1 instanceof ForStatement){
                        exceptionBean.setHasForStat(true);
                    }else{
                        exceptionBean.setHasForStat(false);
                    }

                    exceptionBean.setPackages(methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName());
                    //判断是不是有注释
                    if (methodDeclaration.getJavadoc() != null) {
                        exceptionBean.setMethodComment(methodDeclaration.getJavadoc().toString());
                    }

                    exceptionBeanList.add(exceptionBean);
                }

            }
        }
    }
}
