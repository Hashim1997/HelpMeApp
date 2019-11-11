package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private final static int SPLASH_LENGTH=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        SharedPreferences loginPref=getSharedPreferences("login",MODE_PRIVATE);

        final String type=loginPref.getString("type","empty");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (type.equals("empty")){
                    Intent chooseAccount = new Intent(SplashScreen.this, AccountChooseActivity.class);
                    SplashScreen.this.startActivity(chooseAccount);
                    SplashScreen.this.finish();
                }
                else if (type.equals("Helpers")){
                    Intent helperHome = new Intent(SplashScreen.this, HelperHome.class);
                    SplashScreen.this.startActivity(helperHome);
                    SplashScreen.this.finish();
                }
                else if (type.equals("Users")){
                    Intent userIntent=new Intent(SplashScreen.this,UserHome.class);
                    SplashScreen.this.startActivity(userIntent);
                    SplashScreen.this.finish();
                }
            }
        },SPLASH_LENGTH);
    }
}
