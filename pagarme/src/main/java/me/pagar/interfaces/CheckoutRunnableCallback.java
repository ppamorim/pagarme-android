package me.pagar.interfaces;

import java.io.InputStream;

public interface CheckoutRunnableCallback {
  void onRequestSuccess(InputStream inputStream);
  void onIOException();
  void onJSONException();
  void onInterruptedException();
}
