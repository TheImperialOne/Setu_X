package com.imperial.setux.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPatientActivity extends AppCompatActivity {

    EditText inputAadhaar, inputOTP;
    Button sendOTP, verifyButton;
    FirebaseFirestore firebaseFirestore;
    String getHospitalName;
    String phonenumber;
    String otpid;
    String getAadhaar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    LinearLayout linearLayout;
    private String TAG = "VerifyPatientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_patient);
        inputAadhaar = (EditText) findViewById(R.id.aadhaarInput);
        sendOTP = (Button) findViewById(R.id.sendOTP);
        verifyButton = (Button) findViewById(R.id.verify_button);
        firebaseFirestore = FirebaseFirestore.getInstance();
        getHospitalName = getIntent().getStringExtra("HospitalName");
        linearLayout = findViewById(R.id.otpLayout);

        sendOTP.setOnClickListener(view -> {
            getAadhaar = inputAadhaar.getText().toString().trim();
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String number = documentSnapshot.getString("Phone");
                    phonenumber = "+91" + number;
                    initiateotp();
                    linearLayout.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getApplicationContext(), "Phone number not found", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch Phone number", Toast.LENGTH_LONG).show());
        });

        verifyButton.setOnClickListener(view -> {
            inputOTP = (EditText) findViewById(R.id.inputOTP);
            if (inputOTP.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
            else if (inputOTP.getText().toString().length() != 6)
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
            else {
                Intent intent = new Intent(VerifyPatientActivity.this, AddPatientInfoActivity.class);
                intent.putExtra("hospitalName", getHospitalName);
                intent.putExtra("aadhaar", getAadhaar);
                startActivity(intent);

            }

        });
    }

    private void initiateotp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Intent intent = new Intent(VerifyPatientActivity.this, AddPatientInfoActivity.class);
                        intent.putExtra("hospitalName", getHospitalName);
                        intent.putExtra("aadhaar", getAadhaar);
                        startActivity(intent);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                });        // OnVerificationStateChangedCallbacks

    }
}