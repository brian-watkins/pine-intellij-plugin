package org.pine.plugin.test;

import org.junit.Test;

class NotASpec {

    @Test
    def itRunsATest() {
        <caret>assert 1 == 1
    }

}