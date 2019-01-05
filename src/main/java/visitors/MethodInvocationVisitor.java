package visitors;

import bean.ExceptionBean;
import org.eclipse.jdt.core.dom.*;
import util.RootExceptionUtil;
import util.StringUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if(node instanceof MethodDeclaration){
            m = ((MethodDeclaration)node).resolveBinding();
        }
        else if (node instanceof MethodInvocation) {
            m = ((MethodInvocation) node).resolveMethodBinding();
        }
        else if(node instanceof ClassInstanceCreation){
            m=((ClassInstanceCreation) node).resolveConstructorBinding();
        }

        if(m!=null){
            //判断是否原方法是不是抛出异常
            ITypeBinding exceptionTypes[]= m.getExceptionTypes();
            if(exceptionTypes.length>0){
                for(ITypeBinding iTypeBinding:exceptionTypes){
                    String thrown=iTypeBinding.getPackage().getName()+"."+iTypeBinding.getName();//eg:java.io.IOException
//                    String thrown = iTypeBinding.getName();
                    boolean hasType=false;
                    ExceptionBean exceptionBean=new ExceptionBean();
                    if(comments.containsKey(iTypeBinding.getName())){
//                        String info = comments.get(iTypeBinding.getName()).toString().split("\n")[1];
//                        Pattern pattern = Pattern.compile("<[^>]+>");//去除html标签
//                        Matcher matcher = pattern.matcher(info);
//                        info = matcher.replaceAll("");
//                        info = info.replaceAll("[\\pP]"," ").toLowerCase();//去除标点符号
//
//                        exceptionBean.setExceptionComment(info);
                        exceptionBean.setExceptionComment(comments.get(iTypeBinding.getName()));
                    }
                    exceptionBean.setThrown(thrown);

                    String methodDec1 = m.getMethodDeclaration().toString();
                    String parameter1 = methodDec1.substring(methodDec1.indexOf('('),methodDec1.indexOf(')')+1);
//                    引起异常的函数信息，包括：包、类、函数名、函数参数
                    exceptionBean.setRmethod(m.getDeclaringClass().getPackage().getName()+"."+m.getDeclaringClass().getTypeDeclaration().getName()+"#"+m.getName()+parameter1);
                    exceptionBean.setRpackage(m.getDeclaringClass().getPackage().getName()+".");
                    exceptionBean.setBlock(methodDeclaration.resolveBinding().getDeclaringClass().getTypeDeclaration().getName()+"\n"+methodDeclaration.toString());
                    exceptionBean.setHasForStat(hasForStat(node));
                    exceptionBean.setPackages(methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName());
                    StringBuilder stringBuilder = new StringBuilder();
                    if(!methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName().equals(""))
                        stringBuilder.append(methodDeclaration.resolveBinding().getDeclaringClass().getPackage().getName());
                    //函数信息包括：包、类、函数名
                    if(!methodDeclaration.resolveBinding().getDeclaringClass().getTypeDeclaration().getName().equals(""))
                        stringBuilder.append("."+methodDeclaration.resolveBinding().getDeclaringClass().getTypeDeclaration().getName());
                    stringBuilder.append("#"+methodDeclaration.getName());
//                    //获得函数参数
                    String methodDec = methodDeclaration.resolveBinding().getMethodDeclaration().toString();
                    String parameter = methodDec.substring(methodDec.indexOf('('),methodDec.indexOf(')')+1);
                    exceptionBean.setMethod(stringBuilder.toString()+parameter);

//
                    ASTNode node1=node.getParent();
                    List<String> eStrings = new ArrayList<String>();
                    ITypeBinding iTypeBinding1 = iTypeBinding;
                    if (RootExceptionUtil.isOriginal(iTypeBinding1.getName().toString())) {
                        exceptionBean.setOrigin(true);
                    }else {
                        exceptionBean.setOrigin(false);
                    }
                    while (//!iTypeBinding.getName().toString().equals("Exception")&&
                            !iTypeBinding1.getName().toString().equals("Throwable")) {
                        if ( iTypeBinding1.getSuperclass()!=null) {
                            iTypeBinding1 = iTypeBinding1.getSuperclass();
                            eStrings.add(iTypeBinding1.getName().toString());
                            continue;
                        }
                        break;
                    }
                    exceptionBean.setParents(eStrings);
                    //判断离这个语句最近的语句的类型
                    while (!(node1 instanceof TryStatement ||node1 instanceof MethodDeclaration)){
                        node1=node1.getParent();
                     }
                    //如果是包含在try catch块中则统计最近的catch块里的内容
                    if(node1 instanceof TryStatement){
                        List catchClauses=((TryStatement) node1).catchClauses();
                        for (Object catchClause : catchClauses) {
                            CatchClause catchClause1= (CatchClause) catchClause;
                            if (((CatchClause) catchClause).getException().getType().toString().equals(iTypeBinding.getName().toString()) ||
                                    eStrings.contains(((CatchClause) catchClause).getException().getType().toString())) {//已修改
                                hasType = true;
                                exceptionBean.setCatched(catchClause.toString());
                                if (catchClause1.getBody().toString().contains("throw new")) {
                                    exceptionBean.setType("Rethrow");
                                } else if (catchClause1.getBody()==null
                                        || catchClause1.getBody().toString().contains("log")
                                        || catchClause1.getBody().toString().contains("LOG")
                                        || catchClause.toString().contains("Log")
                                        || StringUtil.replaceBlank(catchClause1.getBody().toString()).equals("{}")) {
                                    exceptionBean.setType("Ignore_Log");
                                } else {exceptionBean.setType("Recover");}
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
                    //判断是不是有注释
                    if(methodDeclaration.getJavadoc()!=null){
                        exceptionBean.setMethodComment(methodDeclaration.getJavadoc().toString());
                    }
                    if(!exceptionBeanList.contains(exceptionBean)) {
                        exceptionBeanList.add(exceptionBean);
                    }
                }
            }
        }
    }
}
