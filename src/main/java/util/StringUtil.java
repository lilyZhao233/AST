package util;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import javax.swing.plaf.PanelUI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    public static String[] splitFirst(final String str, final String separator) {
        if (str == null || str.isEmpty() || separator.isEmpty()) {
            return new String[]{str};
        }
        final int pos = str.indexOf(separator);
        if (pos < 0) {
            return new String[]{str};
        }
        return new String[]{str.substring(0, pos),
                str.substring(pos + separator.length(), str.length())};
    }

    public static String getName(IMethodBinding mInvokeBinding, MethodInvocation mInvocation) {

        IJavaElement IJE = mInvokeBinding.getJavaElement();
        if (IJE != null) {
            IMethod IM = (IMethod) IJE;
            if (IM != null) {
                String MethodFullName = IM.getDeclaringType().getFullyQualifiedName().toString().trim() + "." + mInvocation.getName().toString();
                return MethodFullName;
            }
        }
        return mInvocation.getName().toString();
    }
}
