## Pine Intellij Plugin

Use this plugin to run Pine specs in Intellij based on context. 

To get started, clone the repo, then:

```
$ ./gradlew cleanIdea idea
```

### Using the Plugin

To build the plugin: 

```
$ ./gradlew clean buildPlugin
```

You'll find the plugin .zip file at `./build/distributions/`

Then, in Intellij, under Preferences, look for Plugins. Choose to install the plugin from disk and select the .zip file you just built. You'll have to restart Intellij. Also, make sure to delete any existing run configurations. 

When you're in a Pine spec, use `ctrl-shift-r` to run the behavior(s) in the context around your cursor. So, if your cursor is within an `it` block, it will run just that behavior. If your cursor is within a `when` block, it will run all behaviors in that block. Otherwise, all behaviors in the file will be run. 


### Development

To run the tests:

```
$ ./gradlew clean test
```

To run the plugin in Intellij for development purposes:

```
$ ./gradlew clean runIdea
```

