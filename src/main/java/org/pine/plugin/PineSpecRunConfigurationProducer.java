package org.pine.plugin;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.gradle.execution.test.runner.GradleTestRunConfigurationProducer;
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.util.Arrays;

public class PineSpecRunConfigurationProducer extends GradleTestRunConfigurationProducer {

    public PineSpecRunConfigurationProducer() {
        super(GradleExternalTaskConfigurationType.getInstance());
    }

    @Override
    protected boolean doSetupConfigurationFromContext(ExternalSystemRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        PsiClass sourceClass = findClassForElement(sourceElement.get());

        if (sourceClass == null) {
            return false;
        }

        if (!implementsSpec(sourceClass)) {
            return false;
        }

        configureRunConfiguration(configuration, context.getModule(), sourceClass, sourceElement.get());

        return true;
    }

    @Override
    protected boolean doIsConfigurationFromContext(ExternalSystemRunConfiguration configuration, ConfigurationContext context) {
        PsiElement sourceElement = context.getPsiLocation();

        PsiClass sourceClass = findClassForElement(sourceElement);

        if (sourceClass == null) {
            return false;
        }

        if (!implementsSpec(sourceClass)) {
            return false;
        }

        BehaviorDescription behaviorDescription = getBehaviorDescription(sourceClass, sourceElement);

        return behaviorDescription.getQualifiedName().equals(configuration.getName());
    }

    private void configureRunConfiguration (ExternalSystemRunConfiguration configuration, Module module, PsiClass sourceClass, PsiElement sourceElement) {
        BehaviorDescription behaviorDescription = getBehaviorDescription(sourceClass, sourceElement);

        configuration.setName(behaviorDescription.getQualifiedName());
        configuration.getSettings().setExternalProjectPath(module.getProject().getBasePath());
        configuration.getSettings().setTaskNames(Arrays.asList(":cleanTest", ":test"));
        configuration.getSettings().setScriptParameters("--tests \"" + behaviorDescription.getQualifiedName() + "\"");
    }

    private BehaviorDescription getBehaviorDescription(PsiClass sourceClass, PsiElement sourceElement) {
        BehaviorDescription behaviorDescription = new BehaviorDescription();
        behaviorDescription.setSpecClass(sourceClass);

        SpecWalker specWalker = new SpecWalker(behaviorDescription);
        specWalker.walkSpecWithEnumerator(new SpecMethodEnumerator(sourceElement));

        return behaviorDescription;
    }

    private PsiClass findClassForElement(PsiElement sourceElement) {
        GroovyFile sourceFile = PsiTreeUtil.getParentOfType(sourceElement, GroovyFile.class);

        if (sourceFile != null && sourceFile.isScript()) {
            return sourceFile.getScriptClass();
        }

        return PsiTreeUtil.getParentOfType(sourceElement, PsiClass.class);
    }

    private boolean implementsSpec (PsiClass sourceClass) {
        PsiClass ancestorClass = sourceClass;
        while (ancestorClass != null) {
            if (isSpecClass(ancestorClass)) {
                return true;
            } else {
                ancestorClass = ancestorClass.getSuperClass();
            }
        }

        return false;
    }

    private boolean isSpecClass(PsiClass sourceClass) {
        return Arrays.stream(sourceClass.getInterfaces())
                .filter(c -> c.getQualifiedName().equals("org.pine.Spec"))
                .findFirst().isPresent();
    }

}
