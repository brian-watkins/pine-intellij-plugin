package org.pine.plugin.visitor;

import org.pine.plugin.behavior.BehaviorDescription;

public interface SpecVisitor {
    void foundIt(String name);
    void foundWhen(String name);
    void foundDescribe(String name);

    BehaviorDescription getBehaviorDescription();
}
