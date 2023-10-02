package com.imperial.setux.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.imperial.setux.R;

import java.util.concurrent.TimeUnit;

public class ManageOTP extends AppCompatActivity {

    EditText t2;
    Button b2;
    String phoneNumber, aadhaar;
    String otpID;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);
        phoneNumber = "+91"+getIntent().getStringExtra("mobile");
        aadhaar = getIntent().getStringExtra("aadhaar");
        t2 = findViewById(R.id.t2);
        b2 = findViewById(R.id.b2);
        mAuth = FirebaseAuth.getInstance();

        initiateOTP();

        b2.setOnClickListener(view -> {

            if (t2.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
            else if (t2.getText().toString().length() != 6)
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpID, t2.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }

        });
    }

    private void initiateOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpID = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ManageOTP.this, PatientDashboard.class);
                        intent.putExtra("aadhaar",aadhaar);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Code Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}