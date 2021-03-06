package me.pagar.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import me.pagar.R;

import static me.pagar.card.CardNumberFormat.ALL_DIGITS;
import static me.pagar.card.CardNumberFormat.MASKED_ALL;
import static me.pagar.card.CardNumberFormat.MASKED_ALL_BUT_LAST_FOUR;
import static me.pagar.card.CardNumberFormat.ONLY_LAST_FOUR;
import static me.pagar.card.CardType.AMERICAN_EXPRESS;
import static me.pagar.card.CardType.AUTO;
import static me.pagar.card.CardType.DISCOVER;
import static me.pagar.card.CardType.MASTERCARD;
import static me.pagar.card.CardType.PATTERN_AMERICAN_EXPRESS;
import static me.pagar.card.CardType.PATTERN_DISCOVER;
import static me.pagar.card.CardType.PATTERN_MASTER_CARD;
import static me.pagar.card.CardType.VISA;

public class FrontCreditCardView extends RelativeLayout {

  @IntDef({VISA, MASTERCARD, AMERICAN_EXPRESS, DISCOVER, AUTO})
  @Retention(RetentionPolicy.SOURCE)
  public @interface CreditCardType {
  }

  @IntDef({ALL_DIGITS, MASKED_ALL_BUT_LAST_FOUR, ONLY_LAST_FOUR, MASKED_ALL})
  @Retention(RetentionPolicy.SOURCE)
  public @interface CreditCardFormat {
  }

  private static final boolean DEBUG = false;
  private Context mContext;
  private String mCardNumber = "";
  private String mCardName = "";
  private String mExpiryDate = "";
  private int mCardNumberTextColor = Color.WHITE;
  private int mCardNumberFormat = ALL_DIGITS;
  private int mCardNameTextColor = Color.WHITE;
  private int mExpiryDateTextColor = Color.WHITE;
  private int mValidTillTextColor = Color.WHITE;
  private int mType = VISA;
  private int mBrandLogo;
  private boolean mPutChip = false;
  private boolean mIsEditable = false;
  private boolean mIsCardNumberEditable = false;
  private boolean mIsCardNameEditable = false;
  private boolean mIsExpiryDateEditable = false;
  private int mHintTextColor = Color.WHITE;
  private Typeface creditCardTypeFace;
  private TextView cardNumber;
  private TextView cardName;
  private TextView expiryDate;
  private TextView validTill;
  private ImageView type;
  private ImageView brandLogo;
  private ImageView chip;

  public FrontCreditCardView(Context context) {
    this(context, null);
  }

  public FrontCreditCardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    if (context != null) {
      this.mContext = context;
    } else {
      this.mContext = getContext();
    }

