package com.example.weaverplugin.demo;

/**
 * Runnable target for the end-to-end check. It deliberately has no Weaver-Girl API on its class
 * path: the plugin is loaded by the agent, and the application only sees its own classes.
 */
public final class DemoApplication {

  public static void main(String[] args) {
    OrderService service = new OrderService();
    System.out.println("placeOrder -> " + service.placeOrder("A-1001"));
    System.out.println("placeOrder -> " + service.placeOrder("A-1002"));
  }
}
