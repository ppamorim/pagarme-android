package me.pagar.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

  private double verticalCurrentValue;

  private float rotateHorizontal;
  private float rotateVertical;

  private ShadowLayout shadowLayout;
  private FrontCreditCardView frontCreditCardView;
  private BackCreditCardView backCreditCardView;

  private Spring scaleSpringZoomIn;
  private Spring scaleSpringZoomOut;
  private Spring rotateHorizontalSpring;
  private Spring rotateVerticalSpring;
  private SpringSequencer springSequencer;

  private View bottomView;

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
    rotateHorizontalSpring().addListener(rotateHorizontalSpringListener);
    rotateVerticalSpring().addListener(rotateVerticalSpringListener);
  }

  @Override protected void onDetachedFromWindow() {
    scaleSpringZoomIn().removeListener(scaleSpringZoomInListener);
    scaleSpringZoomOut().removeListener(scaleSpringZoomOutListener);
    rotateHorizontalSpring().removeListener(rotateHorizontalSpringListener);
    rotateVerticalSpring().removeListener(rotateVerticalSpringListener);
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

  public void setBottomView(View bottomView) {
    this.bottomView = bottomView;
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
    springSequencer.add(1, rotateHorizontalSpring());
    //springSequencer.add(2, scaleSpringZoomIn()); <-- Problem here
    springSequencer.setEndValue(0);
  }

  public void showBack() {
    springSequencer.clear();
    springSequencer.add(0, scaleSpringZoomIn());
    springSequencer.add(1, rotateHorizontalSpring());
    springSequencer.add(2, scaleSpringZoomOut());
    springSequencer.setEndValue(1);
  }

  public void toggleTilt() {
    rotateVerticalSpring().setEndValue(rotateVerticalSpring().getCurrentValue() == 0 ? 1 : 0);
  }

  public void setTilt(int endValue) {
    rotateVerticalSpring().setEndValue(rotateVerticalSpring().getCurrentValue() == 0 ? 1 : 0);
  }

  public boolean isHorizontalRotated() {
    return springSequencer.springEnd();
  }

  public boolean isVerticalRotated() {
    return rotateVerticalSpring.getCurrentValue() == 1;
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

  private SimpleSpringListener rotateHorizontalSpringListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);
      rotateHorizontal = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, 180);
      if(rotateHorizontal > 90) {
        frontCreditCardView.setVisibility(GONE);
        backCreditCardView.setVisibility(VISIBLE);
      } else {
        frontCreditCardView.setVisibility(VISIBLE);
        backCreditCardView.setVisibility(GONE);
      }
      ViewCompat.setRotationY(frontCreditCardView, rotateHorizontal);
      ViewCompat.setRotationY(backCreditCardView, 180 - rotateHorizontal);
    }
  };

  private SimpleSpringListener rotateVerticalSpringListener = new SimpleSpringListener() {
    @Override public void onSpringUpdate(Spring spring) {
      super.onSpringUpdate(spring);

      verticalCurrentValue = spring.getCurrentValue();

      float rotation = (float) SpringUtil.mapValueFromRangeToRange(verticalCurrentValue, 0, 1,
          0, -30);
      ViewCompat.setRotationX(frontCreditCardView, rotation);
      ViewCompat.setRotationX(backCreditCardView, rotation);

      float scale = (float) SpringUtil.mapValueFromRangeToRange(verticalCurrentValue, 0, 1, 1, 0.7);
      ViewCompat.setScaleX(frontCreditCardView, scale);
      ViewCompat.setScaleY(frontCreditCardView, scale);
      ViewCompat.setScaleX(backCreditCardView, scale);
      ViewCompat.setScaleY(backCreditCardView, scale);

      if(bottomView != null) {
        int creditCardHeight = frontCreditCardView.getHeight();
        float cardPositionY = (float) SpringUtil.mapValueFromRangeToRange(verticalCurrentValue, 0,
            1, 0, -(creditCardHeight / 6.5));
        ViewCompat.setTranslationY(frontCreditCardView, cardPositionY);
        ViewCompat.setTranslationY(backCreditCardView, cardPositionY);
        ViewCompat.setTranslationY(bottomView,
            (float) SpringUtil.mapValueFromRangeToRange(verticalCurrentValue, 0, 1, 0,
                -(creditCardHeight / 2.5)));
      }
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

  private Spring rotateHorizontalSpring() {
    if(rotateHorizontalSpring == null) {
      synchronized (Spring.class) {
        if(rotateHorizontalSpring == null) {
          rotateHorizontalSpring = SpringSystem
              .create()
              .createSpring()
              .setSpringConfig(
                  SpringConfig.fromOrigamiTensionAndFriction(60, 5));
        }
      }
    }
    return rotateHorizontalSpring;
  }

  private Spring rotateVerticalSpring() {
    if(rotateVerticalSpring == null) {
      synchronized (Spring.class) {
        if(rotateVerticalSpring == null) {
          rotateVerticalSpring = SpringSystem
              .create()
              .createSpring()
              .setSpringConfig(
                  SpringConfig.fromOrigamiTensionAndFriction(60, 5));
        }
      }
    }
    return rotateVerticalSpring;
  }

}