    init();
    loadAttributes(attrs);
    initDefaults();
    addListeners();
  }

  /**
   * Initialize various views and variables
   */
  private void init() {
    final LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.credit_card_view_front, this, true);
    cardNumber = (TextView) findViewById(R.id.card_number);
    cardName = (TextView) findViewById(R.id.card_name);
    type = (ImageView) findViewById(R.id.card_logo);
    brandLogo = (ImageView) findViewById(R.id.brand_logo);
    chip = (ImageView) findViewById(R.id.chip);
    validTill = (TextView) findViewById(R.id.valid_thru_card);
    expiryDate = (TextView) findViewById(R.id.thru_date_card);
  }

  private void loadAttributes(@Nullable AttributeSet attrs) {

    final TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs,
        R.styleable.FrontCreditCardView, 0, 0);

    try {
      mCardNumber = a.getString(R.styleable.FrontCreditCardView_cardNumber);
      mCardName = a.getString(R.styleable.FrontCreditCardView_cardName);
      mExpiryDate = a.getString(R.styleable.FrontCreditCardView_expiryDate);
      mCardNumberTextColor = a.getColor(R.styleable.FrontCreditCardView_cardNumberTextColor,
          Color.WHITE);
      mCardNumberFormat = a.getInt(R.styleable.FrontCreditCardView_cardNumberFormat, 0);
      mCardNameTextColor = a.getColor(R.styleable.FrontCreditCardView_cardNumberTextColor,
          Color.WHITE);
      mExpiryDateTextColor = a.getColor(R.styleable.FrontCreditCardView_expiryDateTextColor,
          Color.WHITE);
      mValidTillTextColor = a.getColor(R.styleable.FrontCreditCardView_validTillTextColor,
          Color.WHITE);
      mType = a.getInt(R.styleable.FrontCreditCardView_type, 0);
      mBrandLogo = a.getResourceId(R.styleable.FrontCreditCardView_brandLogo, 0);
      // mBrandLogoPosition = a.getInt(R.styleable.CreditCardView_brandLogoPosition, 1);
      mPutChip = a.getBoolean(R.styleable.FrontCreditCardView_putChip, false);
      mIsEditable = a.getBoolean(R.styleable.FrontCreditCardView_isEditable, false);
      //For more granular control to the fields. Issue #7
      mIsCardNameEditable = a.getBoolean(R.styleable.FrontCreditCardView_isCardNameEditable, mIsEditable);
      mIsCardNumberEditable = a.getBoolean(R.styleable.FrontCreditCardView_isCardNumberEditable, mIsEditable);
      mIsExpiryDateEditable = a.getBoolean(R.styleable.FrontCreditCardView_isExpiryDateEditable, mIsEditable);
      mHintTextColor = a.getColor(R.styleable.FrontCreditCardView_hintTextColor, Color.WHITE);
    } finally {
      a.recycle();
    }
  }

  private void initDefaults() {
    if(mIsCardNameEditable!=mIsEditable){
      cardName.setEnabled(mIsCardNameEditable);
    }

    if(mIsCardNumberEditable!=mIsEditable){
      cardNumber.setEnabled(mIsCardNumberEditable);
    }

    if(mIsExpiryDateEditable!=mIsEditable){
      expiryDate.setEnabled(mIsExpiryDateEditable);
    }

    // If card number is not null, add space every 4 characters and format it in the appropriate
    // format
    if (mCardNumber != null) {
      cardNumber.setText(checkCardNumberFormat(addSpaceToCardNumber(mCardNumber)));
    }

    // Set the user entered card number color to card number field
    cardNumber.setTextColor(mCardNumberTextColor);

    // Added this check to fix the issue of custom view not rendering correctly in the layout
    // preview.
    if (!isInEditMode()) {
      cardNumber.setTypeface(creditCardTypeFace);
    }

    // If card name is not null, convert the text to upper case
    if (mCardName != null) {
      cardName.setText(mCardName.toUpperCase());
    }

    // This filter will ensure the text entered is in uppercase when the user manually enters
    // the card name
    cardName.setFilters(new InputFilter[]{
        new InputFilter.AllCaps()
    });

    // Set the user entered card name color to card name field
    cardName.setTextColor(mCardNumberTextColor);

    // Added this check to fix the issue of custom view not rendering correctly in the layout
    // preview.
    if (!isInEditMode()) {
      cardName.setTypeface(creditCardTypeFace);
    }

    // Set the appropriate logo based on the type of card
    type.setBackgroundResource(getLogo(mType));

    // If background logo attribute is present, set it as the brand logo background resource
    if (mBrandLogo != 0) {
      brandLogo.setBackgroundResource(mBrandLogo);
      // brandLogo.setLayoutParams(params);
    }

    // If putChip attribute is present, change the visibility of the putChip view and display it
    if (mPutChip) {
      chip.setVisibility(View.VISIBLE);
    }

    // If expiry date is not null, set it to the expiryDate TextView
    if (mExpiryDate != null) {
      expiryDate.setText(mExpiryDate);
    }

    // Set the user entered expiry date color to expiry date field
    expiryDate.setTextColor(mExpiryDateTextColor);

    // Added this check to fix the issue of custom view not rendering correctly in the layout
    // preview.
    if (!isInEditMode()) {
      expiryDate.setTypeface(creditCardTypeFace);
    }

    // Set the appropriate text color to the validTill TextView
    validTill.setTextColor(mValidTillTextColor);
  }

  private void addListeners() {

    // Add text change listener
    cardNumber.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Change card type to auto to dynamically detect the card type based on the card
        // number
        mType = AUTO;
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Delete any spaces the user might have entered manually. The library automatically
        // adds spaces after every 4 characters to the view.
        mCardNumber = s.toString().replaceAll("\\s+", "");
      }
    });

    // Add focus change listener to detect focus being shifted from the cardNumber EditText
    cardNumber.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        // If the field just lost focus
        if (!hasFocus) {
          //Fix for NPE. Issue #6
          if(mCardNumber != null) {
            if (mCardNumber.length() > 12) {
              // If the length of card is >12, add space every 4 characters and format it
              // in the appropriate format
              cardNumber
                  .setText(checkCardNumberFormat(addSpaceToCardNumber(mCardNumber)));

              // If card type is "auto",find the appropriate logo
              if (mType == AUTO) {
                type.setBackgroundResource(getLogo(mType));
              }
            }
          }
        }
      }
    });

    cardName.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Set the mCardName attribute the user entered value in the Card Name field
        mCardName = s.toString().toUpperCase();
      }
    });

    expiryDate.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Set the mExpiryDate attribute the user entered value in the Expiry Date field
        mExpiryDate = s.toString();
      }
    });
  }

  private void redrawViews() {
    invalidate();
    requestLayout();
  }

  public String getCardNumber() {
    return cardNumber.getText().toString();
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber.setText(cardNumber.replaceAll("\\s+", ""));
  }

  public String getCardName() {
    return cardName.getText().toString();
  }

  public void setCardName(String cardName) {
    this.cardName.setText(cardName.toUpperCase());
  }

  @ColorInt public int getCardNumberTextColor() {
    return mCardNumberTextColor;
  }

  public void setCardNumberTextColor(@ColorInt int cardNumberTextColor) {
    mCardNumberTextColor = cardNumberTextColor;
    redrawViews();
  }

  @CreditCardFormat
  public int getCardNumberFormat() {
    return mCardNumberFormat;
  }

  public void setCardNumberFormat(@CreditCardFormat int cardNumberFormat) {
    if (cardNumberFormat < 0 | cardNumberFormat > 3) {
      throw new UnsupportedOperationException("CardNumberFormat: " + cardNumberFormat + "  " +
          "is not supported. Use `CardNumberFormat.*` or `CardType.ALL_DIGITS` if " +
          "unknown");
    }
    mCardNumberFormat = cardNumberFormat;
    redrawViews();
  }

  @ColorInt
  public int getCardNameTextColor() {
    return mCardNameTextColor;
  }

  public void setCardNameTextColor(@ColorInt int cardNameTextColor) {
    mCardNameTextColor = cardNameTextColor;
    redrawViews();
  }

  public String getExpiryDate() {
    return expiryDate.getText().toString();
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate.setText(expiryDate);
  }

  @ColorInt
  public int getExpiryDateTextColor() {
    return mExpiryDateTextColor;
  }

  public void setExpiryDateTextColor(@ColorInt int expiryDateTextColor) {
    mExpiryDateTextColor = expiryDateTextColor;
    redrawViews();
  }

  @ColorInt
  public int getValidTillTextColor() {
    return mValidTillTextColor;
  }

  public void setValidTillTextColor(@ColorInt int validTillTextColor) {
    mValidTillTextColor = validTillTextColor;
    redrawViews();
  }

  @CreditCardType
  public int getType() {
    return mType;
  }

  public void setType(@CreditCardType int type) {
    if (type < 0 | type > 4) {
      throw new UnsupportedOperationException("CardType: " + type + "  is not supported. " +
          "Use `CardType.*` or `CardType.AUTO` if unknown");
    }
    mType = type;
    redrawViews();
  }

  public boolean getIsEditable() {
    return mIsEditable;
  }

  public void setIsEditable(boolean isEditable) {
    mIsEditable = isEditable;
    redrawViews();
  }

  public boolean getIsCardNameEditable() {
    return mIsCardNameEditable;
  }

  public void setIsCardNameEditable(boolean isCardNameEditable) {
    mIsCardNameEditable = isCardNameEditable;
    redrawViews();
  }

  public boolean getIsCardNumberEditable() {
    return mIsCardNumberEditable;
  }

  public void setIsCardNumberEditable(boolean isCardNumberEditable) {
    mIsCardNumberEditable = isCardNumberEditable;
    redrawViews();
  }

  public boolean getIsExpiryDateEditable() {
    return mIsExpiryDateEditable;
  }

  public void setIsExpiryDateEditable(boolean isExpiryDateEditable) {
    mIsExpiryDateEditable = isExpiryDateEditable;
    redrawViews();
  }

  @ColorInt
  public int getHintTextColor() {
    return mHintTextColor;
  }

  public void setHintTextColor(@ColorInt int hintTextColor) {
    mHintTextColor = hintTextColor;
    redrawViews();
  }

  @DrawableRes
  public int getBrandLogo() {
    return mBrandLogo;
  }

  public void setBrandLogo(@DrawableRes int brandLogo) {
    mBrandLogo = brandLogo;
    redrawViews();
  }

  public int getBrandLogoPosition() {
    return mBrandLogo;
  }

  public void setBrandLogoPosition(int brandLogoPosition) {
    redrawViews();
  }

  public void putChip(boolean flag) {
    mPutChip = flag;
    redrawViews();
  }

  /**
   * Return the appropriate drawable resource based on the card type
   *
   * @param type type of card.
   */
  @DrawableRes
  private int getLogo(@CreditCardType int type) {

    switch (type) {
      case VISA:
        return R.mipmap.visa;

      case MASTERCARD:
        return R.mipmap.mastercard;

      case AMERICAN_EXPRESS:
        return R.mipmap.amex;

      case DISCOVER:
        return R.mipmap.discover;

      case AUTO:
        return findCardType();

      default:
        throw new UnsupportedOperationException("CardType: " + type + "  is not supported" +
            ". Use `CardType.*` or `CardType.AUTO` if unknown");
    }

  }

  /**
   * Returns the formatted card number based on the user entered value for card number format
   *
   * @param cardNumber Card Number.
   */
  private String checkCardNumberFormat(String cardNumber) {

    if (DEBUG) {
      Log.e("Card Number", cardNumber);
    }

    switch (getCardNumberFormat()) {
      case MASKED_ALL_BUT_LAST_FOUR:
        cardNumber = "**** **** **** "
            + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        break;
      case ONLY_LAST_FOUR:
        cardNumber = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        break;
      case MASKED_ALL:
        cardNumber = "**** **** **** ****";
        break;
      default:
        //do nothing.
        break;
    }
    return cardNumber;
  }

  /**
   * Returns the appropriate card type drawable resource based on the regex pattern of the card
   * number
   */
  @DrawableRes
  private int findCardType() {

    int type = VISA;
    if (cardNumber.length() > 0) {

      final String cardNumber = getCardNumber().replaceAll("\\s+", "");

      if (Pattern.compile(PATTERN_MASTER_CARD).matcher(cardNumber).matches()) {
        type = MASTERCARD;
      } else if (Pattern.compile(PATTERN_AMERICAN_EXPRESS).matcher(cardNumber)
          .matches()) {
        type = AMERICAN_EXPRESS;
      } else if (Pattern.compile(PATTERN_DISCOVER).matcher(cardNumber).matches()) {
        type = DISCOVER;
      }
    }
    setType(type);

    return getLogo(type);
  }

  /**
   * Adds space after every 4 characters to the card number if the card number is divisible by 4
   *
   * @param cardNumber Card Number.
   */
  private String addSpaceToCardNumber(String cardNumber) {

    if (cardNumber.length() % 4 != 0) {
      return cardNumber;
    } else {
      final StringBuilder result = new StringBuilder();
      for (int i = 0; i < cardNumber.length(); i++) {
        if (i % 4 == 0 && i != 0 && i != cardNumber.length() - 1) {
          result.append(" ");
        }
        result.append(cardNumber.charAt(i));
      }
      return result.toString();
    }
  }

}