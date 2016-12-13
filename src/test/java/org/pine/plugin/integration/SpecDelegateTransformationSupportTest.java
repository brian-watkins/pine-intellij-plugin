package org.pine.plugin.integration;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression;
import org.jetbrains.plugins.groovy.lang.resolve.ast.DelegatedMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecDelegateTransformationSupportTest extends PineIntegrationTestCase {

    public void testResolvesWithFieldButNoDelegate() {
        configureWithFile("SpecClassWithNoDelegate.groovy");
        assertReferenceResolves();
    }

    public void testDoesNotResolveWhenDelegateIsNotOnAField() {
        configureWithFile("SpecClassWithDelegateNotOnField.groovy");
        assertReferenceDoesNotResolve();
    }

    public void testDoesNotResolveWhenDelegateIsUnknown() {
        configureWithFile("SpecClassWithUnknownDelegate.groovy");
        assertReferenceDoesNotResolve();
    }

    public void testResolvesDelegateMethod() {
        configureWithFile("SpecClassWithDelegate.groovy");
        assertReferenceResolvesDelegatedMethod("MagicDelegate", "doMagic");
    }

    public void testResolvesMethodFromDelegateSuperclass() {
        configureWithFile("SpecClassWithMethodFromDelegateSuperclass.groovy");
        assertReferenceResolvesDelegatedMethod("SuperMagic", "superMagicMethod");
    }

    public void testResolvesMethodWithParamsFromDelegate() {
        configureWithFile("SpecClassWithDelegateMethodParams.groovy");
        assertReferenceResolvesDelegatedMethod("MagicDelegate", "doMagic", Arrays.asList("Integer"));
    }

    public void testResolvesMethodAndParametersFromGenericDelegate() {
        configureWithFile("SpecClassWithGenericDelegate.groovy");
        assertReferenceResolvesDelegatedMethod(
                "MagicDelegate",
                "doMagic",
                Arrays.asList("String", "Integer")
        );
    }

    public void testResolvesGenericMethodTypeParametersFromDelegate() {
        configureWithFile("SpecClassWithGenericDelegatedMethod.groovy");
        assertReferenceResolvesDelegatedMethod(
                "MagicDelegate",
                "doMagic",
                Arrays.asList("T", "S"),
                Arrays.asList("T", "S")
        );
    }

    public void testScriptResolvesDelegateMethod() {
        configureWithFile("SpecScriptWithDelegate.groovy");
        assertReferenceResolvesDelegatedMethod("MagicDelegate", "doSomethingMagical");
    }

    public void testTraitResolvesAsExpected() {
        configureWithFile("MyTrait.groovy");
        assertReferenceResolves();
    }


    private String specDelegateTestFixture(String fixtureFile) {
        return "/specDelegateTestData/" + fixtureFile;
    }

    private void configureWithFile(String fixtureFile) {
        String testFixture = specDelegateTestFixture(fixtureFile);
        VirtualFile file = myFixture.copyFileToProject(testFixture, "/test/groovy/org/pine/plugin/test/" + fixtureFile);
        myFixture.configureFromExistingVirtualFile(file);
    }

    private PsiElement resolvedReference() {
        GrReferenceExpression ref = (GrReferenceExpression) myFixture.getFile().findReferenceAt(myFixture.getEditor().getCaretModel().getOffset());
        return ref.advancedResolve().getElement();
    }

    private void assertReferenceDoesNotResolve() {
        assertThat(resolvedReference()).isNull();
    }

    private void assertReferenceResolves() {
        assertThat(resolvedReference()).isNotNull();
    }

    private void assertReferenceResolvesDelegatedMethod(String className, String methodName) {
        assertReferenceResolvesDelegatedMethod(className, methodName, new ArrayList<>(), new ArrayList<>());
    }

    private void assertReferenceResolvesDelegatedMethod(String className, String methodName, List<String> expectedParamTypes) {
        assertReferenceResolvesDelegatedMethod(className, methodName, new ArrayList<>(), new ArrayList<>());
    }

    private void assertReferenceResolvesDelegatedMethod(String className, String methodName, List<String> expectedParamTypes, List<String> expectedTypeParameters) {
        PsiElement resolvedReference = resolvedReference();
        assertThat(resolvedReference).isNotNull();

        DelegatedMethod method = (DelegatedMethod) resolvedReference;

        assertThat(method.getOriginInfo()).isEqualTo("delegates to " + className);
        assertThat(method.getName()).isEqualTo(methodName);

        List<PsiTypeParameter> typeParameters = Arrays.asList(method.getTypeParameters());
        List<String> typeParams = typeParameters.stream()
                .map(typeParam -> typeParam.getName())
                .collect(Collectors.toList());

        for (String typeParam : expectedTypeParameters) {
            assertThat(typeParams).contains(typeParam);
        }

        List<PsiParameter> parameters = Arrays.asList(method.getParameterList().getParameters());
        List<String> paramTypes = parameters.stream()
                .map(psiParameter -> psiParameter.getType().getCanonicalText())
                .collect(Collectors.toList());

        for (String paramType : expectedParamTypes) {
            assertThat(paramTypes).contains(paramType);
        }
    }


}
