package com.imperial.setux;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    //String hospitalName = getIntent().getStringExtra("hospitalName");
    //String getAadhaar = getIntent().getStringExtra("aadhaar");
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String NAME = "Name";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";

    private EditText editDiagnosis, editDetails, editPrescription;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diagnosis);
        date = new Date();
        editDiagnosis = findViewById(R.id.editDiagnosis);
        editDetails = findViewById(R.id.editDetails);
        editPrescription = findViewById(R.id.editPrescription);

    }

    public void saveNote(View v) {
        String Diagnosis = editDiagnosis.getText().toString();
        String Details = editDetails.getText().toString();
        String Prescription = editPrescription.getText().toString();
        Map<String, Object> record = new HashMap<>();
        record.put(DATE, date.toString());
        //record.put(NAME, hospitalName);
        record.put(DIAGNOSIS, Diagnosis);
        record.put(DETAILS, Details);
        record.put(PRESCRIPTION, Prescription);

        db.collection("Users").document("286549103713").collection("Medical History").add(record).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddDiagnosisActivity.this, "Record saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDiagnosisActivity.this, AddPatientInfoActivity.class));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });
    }
}