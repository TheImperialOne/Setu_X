package com.imperial.setux;

import static java.sql.Types.NULL;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class UserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    Button b1, b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);


        b1.setOnClickListener(view -> {
            Intent intent = new Intent(UserActivity.this, PatientLoginActivity.class);
            startActivity(intent);
        });
        b2.setOnClickListener(view -> {
            Intent intent = new Intent(UserActivity.this, HospitalLoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getEmail() != null) {
                Intent intent = new Intent(UserActivity.this, HospitalLoginActivity.class);
                startActivity(intent);
            }
            if (firebaseUser.getPhoneNumber() != null) {
                Intent intent = new Intent(UserActivity.this, PatientDashboard.class);
                startActivity(intent);
            }
        }
    }
}