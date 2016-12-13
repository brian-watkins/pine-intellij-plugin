package org.pine.plugin.unit;

import com.intellij.psi.PsiClass;
import org.junit.Before;
import org.junit.Test;
import org.pine.plugin.behavior.FeatureSpecDescription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeatureSpecDescriptionTest {

    FeatureSpecDescription description;
    String className = "org.test.example.ExampleSpec";

    @Before
    public void setUp() {
        PsiClass mockPsiClass = mock(PsiClass.class);
        when(mockPsiClass.getQualifiedName()).thenReturn(className);

        description = new FeatureSpecDescription(mockPsiClass);
    }

    @Test
    public void itFindsTheNameOfABehaviorWhenThereIsNoClassSet () {
        FeatureSpecDescription emptyDescription = new FeatureSpecDescription(null);
        emptyDescription.setBehavior("does stuff");

        assertThat(emptyDescription.getQualifiedName()).isEqualTo("it does stuff");
    }

    @Test
    public void itFindsTheNameOfASimpleBehavior () {
        description.setBehavior("does stuff");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".it does stuff");
    }

    @Test
    public void itFindsTheNameOfABehaviorInAContext () {
        description.setBehavior("does stuff");
        description.addContext("things are the case");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when things are the case, it does stuff");
    }

    @Test
    public void itFindsTheNameOfAContext () {
        description.addContext("things are the case");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when things are the case, *");
    }

    @Test
    public void itFindsTheNameOfABehaviorInMultipleContexts() {
        description.setBehavior("does stuff");
        description.addContext("things are the case");
        description.addContext("something happened");
        description.addContext("the time is right");

        assertThat(description.getQualifiedName()).isEqualTo(className + ".when the time is right, and something happened, and things are the case, it does stuff");
    }

    @Test
    public void itFindsTheNameWhenNoBehaviorIsSpecified() {
        assertThat(description.getQualifiedName()).isEqualTo(className + ".*");
    }

}
