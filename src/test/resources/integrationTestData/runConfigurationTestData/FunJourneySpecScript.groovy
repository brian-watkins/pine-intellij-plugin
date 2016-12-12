package org.pine.plugin.test

import groovy.transform.BaseScript
import org.pine.script.JourneySpecScript

@BaseScript JourneySpecScript spec

describe 'A journey through the app', {

    it 'does one thing', {
        assert 1 == 1
    }

    it 'does another thing', {
        assert 2 <caret>== 2
    }

    it 'finally does something else', {
        assert 3 == 3
    }

}
