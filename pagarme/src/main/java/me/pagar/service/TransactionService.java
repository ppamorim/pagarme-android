package me.pagar.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import me.pagar.Constant;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionService {

  public static InputStream capture(BigDecimal bigDecimal, int id) throws IOException, JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(Constant.AMOUNT, bigDecimal);
    return BaseService.post(new StringBuilder(Constant.END_POINT)
        .append(Constant.DIVIDER)
        .append(id)
        .append(Constant.CAPTURE).toString(),
        jsonObject.toString());
  }

  public static InputStream refund(BigDecimal bigDecimal, int id) throws IOException, JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(Constant.AMOUNT, bigDecimal);
    return BaseService.post(new StringBuilder(Constant.END_POINT)
            .append(Constant.DIVIDER)
            .append(id)
            .append(Constant.REFUND).toString(),
        jsonObject.toString());
  }

  public static InputStream test() throws IOException, JSONException {
    return BaseService.get("http://www.mocky.io/v2/559c9de0d797efb21d55a58f");
  }

}
