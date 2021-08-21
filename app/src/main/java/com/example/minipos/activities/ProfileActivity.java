package com.example.minipos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.minipos.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void baxkGo(View view) {
        onBackPressed();
    }

    public void logoutMe(View view) {
        Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
    }

    public void pickProfileImagex(View view) {
        Toast.makeText(this, "pick profile image", Toast.LENGTH_SHORT).show();
    }
}