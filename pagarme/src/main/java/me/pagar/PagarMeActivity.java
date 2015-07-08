package me.pagar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import me.pagar.card.CardHashView;
import me.pagar.interfaces.CheckoutListener;
import me.pagar.model.CreditCard;

public class PagarMeActivity extends AppCompatActivity {

  private Toolbar toolbar;
  private CardHashView cardHashView;
  private EditTextShadow cardNumber;
  private EditTextShadow cardValidThru;
  private EditTextShadow cardCvv;
  private EditTextShadow cardName;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pagarme);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    cardHashView = (CardHashView) findViewById(R.id.card_hash_view);
    cardNumber = (EditTextShadow) findViewById(R.id.card_number_shadow);
    cardValidThru = (EditTextShadow) findViewById(R.id.valid_thru);
    cardCvv = (EditTextShadow) findViewById(R.id.cvv_shadow);
    cardName = (EditTextShadow) findViewById(R.id.card_name_shadow);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    cardNumber.addTextChangedListener(cardNumberTextWatcher);
    cardValidThru.addTextChangedListener(cardValidThruTextWatcher);
    cardCvv.addTextChangedListener(cardCvvTextWatcher);
    cardName.addTextChangedListener(cardNameTextWatcher);
    cardHashView.setOnClickListener(onCardHashViewClick);
    configToolbar();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.checkout, menu);
    MenuItem inboxMenuItem = menu.findItem(R.id.action_checkout);
    inboxMenuItem.setActionView(R.layout.checkout_button);
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    boolean isCreated = super.onPrepareOptionsMenu(menu);
    new Handler().post(new Runnable() {
      @Override public void run() {
        findViewById(R.id.icon_title).setOnClickListener(onClickListener);
      }
    });
    return isCreated;
  }

  private View.OnClickListener onCardHashViewClick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if(cardHashView.isRotated()) {
        cardHashView.showCard();
      } else {
        cardHashView.showBack();
      }
    }
  };

  private View.OnClickListener onClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      onCheckoutClick();
    }
  };

  private void onCheckoutClick() {
    CreditCard creditCard = new CreditCard();
    PagarMe.with(this).checkout(creditCard).callback(checkoutListener).execute();
  }

  private void configToolbar() {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  private CheckoutListener checkoutListener = new CheckoutListener() {
    @Override public void onCheckoutSuccess() {
      Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
    }

    @Override public void onCheckoutFail() {
      Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();
    }
  };

  private TextWatcher cardNumberTextWatcher = new TextWatcher() {
    @Override public void afterTextChanged(Editable s) {}
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      cardHashView.getCreditCard().setCardNumber(s.toString());
    }
  };

  private TextWatcher cardValidThruTextWatcher = new TextWatcher() {
    @Override public void afterTextChanged(Editable s) { }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      cardHashView.getCreditCard().setExpiryDate(s.toString());
    }
  };

  private TextWatcher cardCvvTextWatcher = new TextWatcher() {
    @Override public void afterTextChanged(Editable s) { }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      cardHashView.getBackCreditCardView().setCvv(s.toString());
    }
  };

  private TextWatcher cardNameTextWatcher = new TextWatcher() {
    @Override public void afterTextChanged(Editable s) { }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      cardHashView.getCreditCard().setCardName(s.toString());
    }
  };

}
