package org.pine.plugin.unit;

import com.intellij.psi.PsiClass;
import org.junit.Before;
import org.junit.Test;
import org.pine.plugin.BehaviorDescription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehaviorDescriptionTest {

    BehaviorDescription description;
    String className = "org.test.example.ExampleSpec";

    @Before
    public void setUp() {
        PsiClass mockPsiClass = mock(PsiClass.class);
        when(mockPsiClass.getQualifiedName()).thenReturn(className);

        description = new BehaviorDescription();
        description.setSpecClass(mockPsiClass);
    }

    @Test
    public void itFindsTheNameOfABehaviorWhenThereIsNoClassSet () {
        description.setSpecClass(null);
        description.foundIt("does stuff");

        assertThat(description.getQualifiedName()).isEqualTo("it does stuff");
    }

    @Test
    public void itFindsTheNameOfASimpleBehavior () {
        description.foundIt("does stuff");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".it does stuff");
    }

    @Test
    public void itFindsTheNameOfABehaviorInAContext () {
        description.foundIt("does stuff");
        description.foundWhen("things are the case");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when things are the case, it does stuff");
    }

    @Test
    public void itFindsTheNameOfAContext () {
        description.foundWhen("things are the case");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when things are the case, *");
    }

    @Test
    public void itFindsTheNameOfABehaviorInMultipleContexts() {
        description.foundIt("does stuff");
        description.foundWhen("things are the case");
        description.foundWhen("something happened");
        description.foundWhen("the time is right");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when the time is right, and something happened, and things are the case, it does stuff");
    }

    @Test
    public void itFindsTheNameWhenNoBehaviorIsSpecified() {
        assertThat(description.getQualifiedName()).isEqualTo(className + ".*");
    }

}
