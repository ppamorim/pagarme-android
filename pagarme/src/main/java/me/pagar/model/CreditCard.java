package me.pagar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CreditCard implements Parcelable {

  public int[] cardNumber;
  public String cardName;
  public String date;
  public int[] cvv;

  public CreditCard() { }

  public CreditCard(int[] cardNumber, String cardName, String date, int[] cvv) {
    this.cardNumber = cardNumber;
    this.cardName = cardName;
    this.date = date;
    this.cvv = cvv;
  }

  public int[] getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(int[] cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCardName() {
    return cardName;
  }

  public void setCardName(String cardName) {
    this.cardName = cardName;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int[] getCvv() {
    return cvv;
  }

  public void setCvv(int[] cvv) {
    this.cvv = cvv;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeIntArray(getCardNumber());
    dest.writeString(getCardName());
    dest.writeString(getDate());
    dest.writeIntArray(getCvv());
  }

}
