package me.pagar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ViewFlipper;
import com.dd.ShadowLayout;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

public class CardHashView extends ViewFlipper {

  private ShadowLayout shadowLayout;
  private CreditCardView creditCardView;
  private CreditCardView mBackView;
  private CardHashListener cardHashListener;

  float rotate;

  private Spring scaleSpringZoomIn;
  private Spring scaleSpringZoomOut;
  private Spring rotateSpring;

  private SpringSequencer springSequencer;

  public CardHashView(Context context) {
    this(context, null);
  }

  public CardHashView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
    springSequencer = new SpringSequencer();
    springSequencer.add(0, scaleSpringZoomIn());
    springSequencer.add(1, rotateSpring());
    springSequencer.add(2, scaleSpringZoomOut());

  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    scaleSpringZoomIn().addListener(scaleSpringZoomInListener);
    scaleSpringZoomOut().addListener(scaleSpringZoomOutListener);
    rotateSpring().addListener(rotateSpringListener);
  }

  @Override protected void onDetachedFromWindow() {
    scaleSpringZoomIn().removeListener(scaleSpringZoomInListener);
    scaleSpringZoomOut().removeListener(scaleSpringZoomOutListener);
    rotateSpring().removeListener(rotateSpringListener);
    super.onDetachedFromWindow();
  }

  private void initView(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.card_hash_view, this, true);
    shadowLayout = (ShadowLayout) findViewById(R.id.shadow_layout);
    creditCardView = (CreditCardView) findViewById(R.id.credit_card_view);
    mBackView = (CreditCardView) findViewById(R.id.include_back);
    if (attrs != null) {
      final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlipView);
    }
  }

  public CreditCardView getCreditCard() {
    return creditCardView;
  }

  public void showCard() {
    springSequencer.setEndValue(0);
  }

  public void showBack() {
    springSequencer.setEndValue(1);
  }

  public boolean isRotated() {
    return springSequencer.springEnd();
  }

  private SimpleSpringListener scaleSpringZoomInListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      float scale = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.6);
      ViewCompat.setScaleX(shadowLayout, scale);
      ViewCompat.setScaleY(shadowLayout, scale);
      ViewCompat.setScaleY(creditCardView, scale);
      ViewCompat.setScaleX(creditCardView, scale);
      ViewCompat.setScaleY(mBackView, scale);
      ViewCompat.setScaleX(mBackView, scale);
    }
  };

  private SimpleSpringListener scaleSpringZoomOutListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      float scale = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0.6, 1);
      ViewCompat.setScaleX(shadowLayout, scale);
      ViewCompat.setScaleY(shadowLayout, scale);
      ViewCompat.setScaleY(creditCardView, scale);
      ViewCompat.setScaleX(creditCardView, scale);
      ViewCompat.setScaleY(mBackView, scale);
      ViewCompat.setScaleX(mBackView, scale);
    }
  };

  private SimpleSpringListener rotateSpringListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      rotate = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 180);
      if(rotate > 90) {
        creditCardView.setVisibility(GONE);
        mBackView.setVisibility(VISIBLE);
      } else {
        creditCardView.setVisibility(VISIBLE);
        mBackView.setVisibility(GONE);
      }
      ViewCompat.setRotationY(creditCardView, rotate);
      ViewCompat.setRotationY(mBackView, rotate);
    }
  };

  private Spring scaleSpringZoomIn() {
    if(scaleSpringZoomIn == null) {
      synchronized (Spring.class) {
        if(scaleSpringZoomIn == null) {
          scaleSpringZoomIn = SpringSystem
              .create()
              .createSpring()
              .setSpringConfig(
                  SpringConfig.fromOrigamiTensionAndFriction(50, 10));
        }
      }
    }
    return scaleSpringZoomIn;
  }

  private Spring scaleSpringZoomOut() {
    if(scaleSpringZoomOut == null) {
      synchronized (Spring.class) {
        if(scaleSpringZoomOut == null) {
          scaleSpringZoomOut = SpringSystem
              .create()
              .createSpring()
              .setSpringConfig(
                  SpringConfig.fromOrigamiTensionAndFriction(50, 10));
        }
      }
    }
    return scaleSpringZoomOut;
  }

  private Spring rotateSpring() {
    if(rotateSpring == null) {
      synchronized (Spring.class) {
        if(rotateSpring == null) {
          rotateSpring = SpringSystem
              .create()
              .createSpring()
              .setSpringConfig(
                  SpringConfig.fromOrigamiTensionAndFriction(40, 5));
        }
      }
    }
    return rotateSpring;
  }

}
