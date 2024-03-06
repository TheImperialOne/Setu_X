package com.imperial.setux.patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PatientLoginActivity extends AppCompatActivity {
    TextInputEditText getAadhaar, getOTP;
    SharedPreferences loginPreferences;
    private static final String SHARED_PREF_NAME = "loginPreferences", GET_AADHAAR = "getAadhaar";
    Button triggerOTP, login;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String otpID;
    FirebaseAuth firebaseAuth;
    String aadhaar;
    LinearLayout otpLayout;
    private final String TAG = "PatientLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);
        getAadhaar = findViewById(R.id.aadhaarInput);
        getOTP = findViewById(R.id.OTP);
        triggerOTP = findViewById(R.id.getOTP);
        login = findViewById(R.id.login_button);
        firebaseAuth = FirebaseAuth.getInstance();
        otpLayout = findViewById(R.id.otpLayout);
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        triggerOTP.setOnClickListener(view -> {
            aadhaar = Objects.requireNonNull(getAadhaar.getText()).toString().trim();
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(aadhaar);
            Log.d(TAG, aadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String phoneNumber = "+91" + documentSnapshot.getString("Phone");
                    initiateOTP(phoneNumber);
                    Log.d(TAG, phoneNumber);
                    otpLayout.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getApplicationContext(), "Phone number not found", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch Phone number", Toast.LENGTH_LONG).show());
        });
        login.setOnClickListener(view -> {
            if (getOTP.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
            else if (getOTP.getText().toString().length() != 6)
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpID, getOTP.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }

        });
    }

    private void initiateOTP(String phoneNumber) {
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
                        Log.d(TAG, e.toString());
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), PatientDashboard.class);
                        intent.putExtra("aadhaar", aadhaar);
                        Log.d("PatientLoginActivity", aadhaar);
                        SharedPreferences.Editor editor = loginPreferences.edit();
                        editor.putString(GET_AADHAAR, aadhaar);
                        editor.apply();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Code Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}