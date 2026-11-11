package com.example.weaverplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.example.weaverplugin.demo.OrderService;
import com.github.cc11001100.weavergirl.testkit.PluginTestHarness;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class DemoPluginTest {

  @AfterEach
  void clearEvents() {
    DemoPlugin.reset();
  }

  @Test
  void placeOrderIsObserved() {
    PluginTestHarness harness;
    try {
      harness = PluginTestHarness.create().config("demo", "tag", "checkout").install(new DemoPlugin());
    } catch (IllegalStateException cannotAttach) {
      // Self-attachment can be disabled (e.g. -XX:-EnableDynamicAgentLoading on JDK 21+).
      assumeTrue(false, "instrumentation agent unavailable: " + cannotAttach.getMessage());
      return;
    }

    try (PluginTestHarness open = harness) {
      open.weave(OrderService.class);

      String result = new OrderService().placeOrder("A-1001");

      assertEquals("confirmed:A-1001", result);
      assertEquals(
          Arrays.asList("checkout:before:A-1001", "checkout:after:confirmed:A-1001"),
          DemoPlugin.events());
    }
  }
}
