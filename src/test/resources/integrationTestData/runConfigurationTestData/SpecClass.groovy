package org.pine.plugin.test;

import org.junit.runner.RunWith
import org.pine.Spec
import org.pine.SpecRunner
import org.pine.annotation.Describe

@RunWith(SpecRunner)
class FunSpec implements Spec {

    @Describe("MyFunSpec")
    def spec() {

        it 'runs a spec', {
            <caret>assert 1 == 1
        }

        it 'runs another spec', {
            assert 1 == 0
        }

    }

}