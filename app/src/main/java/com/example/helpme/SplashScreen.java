package com.example.helpme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

//a main activity to show splash after launch
public class SplashScreen extends AppCompatActivity {

    //variable to splash length in millisecond
    private final static int SPLASH_LENGTH=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //check if data exist go to home activity else open account choose activity
        SharedPreferences loginPref=getSharedPreferences("login",MODE_PRIVATE);

        final String type=loginPref.getString("type","empty");

        /*
        New handler to start the activity and close it after timeout
        if type empty go to account choose activity
        else if user go to user home
        else go to helper home
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case "empty":
                        Intent chooseAccount = new Intent(SplashScreen.this, AccountChooseActivity.class);
                        SplashScreen.this.startActivity(chooseAccount);
                        SplashScreen.this.finish();
                        break;
                    case "Helpers":
                        Intent helperHome = new Intent(SplashScreen.this, HelperHome.class);
                        SplashScreen.this.startActivity(helperHome);
                        SplashScreen.this.finish();
                        break;
                    case "Users":
                        Intent userIntent = new Intent(SplashScreen.this, UserHome.class);
                        SplashScreen.this.startActivity(userIntent);
                        SplashScreen.this.finish();
                        break;
                }
            }
        },SPLASH_LENGTH);
    }
}
