package me.pagar;

public class ApiKey {

  public String apiKey;

  public ApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public static String fullApiUrl(String path) {
    return new StringBuilder(Constant.END_POINT)
        .append(Constant.DIVIDER)
        .append(Constant.API_VERSION)
        .append(path).toString();
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
  public String getApiKey() {
    return apiKey;
  }

}
