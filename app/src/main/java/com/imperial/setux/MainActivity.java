package com.imperial.setux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(loginIntent);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}