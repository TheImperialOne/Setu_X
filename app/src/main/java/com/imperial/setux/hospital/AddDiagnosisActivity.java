package com.imperial.setux.hospital;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    String hospitalName;
    String getAadhaar;
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";
    private static final String DOCTOR = "Doctor";
    private static final String TREATMENT = "Treatment";
    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private Button doneButton;
    String date = df.format(new Date());
    CardView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diagnosis);
        editDiagnosis = findViewById(R.id.diagnosisInput);
        editDetails = findViewById(R.id.detailsInput);
        editPrescription = findViewById(R.id.prescriptionInput);
        editDoctor = findViewById(R.id.doctorInput);
        editTreatment = findViewById(R.id.treatmentInput);
        doneButton = findViewById(R.id.done_button);
        hospitalName = getIntent().getStringExtra("hospitalName");
        getAadhaar = getIntent().getStringExtra("aadhaar");
        goBack = findViewById(R.id.back);

        doneButton.setOnClickListener(view -> {
            String Diagnosis = editDiagnosis.getText().toString();
            String Details = editDetails.getText().toString();
            String Prescription = editPrescription.getText().toString();
            String Doctor = editDoctor.getText().toString();
            String Treatment = editTreatment.getText().toString();
            Map<String, Object> record = new HashMap<>();
            record.put(DATE, date);
            record.put(HOSPITALNAME, hospitalName);
            record.put(DIAGNOSIS, Diagnosis);
            record.put(DETAILS, Details);
            record.put(DOCTOR, Doctor);
            record.put(TREATMENT, Treatment);
            record.put(PRESCRIPTION, Prescription);

            db.collection("Users").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Record saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    });
        });

        goBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

}