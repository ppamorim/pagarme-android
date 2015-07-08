package me.pagar.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.pagar.R;

public class BackCreditCardView extends RelativeLayout {

  private TextView editTextCvv;

  public BackCreditCardView(Context context) {
    this(context, null);
  }

  public BackCreditCardView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BackCreditCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    init();
  }

  /**
   * Initialize various views and variables
   */
  private void init() {
    ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .inflate(R.layout.credit_card_view_back, this, true);
    editTextCvv = (TextView) findViewById(R.id.cvv);
  }

  public void setCvv(String cvv) {
    editTextCvv.setText(cvv);
  }

}
