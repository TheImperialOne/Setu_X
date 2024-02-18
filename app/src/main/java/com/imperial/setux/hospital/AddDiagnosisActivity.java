package com.imperial.setux.hospital;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;
import com.imperial.setux.patient.Patient;
import com.imperial.setux.patient.PatientDashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    String hospitalName, hospitalEmail;
    String getAadhaar;
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";
    private static final String DOCTOR = "Doctor";
    private static final String TREATMENT = "Treatment";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOODGROUP = "BloodGroup";
    private String Name, Aadhaar, DOB, Phone, Gender, BloodGroup;
    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    //
    DocumentReference documentReference;
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
        hospitalEmail = getIntent().getStringExtra("hospitalEmail");
        getAadhaar = getIntent().getStringExtra("aadhaar");
        documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
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
                        Toast.makeText(AddDiagnosisActivity.this, "Patient Record saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    });

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Patient patient = documentSnapshot.toObject(Patient.class);
                            Name = documentSnapshot.getString(NAME);
                            Aadhaar = documentSnapshot.getString(AADHAAR);
                            DOB = documentSnapshot.getString(DateOfBirth);
                            Phone = documentSnapshot.getString(PHONE);
                            Gender = documentSnapshot.getString(GENDER);
                            BloodGroup = documentSnapshot.getString(BLOODGROUP);
                            Map<String, Object> patientRecord = new HashMap<>();
                            patientRecord.put(NAME, Name);
                            patientRecord.put(AADHAAR, Aadhaar);
                            patientRecord.put(DateOfBirth, DOB);
                            patientRecord.put(PHONE, Phone);
                            patientRecord.put(GENDER, Gender);
                            patientRecord.put(BLOODGROUP, BloodGroup);
                            db.collection("Hospitals").document(hospitalEmail).collection("Patient Data").document(getAadhaar).set(patientRecord)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(AddDiagnosisActivity.this, "Hospital Record saved", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    });

                            db.collection("Hospitals").document(hospitalEmail).collection("Patient Data").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Hospital Record saved", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(AddDiagnosisActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });


        });

        goBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

}