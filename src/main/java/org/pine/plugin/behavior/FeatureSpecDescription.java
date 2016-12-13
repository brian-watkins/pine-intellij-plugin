package org.pine.plugin.behavior;

import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FeatureSpecDescription implements BehaviorDescription {
    private PsiClass specClass;
    private String behavior;
    private List<String> contexts = new ArrayList<>();

    public FeatureSpecDescription (PsiClass specClass) {
        this.specClass = specClass;
    }

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

    public void addContext (String context) {
        contexts.add(0, context);
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }
}
