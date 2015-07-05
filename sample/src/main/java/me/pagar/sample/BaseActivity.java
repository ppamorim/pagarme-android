package me.pagar.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import me.pagar.PagarMeActivity;

public class BaseActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pagarme);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        startActivity(new Intent(getApplicationContext(), PagarMeActivity.class));
      }
    }, 2000);

  }
}
