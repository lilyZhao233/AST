package visitors;

import bean.ExceptionBean;
import org.eclipse.jdt.core.dom.*;
import util.RootExceptionUtil;
import util.StringUtil;

import java.util.ArrayList;
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
        getExceptions(node);
        super.endVisit(node);
    }

    /**
     * 遍历改方法里面所有的new 对象的语句
     *  @param node
     */
    @Override
    public void endVisit(ClassInstanceCreation node){
        getExceptions(node);
        super.endVisit(node);
    }

    /**
     * 判断外层有没有for循环
     * @param methodInvocation
     * @return
     */

    private static boolean hasForStat(ASTNode methodInvocation) {
        boolean hasForStat=false;
        ASTNode node=methodInvocation;
        while(!(node instanceof MethodDeclaration||node==null ||node instanceof TypeDeclaration)) {
            if(node instanceof ForStatement||node instanceof WhileStatement||node instanceof DoStatement) {
                hasForStat=true;
                return hasForStat;
            }
            node =node.getParent();
        }
        return hasForStat;
    }

    /**
     * 对node进行解析获得解析后我们所要的相关属性
     * @param node
     */
    private void getExceptions(ASTNode node){
        IMethodBinding m=null;
        if (node instanceof MethodInvocation) {
            m = ((MethodInvocation) node).resolveMethodBinding();
        }else if(node instanceof ClassInstanceCreation){
            m=((ClassInstanceCreation) node).resolveConstructorBinding();
        }

        if(m!=null){
            //判断是否原方法是不是抛出异常
            ITypeBinding exceptionTypes[]= m.getExceptionTypes();
            if(exceptionTypes.length>0){
                for(ITypeBinding iTypeBinding:exceptionTypes){
                    String thrown=iTypeBinding.getPackage().getName()+"."+iTypeBinding.getName();
                    ExceptionBean exceptionBean=new ExceptionBean();
                    if(comments.containsKey(iTypeBinding.getName())){
                        exceptionBean.setExceptionComment(comments.get(iTypeBinding.getName()));
                    }
                    exceptionBean.setThrown(thrown);
                    System.out.println(m.getDeclaringClass().getPackage().getName()+"."+m.getName());
                    exceptionBean.setMethod(m.getDeclaringClass().getPackage().getName()+"."+m.getName());
                    exceptionBean.setBlock(methodDeclaration.toString());
                    exceptionBean.setHasForStat(hasForStat(node));
                    exceptionBean.setPackages(methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName());
                    //判断是不是有注释
                    if(methodDeclaration.getJavadoc()!=null){
                        exceptionBean.setMethodComment(methodDeclaration.getJavadoc().toString());
                    }
                    List<String> eStrings = new ArrayList<String>();
                    if (RootExceptionUtil.isOriginal(iTypeBinding.getName().toString())) {
                        exceptionBean.setOrigin(true);
                    }else {
                        exceptionBean.setOrigin(false);
                    }
                    while (!iTypeBinding.getName().toString().equals("Exception")&&
                            !iTypeBinding.getName().toString().equals("Throwable")) {
                        if ( iTypeBinding.getSuperclass()!=null) {
                            iTypeBinding = iTypeBinding.getSuperclass();
                            eStrings.add(iTypeBinding.getName().toString());
                            continue;
                        }
                        break;
                    }
                    exceptionBean.setParents(eStrings);
                    exceptionBeanList.add(exceptionBean);
                }
            }
        }
    }
}
