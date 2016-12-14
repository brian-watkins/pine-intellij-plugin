package org.pine.plugin.behavior;

import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JourneySpecDescription implements BehaviorDescription {
    private String journeyName;
    private PsiClass journeySpecClass;
    private List<String> contexts = new ArrayList<>();

    public JourneySpecDescription (PsiClass journeySpecClass) {
        this.journeySpecClass = journeySpecClass;
    }

    @Override
    public String getQualifiedName() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(journeySpecClass.getQualifiedName()).append(".");
        buffer.append(journeyName);
        appendContextDescription(buffer);

        return buffer.toString();
    }

    private void appendContextDescription (StringBuffer buffer) {
        if (contexts.size() > 0) {
            buffer.append(", when ");
            buffer.append(StringUtils.join(contexts, ", and "));
            buffer.append("*");
        }
    }

    public void setJourneyName(String journeyName) {
        this.journeyName = journeyName;
    }

    public void addContext(String name) {
        this.contexts.add(0, name);
    }
}
