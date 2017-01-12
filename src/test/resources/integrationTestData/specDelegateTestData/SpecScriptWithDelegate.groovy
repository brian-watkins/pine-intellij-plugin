package org.pine.plugin.test

import groovy.transform.BaseScript
import groovy.transform.Field
import org.pine.script.SpecScript
import org.pine.annotation.SpecDelegate

@BaseScript SpecScript spec

@Field String somethingElse = "Blah"

@SpecDelegate
@Field MagicDelegate magicDelegate = new MagicDelegate()

describe 'MySystem', {

    it 'runs a spec', {
        assert doSome<caret>thingMagical() == "Magic?"
    }

}

class MagicDelegate {
    def doSomethingMagical() {
        return "MAGIC!"
    }
}