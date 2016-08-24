package org.pine.plugin.unit;

import org.junit.Before;
import org.junit.Test;
import org.pine.plugin.*;

import static org.mockito.Mockito.*;

public class SpecWalkerTest {

    private SpecVisitor mockSpecVisitor = mock(SpecVisitor.class);
    private SpecMethodEnumerator specMethodEnumerator = mock(SpecMethodEnumerator.class);
    private SpecWalker specWalker;

    @Before
    public void setup() {
        specWalker = new SpecWalker(mockSpecVisitor);
    }

    @Test
    public void itNotifiesVisitWhenFindingIt () {
        SpecMethod itMethod = createMethod(SpecMethodType.IT, "does stuff");

        when(specMethodEnumerator.nextSpecMethod()).thenReturn(itMethod).thenReturn(null);

        specWalker.walkSpecWithEnumerator(specMethodEnumerator);

        verify(mockSpecVisitor).foundIt("does stuff");
    }

    @Test
    public void itNotifiesVisitorWhenFindingWhen () {
        SpecMethod whenMethod = createMethod(SpecMethodType.WHEN, "things are the case");

        when(specMethodEnumerator.nextSpecMethod()).thenReturn(whenMethod).thenReturn(null);

        specWalker.walkSpecWithEnumerator(specMethodEnumerator);

        verify(mockSpecVisitor).foundWhen("things are the case");
    }

    @Test
    public void itNotifiesVisitorUntilNoMetodIsFound () {
        SpecMethod method1 = createMethod(SpecMethodType.IT, "does stuff");
        SpecMethod method2 = createMethod(SpecMethodType.WHEN, "things are the case");
        SpecMethod method3 = createMethod(SpecMethodType.WHEN, "other things are the case");

        when(specMethodEnumerator.nextSpecMethod()).thenReturn(method1, method2, method3, null);

        specWalker.walkSpecWithEnumerator(specMethodEnumerator);

        verify(mockSpecVisitor).foundIt("does stuff");
        verify(mockSpecVisitor).foundWhen("things are the case");
        verify(mockSpecVisitor).foundWhen("other things are the case");
        verify(mockSpecVisitor, times(1)).foundIt(any(String.class));
        verify(mockSpecVisitor, times(2)).foundWhen(any(String.class));
    }

    private SpecMethod createMethod (SpecMethodType type, String name) {
        SpecMethod method = new SpecMethod();
        method.setType(type);
        method.setName(name);

        return method;
    }
}
