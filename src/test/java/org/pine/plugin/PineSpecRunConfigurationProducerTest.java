package org.pine.plugin;

import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType;
import org.junit.Before;
import org.junit.Test;
import org.pine.plugin.behavior.BehaviorDescription;
import org.pine.plugin.visitor.SpecVisitor;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class PineSpecRunConfigurationProducerTest {

    PineSpecRunConfigurationProducer subject;
    Module mockModule = mock(Module.class);
    ExternalSystemRunConfiguration mockRunConfiguration = mock(ExternalSystemRunConfiguration.class);
    SpecVisitor mockSpecVisitor = mock(SpecVisitor.class);
    PsiElement mockPsiElement = mock(PsiElement.class);
    ExternalSystemTaskExecutionSettings mockSettings = mock(ExternalSystemTaskExecutionSettings.class);
    List<String> expectedTasks = Arrays.asList("testTask1", "testTask2", "testTask3");
    String expectedProjectPath = "projectPath";

    @Before
    public void setup() {
        subject = new TestablePineSpecRunConfigurationProducer();

        when(mockRunConfiguration.getSettings()).thenReturn(mockSettings);

        BehaviorDescription mockBehaviorDescription = mock(BehaviorDescription.class);
        when(mockSpecVisitor.getBehaviorDescription()).thenReturn(mockBehaviorDescription);
    }

    @Test
    public void testGetsTasksFromGradleTestRunConfigurationProducer() {
        subject.configureRunConfiguration(mockRunConfiguration, mockModule, mockSpecVisitor, mockPsiElement);

        verify(mockSettings).setTaskNames(expectedTasks);
    }

    @Test
    public void testGetsProjectPathFromGradleTestRunConfigurationProducer() {
        subject.configureRunConfiguration(mockRunConfiguration, mockModule, mockSpecVisitor, mockPsiElement);

        verify(mockSettings).setExternalProjectPath(expectedProjectPath);
    }

    class TestablePineSpecRunConfigurationProducer extends PineSpecRunConfigurationProducer {
        public TestablePineSpecRunConfigurationProducer() {
            super(new GradleExternalTaskConfigurationType());
        }

        @Override
        protected List<String> getTasksFromGradleRunTestConfigurationProducer(Module module) {
            return expectedTasks;
        }

        @Nullable
        @Override
        protected String resolveProjectPath(@NotNull Module module) {
            return expectedProjectPath;
        }
    }

}
