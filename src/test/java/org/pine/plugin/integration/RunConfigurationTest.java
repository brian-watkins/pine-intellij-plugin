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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.settings.GradleSystemRunningSettings;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RunConfigurationTest extends LightPlatformCodeInsightFixtureTestCase {

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

    @Override
    protected String getTestDataPath() {
        return new File("src/test/resources/integrationTestData").getAbsolutePath();
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new PineTestProjectDescriptor();
    }

    class PineTestProjectDescriptor extends DefaultLightProjectDescriptor {

        @Override
        public Sdk getSdk() {
            return IdeaTestUtil.getMockJdk18();
        }

        @Override
        public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
            super.configureModule(module, model, contentEntry);

            contentEntry.addSourceFolder("temp:///src/test", true);

            GradleSystemRunningSettings.getInstance().setPreferredTestRunner(GradleSystemRunningSettings.PreferredTestRunner.GRADLE_TEST_RUNNER);

            PsiTestUtil.addLibrary(module, model, "pine-1.0-SNAPSHOT.jar", "/Users/bwatkins/work/pine-intellij-plugin/lib", "pine-1.0-SNAPSHOT.jar");
            PsiTestUtil.addLibrary(module, model, "groovy-all-2.4.6.jar", "/Users/bwatkins/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/2016.2/585b4f969e1ca713c4eb3e9dbdb1f2cdf6a85172/ideaIC-2016.2/lib", "groovy-all-2.4.6.jar");
        }
    }

    private RunnerAndConfigurationSettings produceRunnerAndConfigurationSettingsFor(String fixtureFile) {
        VirtualFile file = myFixture.copyFileToProject(fixtureFile, "/test/groovy/org/pine/plugin/test/" + fixtureFile);
        myFixture.configureFromExistingVirtualFile(file);

        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        ConfigurationContext configurationContext = new ConfigurationContext(elementAtCaret);

        return configurationContext.getConfiguration();
    }

    private ExternalSystemRunConfiguration produceRunConfigurationFor(String fixtureFile) {
        RunnerAndConfigurationSettings settings = produceRunnerAndConfigurationSettingsFor(fixtureFile);

        if (settings != null) {
            return (ExternalSystemRunConfiguration)settings.getConfiguration();
        }

        return null;
    }

    private ExternalSystemRunConfiguration findExistingRunConfigurationFor(String fixtureFile) {
        VirtualFile file = myFixture.copyFileToProject(fixtureFile, "/test/groovy/org/pine/plugin/test/" + fixtureFile);
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
