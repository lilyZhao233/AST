package bean;

public class ExceptionBean {

    private String type;

    private String thrown;

    private String catched;

    private String block;

    private String method;

    private String packages;

    private String methodComment;

    private String exceptionComment;

    private boolean hasForStat;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThrown() {
        return thrown;
    }

    public void setThrown(String thrown) {
        this.thrown = thrown;
    }

    public String getCatched() {
        return catched;
    }

    public void setCatched(String catched) {
        this.catched = catched;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public boolean isHasForStat() {
        return hasForStat;
    }

    public void setHasForStat(boolean hasForStat) {
        this.hasForStat = hasForStat;
    }

    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
    }

    public String getExceptionComment() {
        return exceptionComment;
    }

    public void setExceptionComment(String exceptionComment) {
        this.exceptionComment = exceptionComment;
    }
}
