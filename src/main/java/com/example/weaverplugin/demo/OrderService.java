package com.example.weaverplugin.demo;

/** Stand-in for the application class a plugin instruments. */
public class OrderService {

  public String placeOrder(String orderId) {
    return "confirmed:" + orderId;
  }
}
