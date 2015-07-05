package me.pagar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

public class CardHashView extends ViewFlipper {

  private enum FlipViewSide {
    NOT_CHECKED,
    CHECKED
  }

  private CreditCardView creditCardView;
  private View mBackView;
  private CardHashListener cardHashListener;

  private boolean mChecked;

  public CardHashView(Context context) {
    this(context, null);
  }

  public CardHashView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }

  private void initView(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.card_hash_view, this, true);
    creditCardView = (CreditCardView) findViewById(R.id.credit_card_view);
    if (attrs != null) {
      final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlipView);
    }
  }

  public CreditCardView getCreditCard() {
    return creditCardView;
  }

  public void setFrontView(View view) {
    if (view == null) {
      throw new IllegalArgumentException("The front view can not be null");
    }
    removeViewAt(FlipViewSide.NOT_CHECKED.ordinal());
    addView(view, FlipViewSide.NOT_CHECKED.ordinal());
  }

  public void setBackView(View view) {
    if (view == null) {
      throw new IllegalArgumentException("The back view can not be null");
    }
    removeViewAt(FlipViewSide.CHECKED.ordinal());
    addView(view, FlipViewSide.CHECKED.ordinal());
  }

  private void setChecked(boolean checked) {
    mChecked = checked;
    this.setDisplayedChild(checked ? FlipViewSide.CHECKED.ordinal() : FlipViewSide.NOT_CHECKED.ordinal());

    if(cardHashListener != null) {
      cardHashListener.onFlipViewClick(this, checked);
    }
  }

  public void setCardHashListener(CardHashListener cardHashListener) {
    this.cardHashListener = cardHashListener;
  }

}
