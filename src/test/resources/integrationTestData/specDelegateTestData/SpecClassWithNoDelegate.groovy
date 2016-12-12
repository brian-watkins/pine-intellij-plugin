package org.pine.plugin.test

import org.junit.runner.RunWith
import org.pine.Spec
import org.pine.SpecRunner
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate

@RunWith(SpecRunner)
class FunSpec implements Spec {

    String someField = "someField"

    @Describe("MyFunDelegateSpec")
    def spec() {

        it 'runs a spec using the delegate', {
            assert doMa<caret>gic() == "something fun!"
        }

    }

    def doMagic() {
        return "magic"
    }

}

