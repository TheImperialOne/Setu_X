package com.imperial.setux.hospital;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    private String hospitalName, getAadhaar, getDate, getDocumentID;
    private CollectionReference historyReference;
    private DocumentReference documentReference;
    private Boolean getEditStatus;
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String HOSPITAL_NAME = "HospitalName";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";
    private static final String DOCTOR = "Doctor";
    private static final String TREATMENT = "Treatment";
    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private Button doneButton;
    private String date = df.format(new Date());
    private CardView goBack;
    private String Diagnosis, Details, Doctor, Prescription, Treatment;

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
        getDate = getIntent().getStringExtra("date");
        getDocumentID = getIntent().getStringExtra("documentID");
        getEditStatus = getIntent().getBooleanExtra("editStatus", true);
        documentReference = firebaseFirestore.collection("Users").document(String.valueOf(getAadhaar));
        historyReference = documentReference.collection("Medical History");
        goBack = findViewById(R.id.back);

        if (getEditStatus) uploadEditData();

        doneButton.setOnClickListener(view -> {
            Diagnosis = editDiagnosis.getText().toString();
            Details = editDetails.getText().toString();
            Prescription = editPrescription.getText().toString();
            Doctor = editDoctor.getText().toString();
            Treatment = editTreatment.getText().toString();
            Map<String, Object> record = new HashMap<>();
            if (getEditStatus) record.put(DATE, getDate);
            else record.put(DATE, date);
            record.put(HOSPITAL_NAME, hospitalName);
            record.put(DIAGNOSIS, Diagnosis);
            record.put(DETAILS, Details);
            record.put(DOCTOR, Doctor);
            record.put(TREATMENT, Treatment);
            record.put(PRESCRIPTION, Prescription);
            if (!getEditStatus) {
                firebaseFirestore.collection("Users").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddDiagnosisActivity.this, "Record saved", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                historyReference.document(getDocumentID).update(record).addOnSuccessListener(unused -> {
                    Toast.makeText(AddDiagnosisActivity.this, "Record saved", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show());
            }

        });

        goBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    public void uploadEditData() {
        historyReference.document(getDocumentID).get().addOnSuccessListener(documentSnapshot -> {
            editDiagnosis.setText(documentSnapshot.getString(DIAGNOSIS));
            editDetails.setText(documentSnapshot.getString(DETAILS));
            editDoctor.setText(documentSnapshot.getString(DOCTOR));
            editTreatment.setText(documentSnapshot.getString(TREATMENT));
            editPrescription.setText(documentSnapshot.getString(PRESCRIPTION));
        });
    }

}