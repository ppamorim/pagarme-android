package me.pagar;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class FingerPrint {

  public static boolean validateFingerprint(String id, String fingerprint) throws
      NoSuchAlgorithmException, UnsupportedEncodingException {
    return (ConvertUtil.SHA1(id).equals(fingerprint));
  }

}
