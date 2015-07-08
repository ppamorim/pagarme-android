package me.pagar.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.InputStream;

public class BaseService {

  public static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  public static InputStream post(String url,String json) throws IOException {
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    Response response = new OkHttpClient().newCall(request).execute();
    return response.body().byteStream();
  }

  public static InputStream get(String url) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .build();
    Response response = new OkHttpClient().newCall(request).execute();
    return response.body().byteStream();
  }

}
