package integrationTestData.specDelegateTestData

trait MyTrait {

    def someMethod () {
        return "something"
    }

    def someOtherMethod() {
        return someMe<caret>thod()
    }

}