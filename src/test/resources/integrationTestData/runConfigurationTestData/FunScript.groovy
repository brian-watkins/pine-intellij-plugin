package org.pine.plugin.test

import groovy.transform.BaseScript
import org.pine.script.SpecScript

@BaseScript SpecScript spec

describe 'MySystem', {

    when 'things are the case', {
        when 'the time is right', {
            it 'ru<caret>ns a spec', {
                assert 1 == 0
            }

        }

        it 'also does something else', {
            assert 1 == 1
        }
    }

}
