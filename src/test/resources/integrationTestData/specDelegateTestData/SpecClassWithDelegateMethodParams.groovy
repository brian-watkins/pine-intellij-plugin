package org.pine.plugin.test

import org.junit.runner.RunWith
import org.pine.Spec
import org.pine.SpecRunner
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate

@RunWith(SpecRunner)
class FunSpec implements Spec {

    @SpecDelegate
    MagicDelegate magicDelegate = new MagicDelegate()

    @Describe("MyFunDelegateSpec")
    def spec() {

        it 'runs a spec using the delegate', {
            assert doMa<caret>gic(37) == "something fun!"
        }

    }

}

class MagicDelegate {
    def doMagic(String word) {
        return "magic"
    }

    def doMagic(Integer number) {
        return number
    }
}

