package me.pagar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

public class PagarMeActivity extends AppCompatActivity {

  private CardHashView cardHashView;
  private EditTextShadow cardNumber;
  private EditTextShadow cardValidThru;
  private EditTextShadow cardCvv;
  private EditTextShadow cardName;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pagarme);
    cardHashView = (CardHashView) findViewById(R.id.card_hash_view);
    cardNumber = (EditTextShadow) findViewById(R.id.card_number_shadow);
    cardValidThru = (EditTextShadow) findViewById(R.id.valid_thru);
    cardCvv = (EditTextShadow) findViewById(R.id.cvv);
    cardName = (EditTextShadow) findViewById(R.id.card_name_shadow);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    cardNumber.addTextChangedListener(cardNumberTextWatcher);
    cardValidThru.addTextChangedListener(cardValidThruTextWatcher);
    cardCvv.addTextChangedListener(cardCvvTextWatcher);
    cardName.addTextChangedListener(cardNameTextWatcher);
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
