package com.example.minipos.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.minipos.R;
import com.example.minipos.activities.DashboardActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SLIDE_TIMER = 3000;

    SharedPreferences sharedPreferencesOnboardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferencesOnboardingScreen = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

                boolean isFirstTimeUser = sharedPreferencesOnboardingScreen.getBoolean("firstTime",true);
                if(isFirstTimeUser){

                    SharedPreferences.Editor editor = sharedPreferencesOnboardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    //start the next actitivty after 5 seconds
                    startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }


            }
        },SLIDE_TIMER) ;

    }
}