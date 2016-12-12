package org.pine.plugin.integration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
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

abstract public class PineIntegrationTestCase extends LightPlatformCodeInsightFixtureTestCase {

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

            try {
                addLibrary(module, model, getJarFileForClass(GroovyObject.class));
                addLibrary(module, model, getJarFileForClass(SpecRunner.class));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void addLibrary (Module module, ModifiableRootModel model, File jarFile) {
        PsiTestUtil.addLibrary(module, model, jarFile.getName(), jarFile.getParent(), jarFile.getName());
    }

    private File getJarFileForClass(Class sourceClass) throws URISyntaxException {
        return new File(sourceClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }

}
