package com.imperial.setux.patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

public class PatientDashboard extends AppCompatActivity {
    Button btnLogout, btnMedicalHistory;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
    DocumentReference documentReference;
    MaterialTextView setDOB, setBloodGroup, setGender, setPhoneNumber, setAadhaarNumber, setAddress, setName;
    private static final String TAG = "PatientDashboard";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";
    private String getAadhaar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        getAadhaar= getIntent().getStringExtra("aadhaar");
        btnLogout = findViewById(R.id.logout_button);
        documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
        btnMedicalHistory = findViewById(R.id.viewHistoryButton);
        setName = findViewById(R.id.name);
        setDOB = findViewById(R.id.DOB);
        setBloodGroup = findViewById(R.id.bloodGroup);
        setGender = findViewById(R.id.gender);
        setPhoneNumber = findViewById(R.id.phoneNumber);
        setAadhaarNumber = findViewById(R.id.aadhaarNumber);
        setAddress = findViewById(R.id.address);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String Name = documentSnapshot.getString(NAME);
                        String Aadhaar = documentSnapshot.getString(AADHAAR);
                        String DOB = documentSnapshot.getString(DateOfBirth);
                        String Phone = documentSnapshot.getString(PHONE);
                        String Gender = documentSnapshot.getString(GENDER);
                        String BloodGroup = documentSnapshot.getString(BLOOD);
                        setName.setText(Name);
                        setAadhaarNumber.setText(Aadhaar);
                        setPhoneNumber.setText(Phone);
                        setDOB.setText(DOB);
                        setGender.setText(Gender);
                        setBloodGroup.setText(BloodGroup);
                    } else {
                        Toast.makeText(PatientDashboard.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PatientDashboard.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
        btnMedicalHistory.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(),MedicalHistoryActivity.class).putExtra("aadhaar",getAadhaar));
        });
    }
    private long pressedTime;
    @Override
    public void onBackPressed(){
        if (pressedTime + 2000 > System.currentTimeMillis()){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}