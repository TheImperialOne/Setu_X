package com.imperial.setux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerifyUserActivity extends AppCompatActivity {

    EditText t1;
    Button b1;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    //Hospital hospital = (Hospital) getIntent().getSerializableExtra("Hospital");
    EditText t2;
    Button b2;
    String phonenumber;
    String otpid;
    String Aadhaar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);
        t1 = (EditText) findViewById(R.id.t1);
        b1 = (Button) findViewById(R.id.b1);
        t2 = (EditText) findViewById(R.id.t2);
        b2 = (Button) findViewById(R.id.b2);
        Aadhaar = "286549103713";

        b1.setOnClickListener(view -> {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Aadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    String number = documentSnapshot.getString("Phone");
                    phonenumber = "+91"+number;
                    initiateotp();
                }else
                    Toast.makeText(getApplicationContext(), "Phone number not found", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch Phone number", Toast.LENGTH_LONG).show());
        });

        b2.setOnClickListener(view -> {

            if (t2.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_LONG).show();
            else if (t2.getText().toString().length() != 6)
                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, t2.getText().toString());
                signInWithPhoneAuthCredential(credential);
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
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(VerifyUserActivity.this, AddPatientInfoActivity.class);
                        //intent.putExtra("hospitalName", hospital.getHospitalName());
                        //intent.putExtra("aadhaar", Aadhaar);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Code Error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}