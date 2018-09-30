package org.properti.analisa.financialcheck.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.auth.LoginActivity;
import org.properti.analisa.financialcheck.utils.DialogUtils;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

public class SplashScreenActivity extends AppCompatActivity {

    final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        String lang;
        if(pref != null){
            lang = pref.getString("language", "");
        }
        else{
            lang = "en";
        }
        LocalizationUtils.setLocale(lang, getBaseContext());

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                else {
                    DialogUtils.showAlertDialog(SplashScreenActivity.this, "No Internet Connection", "You're internet is disconnected");
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public void setLangPref(String lang){
        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
