package me.pagar;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

public class EditTextShadow extends FrameLayout {

  private int textColor;
  private int hintColor;
  private int inputType;
  private float textSize;
  private String hint;

  private EditText editText;

  public EditTextShadow(Context context) {
    this(context, null);
  }

  public EditTextShadow(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EditTextShadow(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(attrs);
  }

  private void initView(AttributeSet attributeSet) {
    TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.EditTextPagarMe);
    if(a != null) {
      inputType = a.getInt(R.styleable.EditTextPagarMe_android_inputType,
          EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
      textSize = a.getDimensionPixelSize(R.styleable.EditTextPagarMe_android_textSize, 0);
      textColor = a.getColor(R.styleable.EditTextPagarMe_android_textColor, 0);
      hint = a.getString(R.styleable.EditTextPagarMe_android_hint);
      hintColor = a.getColor(R.styleable.EditTextPagarMe_android_textColorHint, 0);
      a.recycle();
    }
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    addViewAndInit();
  }

  private void addViewAndInit() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_text_pagarme, null);
    editText = (EditText) view.findViewById(R.id.edit_text);
    addView(view);
    configView();
  }

  private void configView() {
    if(textSize > 0) {
      editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
    if(textColor > 0) {
      editText.setTextColor(textColor);
    }
    if(hint != null) {
      editText.setHint(hint);
    }
    if(hintColor > 0) {
      editText.setHintTextColor(hintColor);
    }
    if(inputType > 0) {
      editText.setInputType(inputType);
    }
  }

  public void addTextChangedListener(TextWatcher textWatcher) {
    editText.addTextChangedListener(textWatcher);
  }

}
