package me.pagar.interfaces;

import java.io.InputStream;

public interface CheckoutListener {
  void onCheckoutSuccess(InputStream inputStream);
  void onCheckoutFail();
}
