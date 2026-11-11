# Weaver-Girl Plugin Template

A minimal, working third-party plugin for [Weaver-Girl](https://github.com/weaver-girl/weaver-girl),
the modular Java bytecode instrumentation framework. Use it as the starting point for an APM
extension, an IAST scanner, an audit trail, or any other product built on the hook layer.

The template ships one interception, its ServiceLoader registration, an in-process test that proves
the callback fires, and a runnable application for the end-to-end check.

## Use this template

1. Click **Use this template → Create a new repository** (or copy the files).
2. Rename the Maven coordinates in `pom.xml` (`com.example:weaver-girl-plugin-demo`).
3. Pick a stable plugin name in `DemoPlugin.NAME` and `META-INF/weaver-girl-plugin.properties`.
   Configuration keys, `depends()`/`requires()`, and `enabledPlugins`/`disabledPlugins` all key on it.
4. Rename the Java package and the two `META-INF` files. Then build:

```bash
mvn verify
```

The test attaches the instrumentation agent to the test JVM, installs the plugin through the real
lifecycle, weaves `OrderService`, and asserts the before/after callbacks ran.

## Run it against the agent

```bash
# 1. Build the plugin jar
mvn package

# 2. Put it in the agent's plugin directory (next to weaver-girl-agent-<version>.jar)
mkdir -p /tmp/weaver-plugins
cp target/weaver-girl-plugin-demo-1.0.0-SNAPSHOT.jar /tmp/weaver-plugins/

# 3. Run an application with the agent attached
java -javaagent:weaver-girl-agent-1.0.0-SNAPSHOT.jar=plugins=/tmp/weaver-plugins \
     -cp target/classes com.example.weaverplugin.demo.DemoApplication
```

Expected output:

```
[demo] before:A-1001
[demo] after:confirmed:A-1001
placeOrder -> confirmed:A-1001
```

Configuration can be passed inline (`=config=/path/weaver.yml`) or through system properties. A
plugin's keys live under its name:

```yaml
plugins:
  demo:
    tag: checkout
```

## What is in here

| Path | Purpose |
| --- | --- |
| `DemoPlugin.java` | The plugin: lifecycle hooks and one interceptor |
| `demo/OrderService.java` | Stand-in for the application class being instrumented |
| `demo/DemoApplication.java` | Runnable target for the end-to-end check |
| `META-INF/services/...WeaverPlugin` | ServiceLoader registration — discovery depends on it |
| `META-INF/weaver-girl-plugin.properties` | Name, version, contract version, minimum agent |
| `DemoPluginTest.java` | In-process test with `weaver-girl-testkit` |

## Contract rules

- Compile against `weaver-girl-api` only, with `<scope>provided</scope>`.
- Never bundle `weaver-girl-api` in the plugin jar. The agent supplies the one shared copy; a second
  copy defeats the parent-first delegation the contract relies on.
- Declare `plugin.apiVersion` as the `WeaverPlugin.API_VERSION` you compiled against. The loader
  refuses a jar built against a newer contract instead of failing halfway through `init`.
- Read configuration in `init`, but start threads and open sockets in `registerInterceptors` (or
  lazily). `init` runs even when the plugin is disabled.
- Declare targets by class name (`intercept("com.example.Target")`) unless the library is on your
  compile classpath. That is what lets a plugin instrument code it does not bundle.

Until `1.0.0` is on Maven Central, build the core first and install it locally:

```bash
git clone https://github.com/weaver-girl/weaver-girl.git
cd weaver-girl && ./mvnw -DskipTests install
```

Then `mvn verify` in this repository resolves `1.0.0-SNAPSHOT` from `~/.m2`.

## Full documentation

- [Plugin Developer Guide](https://github.com/weaver-girl/weaver-girl/blob/main/docs/plugin-developer-guide.md)
- [Architecture](https://github.com/weaver-girl/weaver-girl/blob/main/docs/ARCHITECTURE.md)
- [YAML Config Reference](https://github.com/weaver-girl/weaver-girl/blob/main/docs/yaml-config-reference.md)

## License

[MIT](LICENSE)
