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
            assert doSomething('hey<caret>') == 1
        }

        it 'runs another spec', {
            assert 1 == 0
        }

    }

    def doSomething () {
        return 1;
    }

}