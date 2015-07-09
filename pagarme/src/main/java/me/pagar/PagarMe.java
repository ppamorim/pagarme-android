package me.pagar;

import android.content.Context;
import java.io.InputStream;
import me.pagar.interfaces.CheckoutListener;
import me.pagar.model.CreditCard;
import me.pagar.runnable.CheckoutManager;
import me.pagar.runnable.CheckoutTask;

public class PagarMe {

  public static volatile PagarMe singleton = null;

  private Context context;

  private CreditCard creditCard;

  private CheckoutTask checkoutTask;

  public PagarMe(Context context) {
    this.context = context;
  }

  public static PagarMe with(Context context) {
    if (singleton == null) {
      synchronized (PagarMe.class) {
        if (singleton == null) {
          singleton = new Builder(context).build();
        }
      }
    }
    return singleton;
  }

  public PagarMe checkout(CreditCard creditCard) {
    this.creditCard = creditCard;
    return singleton;
  }

  public PagarMe callback(CheckoutListener checkoutListener) {
    this.checkoutListener = checkoutListener;
    return singleton;
  }

  public void execute() {
    checkoutTask = CheckoutManager.startCheckout(checkoutListener);
  }

  private CheckoutListener checkoutListener = new CheckoutListener() {
    @Override public void onCheckoutSuccess(InputStream inputStream) {
      System.out.println("teste!");
    }

    @Override public void onCheckoutFail() {

    }
  };

  /** Fluent API for creating {@link PagarMe} instances. */
  @SuppressWarnings("UnusedDeclaration") // Public API.
  public static class Builder {
    private final Context context;

    /** Start building a new {@link PagarMe} instance. */
    public Builder(Context context) {
      if (context == null) {
        throw new IllegalArgumentException("Context must not be null.");
      }
      this.context = context.getApplicationContext();
    }

    /** Create the {@link PagarMe} instance. */
    public PagarMe build() {
      Context context = this.context;
      return new PagarMe(context);
    }
  }

}
