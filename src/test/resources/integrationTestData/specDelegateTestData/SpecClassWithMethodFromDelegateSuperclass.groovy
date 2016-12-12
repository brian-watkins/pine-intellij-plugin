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
            assert superMag<caret>icMethod() == "something fun!"
        }

    }

}

class MagicDelegate extends SuperMagic {
    def someMagic () {
        return "Stuff"
    }
}

class SuperMagic {
    def superMagicMethod() {
        return "Super magic!"
    }
}

