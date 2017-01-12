package org.pine.plugin;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.gradle.execution.test.runner.GradleTestRunConfigurationProducer;
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType;
import org.jetbrains.plugins.gradle.util.GradleConstants;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.pine.plugin.behavior.BehaviorDescription;
import org.pine.plugin.visitor.FeatureSpecVisitor;
import org.pine.plugin.visitor.JourneySpecVisitor;
import org.pine.plugin.visitor.SpecVisitor;
import org.pine.plugin.walker.SpecMethodEnumerator;
import org.pine.plugin.walker.SpecWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PineSpecRunConfigurationProducer extends GradleTestRunConfigurationProducer {

    private static final String SPEC_CLASS_NAME = "org.pine.Spec";
    private static final String JOURNEY_SPEC_CLASS_NAME = "org.pine.JourneySpec";

    public PineSpecRunConfigurationProducer() {
        super(GradleExternalTaskConfigurationType.getInstance());
    }

    public PineSpecRunConfigurationProducer(ConfigurationType configurationType) {
        super(configurationType);
    }

    @Override
    protected boolean doSetupConfigurationFromContext(ExternalSystemRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        PsiClass sourceClass = findClassForElement(sourceElement.get());

        if (sourceClass == null) {
            return false;
        }

        String specType = findSpecType(sourceClass);
        if (specType == null) {
            return false;
        }

        SpecVisitor visitor = getSpecVisitor(specType, sourceClass);

        configureRunConfiguration(configuration, context.getModule(), visitor, sourceElement.get());

        return true;
    }

    @Override
    protected boolean doIsConfigurationFromContext(ExternalSystemRunConfiguration configuration, ConfigurationContext context) {
        PsiElement sourceElement = context.getPsiLocation();

        PsiClass sourceClass = findClassForElement(sourceElement);

        if (sourceClass == null) {
            return false;
        }

        String specType = findSpecType(sourceClass);
        if (specType == null) {
            return false;
        }

        SpecVisitor visitor = getSpecVisitor(specType, sourceClass);

        BehaviorDescription behaviorDescription = getBehaviorDescription(visitor, sourceElement);

        return behaviorDescription.getQualifiedName().equals(configuration.getName());
    }

    protected void configureRunConfiguration (ExternalSystemRunConfiguration configuration, Module module, SpecVisitor visitor, PsiElement sourceElement) {
        BehaviorDescription behaviorDescription = getBehaviorDescription(visitor, sourceElement);

        configuration.setName(behaviorDescription.getQualifiedName());
        configuration.getSettings().setExternalProjectPath(resolveProjectPath(module));
        configuration.getSettings().setTaskNames(getTasksFromGradleRunTestConfigurationProducer(module));
        configuration.getSettings().setScriptParameters("--tests \"" + behaviorDescription.getQualifiedName() + "\"");
    }

    protected List<String> getTasksFromGradleRunTestConfigurationProducer(Module module) {
        return getTasksToRun(module);
    }

    private SpecVisitor getSpecVisitor (String specType, PsiClass specClass) {
        if (JOURNEY_SPEC_CLASS_NAME.equals(specType)) {
            return new JourneySpecVisitor(specClass);
        }

        return new FeatureSpecVisitor(specClass);
    }

    private BehaviorDescription getBehaviorDescription(SpecVisitor visitor, PsiElement sourceElement) {
        SpecWalker specWalker = new SpecWalker(new SpecMethodEnumerator(sourceElement));
        specWalker.accept(visitor);

        return visitor.getBehaviorDescription();
    }

    private PsiClass findClassForElement(PsiElement sourceElement) {
        GroovyFile sourceFile = PsiTreeUtil.getParentOfType(sourceElement, GroovyFile.class);

        if (sourceFile != null && sourceFile.isScript()) {
            return sourceFile.getScriptClass();
        }

        return PsiTreeUtil.getParentOfType(sourceElement, PsiClass.class);
    }

    private String findSpecType(PsiClass sourceClass) {
        PsiClass ancestorClass = sourceClass;
        while (ancestorClass != null) {
            String specClass = getSpecType(ancestorClass);
            if (specClass != null) {
                return specClass;
            } else {
                ancestorClass = ancestorClass.getSuperClass();
            }
        }

        return null;
    }

    private String getSpecType(PsiClass sourceClass) {
        return Arrays.stream(sourceClass.getInterfaces())
                .filter(c -> isSpecType(c.getQualifiedName()))
                .findFirst()
                .map(c -> c.getQualifiedName())
                .orElse(null);
    }

    private boolean isSpecType(String interfaceType) {
        return interfaceType.equals(SPEC_CLASS_NAME) ||
                interfaceType.equals(JOURNEY_SPEC_CLASS_NAME);
    }

}
