package org.pine.plugin.test

import org.junit.runner.RunWith
import org.pine.Spec
import org.pine.SpecRunner
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate

@RunWith(SpecRunner)
class FunSpec implements Spec {

    @SpecDelegate
    SomeUnknownDelegate someField = new SomeUnknownDelegate()

    @Describe("MyFunDelegateSpec")
    def spec() {

        it 'runs a spec using the delegate', {
            assert unknown<caret>Method() == "something fun!"
        }

    }

}

