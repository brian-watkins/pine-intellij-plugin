package org.pine.plugin.integration;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import groovy.lang.GroovyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.settings.GradleSystemRunningSettings;
import org.pine.SpecRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RunConfigurationTest extends PineIntegrationTestCase {

    public void testConfiguresTheRunConfigurationFromAGroovyClass() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("SpecClass.groovy");
        assertRunConfigurationIsValid(runConfiguration, "org.pine.plugin.test.FunSpec.it runs a spec");
    }

    public void testConfiguresTheRunConfigurationFromWithinALocalMethod() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("SpecClassLocalMethod.groovy");
        assertRunConfigurationIsValid(runConfiguration, "org.pine.plugin.test.FunSpec.it runs a spec");
    }

    public void testConfiguresTheRunConfigurationFromAGroovyScript() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("FunScript.groovy");
        assertRunConfigurationIsValid(runConfiguration, "org.pine.plugin.test.FunScript.when things are the case, and the time is right, it runs a spec");
    }

    public void testConfiguresTheRunConfigurationFromAJourneySpec() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("FunJourneySpecScript.groovy");
        assertRunConfigurationIsValid(runConfiguration, "org.pine.plugin.test.FunJourneySpecScript.A journey through the app");
    }

    public void testRecognizesConfiguration() {
        RunnerAndConfigurationSettings initialRunConfigSettings = produceRunnerAndConfigurationSettingsFor("FunScript.groovy");

        RunManager.getInstance(getProject()).addConfiguration(initialRunConfigSettings, true);

        ExternalSystemRunConfiguration runConfiguration = findExistingRunConfigurationFor("FunScript.groovy");
        assertRunConfigurationIsValid(runConfiguration, "org.pine.plugin.test.FunScript.when things are the case, and the time is right, it runs a spec");
    }

    public void testRecognizesConfigurationIsDifferent() {
        RunnerAndConfigurationSettings initialRunConfigSettings = produceRunnerAndConfigurationSettingsFor("FunScript.groovy");

        RunManager.getInstance(getProject()).addConfiguration(initialRunConfigSettings, true);

        ExternalSystemRunConfiguration runConfiguration = findExistingRunConfigurationFor("SpecClass.groovy");
        assertThat(runConfiguration).isNull();
    }

    public void testReturnsNullWhenClassIsNotASpec() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("NotASpecClass.groovy");
        assertThat(runConfiguration).isNull();
    }

    public void testReturnsNullWhenNoClassFound() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("NoClassInContext.groovy");
        assertThat(runConfiguration).isNull();
    }

    public void testReturnsNullWhenNotAGroovyFile() {
        ExternalSystemRunConfiguration runConfiguration = produceRunConfigurationFor("NotGroovy.java");
        assertThat(runConfiguration).isNull();
    }

    private RunnerAndConfigurationSettings produceRunnerAndConfigurationSettingsFor(String fixtureFile) {
        String testFixture = runConfigurationTestFixture(fixtureFile);
        VirtualFile file = myFixture.copyFileToProject(testFixture, "/test/groovy/org/pine/plugin/test/" + fixtureFile);
        myFixture.configureFromExistingVirtualFile(file);

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        ConfigurationContext configurationContext = new ConfigurationContext(elementAtCaret);

        return configurationContext.getConfiguration();
    }

    private String runConfigurationTestFixture(String fixtureFile) {
        return "/runConfigurationTestData/" + fixtureFile;
    }

    private ExternalSystemRunConfiguration produceRunConfigurationFor(String fixtureFile) {
        RunnerAndConfigurationSettings settings = produceRunnerAndConfigurationSettingsFor(fixtureFile);

        if (settings != null) {
            return (ExternalSystemRunConfiguration)settings.getConfiguration();
        }

        return null;
    }

    private ExternalSystemRunConfiguration findExistingRunConfigurationFor(String fixtureFile) {
        String testFixture = runConfigurationTestFixture(fixtureFile);
        VirtualFile file = myFixture.copyFileToProject(testFixture, "/test/groovy/org/pine/plugin/test/" + fixtureFile);
        myFixture.configureFromExistingVirtualFile(file);

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        ConfigurationContext configurationContext = new ConfigurationContext(elementAtCaret);

        RunnerAndConfigurationSettings settings = configurationContext.findExisting();

        if (settings != null) {
            return (ExternalSystemRunConfiguration)settings.getConfiguration();
        }

        return null;
    }

    private void assertRunConfigurationIsValid (ExternalSystemRunConfiguration runConfiguration, String expectedBehaviorDescription) {
        List<String> tasks = runConfiguration.getSettings().getTaskNames();
        assertThat(tasks).containsOnly(":test", ":cleanTest");
        assertThat(runConfiguration.getName()).isEqualTo(expectedBehaviorDescription);
        assertThat(runConfiguration.getSettings().getExternalProjectPath()).isEqualTo(getProject().getBasePath());
        assertThat(runConfiguration.getSettings().getScriptParameters()).isEqualTo("--tests \"" + expectedBehaviorDescription + "\"");
    }

}
