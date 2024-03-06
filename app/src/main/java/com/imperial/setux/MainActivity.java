package com.imperial.setux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imperial.setux.hospital.HospitalDashboardActivity;
import com.imperial.setux.patient.PatientDashboard;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 3000;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;

    SharedPreferences loginPreferences;
    String SHARED_PREF_NAME = "loginPreferences", GET_AADHAAR = "getAadhaar";
    String getAadhaar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(() -> {
            loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            firebaseAuth = FirebaseAuth.getInstance();
            getAadhaar = loginPreferences.getString(GET_AADHAAR, null);
            if (getAadhaar != null) {
                Intent intent = new Intent(getApplicationContext(), PatientDashboard.class);
                startActivity(intent);
            } else if (firebaseAuth.getCurrentUser() != null) {
                Intent intent = new Intent(getApplicationContext(), HospitalDashboardActivity.class);
                startActivity(intent);
            } else {
                Intent loginIntent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}