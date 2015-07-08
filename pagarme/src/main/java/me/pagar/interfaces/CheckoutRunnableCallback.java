package me.pagar.interfaces;

public interface CheckoutRunnableCallback {
  void onRequestSuccess();
  void onIOException();
  void onJSONException();
  void onInterruptedException();
}
