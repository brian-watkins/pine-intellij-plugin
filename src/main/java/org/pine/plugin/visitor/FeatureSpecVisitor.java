package org.pine.plugin.visitor;

import com.intellij.psi.PsiClass;
import org.pine.plugin.behavior.BehaviorDescription;
import org.pine.plugin.behavior.FeatureSpecDescription;

public class FeatureSpecVisitor implements SpecVisitor {

    private FeatureSpecDescription description;

    public FeatureSpecVisitor (PsiClass specClass) {
        this.description = new FeatureSpecDescription(specClass);
    }

    @Override
    public void foundIt(String name) {
        description.setBehavior(name);
    }

    @Override
    public void foundWhen(String name) {
        description.addContext(name);
    }

    @Override
    public void foundDescribe(String name) { }

    @Override
    public BehaviorDescription getBehaviorDescription() {
        return this.description;
    }
}
