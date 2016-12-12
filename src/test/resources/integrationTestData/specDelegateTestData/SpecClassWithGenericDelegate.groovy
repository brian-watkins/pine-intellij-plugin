package org.pine.plugin.test

import org.junit.runner.RunWith
import org.pine.Spec
import org.pine.SpecRunner
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate

@RunWith(SpecRunner)
class FunSpec implements Spec {

    @SpecDelegate
    MagicDelegate<String, Integer> magicDelegate = new MagicDelegate()

    @Describe("MyFunDelegateSpec")
    def spec() {

        it 'runs a spec using the delegate', {
            assert doMa<caret>gic("happy", 64) == "something fun!"
        }

    }

}

class MagicDelegate<T, S> {
    public String doMagic (T arg1, S arg2) {
        return "some number"
    }
}

