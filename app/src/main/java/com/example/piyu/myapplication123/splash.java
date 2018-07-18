package com.example.piyu.myapplication123;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class splash extends AppCompatActivity {
    public static final String My_PREFS_NAME = "MyPrefsFile";
    String nam, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2 * 1000);
                    SharedPreferences pref = getSharedPreferences(My_PREFS_NAME, MODE_PRIVATE);
                    nam = pref.getString("username", "0");
                    pass = pref.getString("password", "0");
                    if (nam == "0" && pass == "0") {
                        startActivity(new Intent(getApplicationContext(), login.class));

                    } else {
                        startActivity(new Intent(getApplicationContext(), Main.class));
                    }
                    finish();
                } catch (Exception e) {

                }
            }
        };
        background.start();
    }
}
