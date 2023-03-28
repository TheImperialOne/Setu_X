package com.imperial.setux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PatientLoginActivity extends AppCompatActivity {
    EditText t1;
    Button b1;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);
        t1 = findViewById(R.id.t1);
        b1 = findViewById(R.id.b1);
        String aadhaar = "286549103713";

        b1.setOnClickListener(view -> {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(aadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    String number = documentSnapshot.getString("Phone");
                    Intent intent = new Intent(PatientLoginActivity.this, ManageOTP.class);
                    intent.putExtra("mobile", number);
                    intent.putExtra("aadhaar", aadhaar);
                    startActivity(intent);
                }else
                    Toast.makeText(getApplicationContext(), "Phone number not found", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to fetch Phone number", Toast.LENGTH_LONG).show());
        });
    }
}