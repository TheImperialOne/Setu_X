package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;
import com.imperial.setux.hospital.AddPatientInfoActivity;

import java.util.HashMap;
import java.util.Map;

public class VerifyPatientActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private MaterialButton verifyPatientBtn;
    private MaterialButton proceedToDetailsBtn;
    private String lastVerifiedPatientEmail, hospitalEmail, hospitalName; // Store last verified email

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_patient);

        hospitalName = getIntent().getStringExtra("hospitalName");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        hospitalEmail = currentUser.getEmail();

        emailInput = findViewById(R.id.emailInput);
        verifyPatientBtn = findViewById(R.id.verifyPatientBtn);

        firestore = FirebaseFirestore.getInstance();
        proceedToDetailsBtn = findViewById(R.id.proceedToDetailsBtn);
        proceedToDetailsBtn.setOnClickListener(v -> checkAccessApproval());


        verifyPatientBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfPatientExists(email);
        });
    }

    private void checkIfPatientExists(String email) {
        firestore.collection("Patients")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        lastVerifiedPatientEmail = email;
                        sendAccessRequestToPatient(email);
                    } else {
                        Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error accessing database", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendAccessRequestToPatient(String patientEmail) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String hospitalEmail = currentUser.getEmail();
        if (hospitalEmail == null || hospitalEmail.isEmpty()) {
            Toast.makeText(this, "Failed to get hospital email", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("hospitalEmail", hospitalEmail);
        request.put("timestamp", System.currentTimeMillis());
        request.put("status", "pending");

        DocumentReference reqRef = firestore
                .collection("Patients")
                .document(patientEmail)
                .collection("AccessRequests")
                .document(hospitalEmail);

        reqRef.set(request)
                .addOnSuccessListener(unused -> {
                    verifyPatientBtn.setVisibility(View.GONE);
                    Toast.makeText(this, "Request sent to patient", Toast.LENGTH_SHORT).show();
                    proceedToDetailsBtn.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send request", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAccessApproval() {
        if (lastVerifiedPatientEmail == null) {
            proceedToDetailsBtn.setVisibility(View.GONE);
            verifyPatientBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Verify a patient first", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String hospitalEmail = currentUser.getEmail();

        firestore.collection("Patients")
                .document(lastVerifiedPatientEmail)
                .collection("AccessRequests")
                .document(hospitalEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("approved".equalsIgnoreCase(status)) {
                            navigateToPatientDetails(lastVerifiedPatientEmail);
                        } else {
                            Toast.makeText(this, "Access still pending", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No access request found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking access", Toast.LENGTH_SHORT).show();
                });
    }


    private void navigateToPatientDetails(String patientEmail) {
        Intent intent = new Intent(this, AddPatientInfoActivity.class);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("hospitalEmail", hospitalEmail);
        intent.putExtra("patientEmail", patientEmail);
        startActivity(intent);
    }
}
