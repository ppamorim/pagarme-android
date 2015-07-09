package me.pagar.runnable;

import java.io.InputStream;
import me.pagar.interfaces.CheckoutRunnableCallback;

public class CheckoutTask implements CheckoutRunnableCallback {

  private Runnable checkoutRunnable;
  private CheckoutManager checkoutManager;

  public CheckoutTask() {
    this.checkoutRunnable = new CheckoutRunnable(this);
  }

  public void initialize(CheckoutManager checkoutManager) {
    this.checkoutManager = checkoutManager;
  }

  public Runnable getCheckoutRunnable() {
    return checkoutRunnable;
  }

  @Override public void onRequestSuccess(InputStream inputStream) {
    checkoutManager.onRequestSuccess(inputStream);
  }

  @Override public void onIOException() {

  }

  @Override public void onJSONException() {

  }

  @Override public void onInterruptedException() {

  }
}
