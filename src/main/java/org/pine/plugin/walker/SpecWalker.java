package org.pine.plugin.walker;

import org.pine.plugin.visitor.SpecVisitor;

public class SpecWalker {

    private SpecMethodEnumerator enumerator;

    public SpecWalker (SpecMethodEnumerator enumerator) {
        this.enumerator = enumerator;
    }

    public void accept(SpecVisitor specVisitor) {
        SpecMethod method;
        while ((method = enumerator.nextSpecMethod()) != null) {
            notifyVisitor(specVisitor, method);
        }
    }

    private void notifyVisitor (SpecVisitor specVisitor, SpecMethod method) {
        switch (method.getType()) {
            case IT:
                specVisitor.foundIt(method.getName());
                break;
            case WHEN:
                specVisitor.foundWhen(method.getName());
                break;
            case DESCRIBE:
                specVisitor.foundDescribe(method.getName());
        }
    }
}
