package org.pine.plugin.test

import groovy.transform.BaseScript
import org.pine.script.JourneySpecScript

@BaseScript JourneySpecScript spec

describe 'A journey through the app with whens', {

    it 'does one thing', {
        assert 1 == 1
    }

    when 'something happens', {

        it 'does another thing', {
            assert 2 == 2
        }

        when 'another thing happens', {

            it 'finally does something else', {
                asser<caret>t 3 == 3
            }

        }

        when 'that one thing happens', {

            it 'does it', {
                assert 4 == 4
            }

        }

    }

    when 'other things happen', {

        it 'does things', {
            assert 1 == 1
        }

    }

}
