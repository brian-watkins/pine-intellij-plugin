package org.pine.plugin.behavior;

import com.intellij.psi.PsiClass;

public class JourneySpecDescription implements BehaviorDescription {
    private String journeyName;
    private PsiClass journeySpecClass;

    public JourneySpecDescription (PsiClass journeySpecClass) {
        this.journeySpecClass = journeySpecClass;
    }

    @Override
    public String getQualifiedName() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(journeySpecClass.getQualifiedName()).append(".");
        buffer.append(journeyName);

        return buffer.toString();
    }

    public void setJourneyName(String journeyName) {
        this.journeyName = journeyName;
    }
}
