package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

public class AddPatientInfoActivity extends AppCompatActivity {

    Button viewHistory;
    MaterialTextView userName, userDOB, userBloodGroup, userPhone, userGender;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String hospitalName, hospitalEmail;
    String getAadhaar;
    CardView cardView;
    private static final String TAG = "AddPatientInfoActivity";
    private static final String NAME = "FullName";
    private static final String DateOfBirth = "DOB";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_info);
        hospitalName = getIntent().getStringExtra("hospitalName");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        hospitalEmail = currentUser.getEmail();
        String patientEmail = getIntent().getStringExtra("patientEmail");
        if (patientEmail == null || patientEmail.isEmpty()) {
            Toast.makeText(this, "No patient email provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        documentReference = firebaseFirestore.collection("Patients").document(patientEmail);
        userName = findViewById(R.id.userName);
        userDOB = findViewById(R.id.DOB);
        userGender = findViewById(R.id.gender);
        userBloodGroup = findViewById(R.id.bloodGroup);
        userPhone = findViewById(R.id.phoneNumber);
        viewHistory = findViewById(R.id.viewHistoryButton);
        cardView = findViewById(R.id.back);
        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String Name = documentSnapshot.getString(NAME);
                        String DOB = documentSnapshot.getString(DateOfBirth);
                        String Phone = documentSnapshot.getString(PHONE);
                        String Gender = documentSnapshot.getString(GENDER);
                        String BloodGroup = documentSnapshot.getString(BLOOD);
                        userName.setText(Name);
                        userPhone.setText(Phone);
                        userDOB.setText(DOB);
                        userBloodGroup.setText(BloodGroup);
                        userGender.setText(Gender);

                    } else {
                        Toast.makeText(AddPatientInfoActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPatientInfoActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });

        viewHistory.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(), PatientMedicalHistory.class).putExtra("hospitalName",hospitalName).putExtra("hospitalEmail",hospitalEmail));
        });
        cardView.setOnClickListener(view->{
            onBackPressed();
        });
    }
}