package org.pine.plugin.visitor;

import com.intellij.psi.PsiClass;
import org.pine.plugin.behavior.BehaviorDescription;
import org.pine.plugin.behavior.JourneySpecDescription;

public class JourneySpecVisitor implements SpecVisitor {

    private JourneySpecDescription description;

    public JourneySpecVisitor (PsiClass specClass) {
        this.description = new JourneySpecDescription(specClass);
    }

    public BehaviorDescription getBehaviorDescription() {
        return description;
    }

    @Override
    public void foundIt(String name) { }

    @Override
    public void foundWhen(String name) {

    }

    @Override
    public void foundDescribe(String name) {
        description.setJourneyName(name);
    }
}
