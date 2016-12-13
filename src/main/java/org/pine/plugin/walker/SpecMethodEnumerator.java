package org.pine.plugin.walker;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall;

public class SpecMethodEnumerator {

    private PsiElement currentElement;

    public SpecMethodEnumerator(PsiElement element) {
        this.currentElement = element;
    }

    public SpecMethod nextSpecMethod() {
        GrMethodCall methodCall;
        while ((methodCall = PsiTreeUtil.getParentOfType(currentElement, GrMethodCall.class)) != null) {
            currentElement = methodCall;

            SpecMethodType methodType = getMethodType(methodCall);
            if (methodType != null) {
                return getSpecMethod(methodType, getMethodArgument(methodCall));
            }
        }

        return null;
    }

    private SpecMethod getSpecMethod (SpecMethodType type, String name) {
        SpecMethod specMethod = new SpecMethod();
        specMethod.setType(type);
        specMethod.setName(name);
        return specMethod;
    }

    private SpecMethodType getMethodType (GrMethodCall methodCall) {
        String methodName = methodCall.getInvokedExpression().getText();

        if (methodName.equals("it")) {
            return SpecMethodType.IT;
        } else if (methodName.equals("when")) {
            return SpecMethodType.WHEN;
        } else if (methodName.equals("describe")) {
            return SpecMethodType.DESCRIBE;
        }

        return null;
    }

    private String getMethodArgument (GrMethodCall methodCall) {
        return methodCall.getArgumentList().getAllArguments()[0].getText().replaceAll("[\'\"]", "");
    }
}
