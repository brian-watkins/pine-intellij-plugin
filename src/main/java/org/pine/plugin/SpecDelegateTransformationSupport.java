package org.pine.plugin;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.*;
import icons.JetgroovyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.jetbrains.plugins.groovy.lang.psi.GroovyRecursiveElementVisitor;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariableDeclaration;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrTypeDefinition;
import org.jetbrains.plugins.groovy.lang.psi.impl.PsiImplUtil;
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GroovyScriptClass;
import org.jetbrains.plugins.groovy.lang.psi.util.GroovyCommonClassNames;
import org.jetbrains.plugins.groovy.lang.resolve.ast.DelegatedMethod;
import org.jetbrains.plugins.groovy.lang.resolve.processors.GrScopeProcessorWithHints;
import org.jetbrains.plugins.groovy.transformations.AstTransformationSupport;
import org.jetbrains.plugins.groovy.transformations.TransformationContext;

import java.util.*;

public class SpecDelegateTransformationSupport implements AstTransformationSupport {

    public static final String SPECDELEGATE_ANNOTATION = "org.pine.annotation.SpecDelegate";

    @Override
    public void applyTransformation(@NotNull TransformationContext transformationContext) {
        GrTypeDefinition codeClassDefinition = transformationContext.getCodeClass();

        if (codeClassDefinition instanceof GroovyScriptClass) {
            applyTransformation(transformationContext, (GroovyScriptClass) codeClassDefinition);
        }
        else if (codeClassDefinition instanceof GrClassDefinition) {
            applyTransformation(transformationContext, (GrClassDefinition) codeClassDefinition);
        }
    }

    private void applyTransformation(TransformationContext context, GrClassDefinition groovyClass) {
        for (GrField field : groovyClass.getCodeFields()) {
            final PsiAnnotation annotation = PsiImplUtil.getAnnotation(field, SPECDELEGATE_ANNOTATION);
            if (annotation == null) continue;

            applyTransformation(context, field.getDeclaredType());
        }
    }

    private void applyTransformation(TransformationContext context, GroovyScriptClass scriptClass) {
        scriptClass.getContainingFile().accept(new GroovyScriptSpecDelegateVisitor(context));
    }

    private void applyTransformation(@NotNull TransformationContext context, PsiType type) {
        final PsiClassType.ClassResolveResult delegateResult = ((PsiClassType) type).resolveGenerics();
        final PsiClass delegate = delegateResult.getElement();
        if (delegate == null) return;

        SpecDelegateProcessor processor = new SpecDelegateProcessor(context);
        delegate.processDeclarations(
                processor,
                ResolveState.initial().put(PsiSubstitutor.KEY, delegateResult.getSubstitutor()),
                null,
                context.getCodeClass()
        );
    }

    private class GroovyScriptSpecDelegateVisitor extends GroovyRecursiveElementVisitor {

        private TransformationContext context;

        public GroovyScriptSpecDelegateVisitor (TransformationContext context) {
            this.context = context;
        }

        @Override
        public void visitVariableDeclaration(@NotNull GrVariableDeclaration element) {
            if (element.getModifierList().findAnnotation(GroovyCommonClassNames.GROOVY_TRANSFORM_FIELD) != null) {
                PsiAnnotation annotation = PsiImplUtil.getAnnotation(element, "org.pine.annotation.SpecDelegate");
                if (annotation == null) return;

                PsiType type = element.getTypeElementGroovy().getType();

                applyTransformation(this.context, type);
            }
            super.visitVariableDeclaration(element);
        }

    }

    private static class SpecDelegateProcessor extends GrScopeProcessorWithHints {

        private final TransformationContext context;

        public SpecDelegateProcessor(TransformationContext context) {
            super(null, EnumSet.of(DeclarationKind.METHOD));
            this.context = context;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
            if (!(element instanceof PsiMethod)) return true;

            PsiMethod method = (PsiMethod) element;

            PsiSubstitutor substitutor = state.get(PsiSubstitutor.KEY);
            if (substitutor == null) substitutor = PsiSubstitutor.EMPTY;

            context.addMethod(createDelegationMethod(method, substitutor));
            return true;
        }

        @NotNull
        protected PsiMethod createDelegationMethod(@NotNull PsiMethod method, @NotNull PsiSubstitutor substitutor) {
            final LightMethodBuilder builder = new LightMethodBuilder(context.getManager(), GroovyLanguage.INSTANCE, method.getName());
            builder.setMethodReturnType(substitutor.substitute(method.getReturnType()));
            builder.setContainingClass(context.getCodeClass());
            builder.setNavigationElement(method);
            builder.addModifier(PsiModifier.PUBLIC);
            builder.setBaseIcon(JetgroovyIcons.Groovy.Method);
            setTypeParameters(builder, method);
            setSubstitutedMethodArguments(builder, method, substitutor);

            return new DelegatedMethod(builder, method);
        }

        private void setTypeParameters(LightMethodBuilder builder, PsiMethod method) {
            PsiTypeParameter[] typeParameters = method.getTypeParameters();
            for (PsiTypeParameter typeParameter : typeParameters) {
                builder.addTypeParameter(typeParameter);
            }
        }

        private void setSubstitutedMethodArguments(LightMethodBuilder builder, PsiMethod method, PsiSubstitutor substitutor) {
            PsiParameter[] methodArguments = method.getParameterList().getParameters();
            for (PsiParameter argument : methodArguments) {
                PsiType type = substitutor.substitute(argument.getType());
                LightParameter lightArgument = new LightParameter(argument.getName(), type, builder, JavaLanguage.INSTANCE);
                builder.addParameter(lightArgument);
            }
        }
    }
}
