package me.pagar.runnable;

import android.os.Debug;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import me.pagar.interfaces.CheckoutRunnableCallback;
import me.pagar.service.TransactionService;
import org.json.JSONException;

public class CheckoutRunnable implements Runnable {

  private static final String TAG = "CheckoutRunnable";

  private CheckoutRunnableCallback checkoutRunnableCallback;

  public CheckoutRunnable(CheckoutRunnableCallback checkoutRunnableCallback) {
    this.checkoutRunnableCallback = checkoutRunnableCallback;
  }

  @Override public void run() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    try {
      if(Thread.interrupted()) {
        throw new InterruptedException();
      }
      InputStream inputStream = TransactionService.test();
      System.out.println("AEEEEEE");
      if(Thread.interrupted()) {
        throw new InterruptedException();
      }
      if(inputStream != null) {
        checkoutRunnableCallback.onRequestSuccess();
      }
    } catch (IOException ioException) {
      checkoutRunnableCallback.onIOException();
    } catch (JSONException jsonException) {
      checkoutRunnableCallback.onJSONException();
    } catch (InterruptedException interruptedException) {
      checkoutRunnableCallback.onInterruptedException();
    } finally {
      Thread.interrupted();
    }
  }

}
