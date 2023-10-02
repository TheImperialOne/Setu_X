package com.imperial.setux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imperial.setux.hospital.HospitalDashboardActivity;
import com.imperial.setux.patient.PatientDashboard;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(() -> {
            if (firebaseUser != null) {
                if (firebaseUser.getEmail() != null) {
                    Intent intent = new Intent(getApplicationContext(), HospitalDashboardActivity.class);
                    startActivity(intent);
                }
                if (firebaseUser.getPhoneNumber() != null) {
                    Intent intent = new Intent(getApplicationContext(), PatientDashboard.class);
                    startActivity(intent);
                }
            }
            Intent loginIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(loginIntent);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}