package me.pagar;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import java.util.ArrayList;

/**
 * SpringSequencer class provides a sequencer animation for your collection of Spring,
 * this automatically runs the next spring when the current spring ends.
 */
public class SpringSequencer {

  private boolean animationEnd;
  private int position = 0;
  private double mEndValue = -1;
  private ArrayList<Spring> springs = new ArrayList<>();

  /**
   *
   * @param springId represent the position of spring to be animated.
   * @param spring the instance of spring.
   * @return Actual instance of SpringSequencer.
   */
  public SpringSequencer add(int springId, Spring spring) {
    springs.add(springId, spring);
    return this;
  }

  /**
   * Erase all springs added on springs arrayList
   * @return Actual instance of SpringSequencer.
   */
  public SpringSequencer clear() {
    if(springs.size() > 0) {
      springs.clear();
    }
    return this;
  }

  public boolean springEnd() {
    return animationEnd;
  }

  /**
   * Control the state of animation, this do a recursivity to jump to the next
   * animation that contains at springs arraylist.
   * @param endValue the endValue for the spring.
   */
  public void setEndValue(double endValue) {

    if(this.mEndValue != endValue) {
      this.mEndValue = endValue;
      reset();
    }

    if(springs.size() <= 0) {
      throw new IllegalStateException("Springs don't have one item");
    }

    if(!animationEnd && springs.size() > position) {
      Spring spring = springs.get(position);
      if(spring != null) {
        spring.addListener(new SimpleSpringListener() {
          @Override public void onSpringAtRest(Spring spring) {
            position++;
            setEndValue(mEndValue);
          }
        }).setEndValue(mEndValue);
      }
    } else {
      animationEnd = true;
      position = 0;
    }
  }

  private void reset() {
    animationEnd = false;
    position = 0;
  }

}
