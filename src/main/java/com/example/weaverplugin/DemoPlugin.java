package com.example.weaverplugin;

import com.github.cc11001100.weavergirl.api.plugin.AbstractPlugin;
import com.github.cc11001100.weavergirl.api.plugin.PluginContext;
import com.github.cc11001100.weavergirl.api.plugin.PluginLogger;
import com.github.cc11001100.weavergirl.api.registry.InterceptorRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal plugin: records and logs every {@code OrderService.placeOrder} call.
 *
 * <p>Interception is declared by class name, not by a compile-time reference. That is what lets a
 * plugin instrument a library it does not bundle, and it keeps the plugin's class loader from
 * linking against application classes.
 *
 * <p>Configuration is scoped by {@link #name()}: in {@code weaver.yml} write
 * {@code plugins: {demo: {tag: checkout}}} and read it with {@code context.getConfig("tag", ...)}.
 */
public final class DemoPlugin extends AbstractPlugin {

  public static final String NAME = "demo";
  public static final String TARGET_CLASS = "com.example.weaverplugin.demo.OrderService";

  private static final List<String> EVENTS = Collections.synchronizedList(new ArrayList<String>());

  private String tag = "demo";
  private PluginLogger log;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public void init(PluginContext context) {
    // init runs before the enable check, so it must not start threads or open sockets.
    tag = context.getConfig("tag", "demo");
    log = context.getLogger();
    log.info("demo plugin initialised, tag=" + tag);
  }

  @Override
  public void registerInterceptors(InterceptorRegistry registry) {
    registry.register(
        intercept(TARGET_CLASS)
            .method("placeOrder")
            .name("demo-place-order")
            .before(inv -> record("before:" + inv.getArgument(0)))
            .after(inv -> record("after:" + inv.getReturnValue()))
            .build());
  }

  private void record(String event) {
    EVENTS.add(tag + ":" + event);
    log.info("[demo] " + event);
  }

  /** Observed calls, oldest first. */
  public static List<String> events() {
    synchronized (EVENTS) {
      return new ArrayList<String>(EVENTS);
    }
  }

  public static void reset() {
    EVENTS.clear();
  }
}
