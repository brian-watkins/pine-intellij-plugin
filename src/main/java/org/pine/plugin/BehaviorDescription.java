package org.pine.plugin;

import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BehaviorDescription implements SpecVisitor {

    private PsiClass specClass;
    private String behavior;
    private List<String> contexts = new ArrayList<>();

    public String getQualifiedName() {
        StringBuffer buffer = new StringBuffer();
        appendClassDescription(buffer);
        appendContextDescription(buffer);
        appendBehaviorDescription(buffer);

        return buffer.toString();
    }

    private void appendClassDescription (StringBuffer buffer) {
        if (specClass != null) {
            buffer.append(specClass.getQualifiedName()).append(".");
        }
    }

    private void appendContextDescription (StringBuffer buffer) {
        if (contexts.size() > 0) {
            buffer.append("when ");
            buffer.append(StringUtils.join(contexts, ", and "));
            buffer.append(", ");
        }
    }

    private void appendBehaviorDescription (StringBuffer buffer) {
        if (behavior != null) {
            buffer.append("it ").append(behavior);
        }
        else {
            buffer.append("*");
        }
    }

    @Override
    public void foundIt(String name) {
        behavior = name;
    }

    @Override
    public void foundWhen(String name) {
        contexts.add(0, name);
    }

    public PsiClass getSpecClass() {
        return specClass;
    }

    public void setSpecClass(PsiClass specClass) {
        this.specClass = specClass;
    }
}
