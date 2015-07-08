package me.pagar.card;

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
import me.pagar.R;
import me.pagar.SpringSequencer;

public class CardHashView extends ViewFlipper {

  private float rotate;

  private ShadowLayout shadowLayout;
  private FrontCreditCardView frontCreditCardView;
  private BackCreditCardView backCreditCardView;

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
    frontCreditCardView = (FrontCreditCardView) findViewById(R.id.credit_card_view);
    backCreditCardView = (BackCreditCardView) findViewById(R.id.back_credit_card_view);
    if (attrs != null) {
      final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlipView);
    }
  }

  public FrontCreditCardView getCreditCard() {
    return frontCreditCardView;
  }

  public BackCreditCardView getBackCreditCardView() {
    return backCreditCardView;
  }

  public void showCard() {
    springSequencer.clear();
    springSequencer.add(0, scaleSpringZoomOut());
    springSequencer.add(1, rotateSpring());
    //springSequencer.add(2, scaleSpringZoomIn()); <-- Problem here
    springSequencer.setEndValue(0);
  }

  public void showBack() {
    springSequencer.clear();
    springSequencer.add(0, scaleSpringZoomIn());
    springSequencer.add(1, rotateSpring());
    springSequencer.add(2, scaleSpringZoomOut());
    springSequencer.setEndValue(1);
  }

  public boolean isRotated() {
    return springSequencer.springEnd();
  }

  private void scaleViews(float scale) {
    ViewCompat.setScaleX(shadowLayout, scale);
    ViewCompat.setScaleY(shadowLayout, scale);
    ViewCompat.setScaleY(frontCreditCardView, scale);
    ViewCompat.setScaleX(frontCreditCardView, scale);
    ViewCompat.setScaleY(backCreditCardView, scale);
    ViewCompat.setScaleX(backCreditCardView, scale);
  }

  private SimpleSpringListener scaleSpringZoomInListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      scaleViews((float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.6));
    }
  };

  private SimpleSpringListener scaleSpringZoomOutListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      scaleViews(
          (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0.6, 1));
    }
  };

  private SimpleSpringListener rotateSpringListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      rotate = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 180);
      if(rotate > 90) {
        frontCreditCardView.setVisibility(GONE);
        backCreditCardView.setVisibility(VISIBLE);
      } else {
        frontCreditCardView.setVisibility(VISIBLE);
        backCreditCardView.setVisibility(GONE);
      }
      ViewCompat.setRotationY(frontCreditCardView, rotate);
      ViewCompat.setRotationY(backCreditCardView, 180 - rotate);
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
                  SpringConfig.fromBouncinessAndSpeed(0, 30));
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
                  SpringConfig.fromBouncinessAndSpeed(0, 30));
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
                  SpringConfig.fromOrigamiTensionAndFriction(60, 5));
        }
      }
    }
    return rotateSpring;
  }

}
