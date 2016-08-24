package org.pine.plugin;

public class SpecWalker {

    private SpecVisitor specVisitor;

    public SpecWalker (SpecVisitor visitor) {
        this.specVisitor = visitor;
    }

    public void walkSpecWithEnumerator(SpecMethodEnumerator enumerator) {
        SpecMethod method;
        while ((method = enumerator.nextSpecMethod()) != null) {
            notifyVisitor(method);
        }
    }

    private void notifyVisitor (SpecMethod method) {
        switch (method.getType()) {
            case IT:
                this.specVisitor.foundIt(method.getName());
                break;
            case WHEN:
                this.specVisitor.foundWhen(method.getName());
                break;
        }
    }
}
