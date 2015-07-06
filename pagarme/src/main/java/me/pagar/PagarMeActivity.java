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
    cardCvv = (EditTextShadow) findViewById(R.id.cvv);
    cardName = (EditTextShadow) findViewById(R.id.card_name_shadow);

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    cardNumber.addTextChangedListener(cardNumberTextWatcher);
    cardValidThru.addTextChangedListener(cardValidThruTextWatcher);
    cardCvv.addTextChangedListener(cardCvvTextWatcher);
    cardName.addTextChangedListener(cardNameTextWatcher);
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
    new Handler().post(new Runnable() {
      @Override public void run() {
        TextView textView = (TextView) findViewById(R.id.icon_title);
        textView.setOnClickListener(onClickListener);
      }
    });
    return true;
  }

  private View.OnClickListener onClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      onCheckoutClick();
    }
  };

  private void onCheckoutClick() {
    System.out.println("testeeeeeeeeeeeeeeeee");
  }

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
      //cardHashView.getCreditCard().setCardName(s.toString());
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
