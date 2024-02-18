package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.RecyclerRowAdapter;
import com.imperial.setux.SelectListener;
import com.imperial.setux.patient.PatientDiagnosis;

import java.util.ArrayList;

public class PatientMedicalHistory extends AppCompatActivity implements SelectListener {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    CollectionReference historyReference;
    RecyclerView recyclerView;
    ArrayList<PatientDiagnosis> arrayList = new ArrayList<>();
    Button addRecord;
    private String TAG = "MedicalHistoryActivity";
    CardView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical_history);
        String getAadhaar = getIntent().getStringExtra("aadhaar");
        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalEmail = getIntent().getStringExtra("hospitalEmail");
        String bucketID = getIntent().getStringExtra("bucketID");
        documentReference = firebaseFirestore.collection("Users").document(String.valueOf(getAadhaar));
        historyReference = documentReference.collection("Medical History");
        recyclerView = findViewById(R.id.recycleView);
        addRecord = findViewById(R.id.addNew);
        goBack = findViewById(R.id.back);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        goBack.setOnClickListener(view -> {
            onBackPressed();
        });
        addRecord.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddDiagnosisActivity.class).putExtra("aadhaar", getAadhaar).putExtra("hospitalName", hospitalName).putExtra("hospitalEmail", hospitalEmail).putExtra("bucketID", bucketID));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        historyReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            arrayList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PatientDiagnosis patient = documentSnapshot.toObject(PatientDiagnosis.class);
                patient.setDocumentID(documentSnapshot.getId());
                String DocumentID = patient.getDocumentID();
                String Date = patient.getDate();
                String HospitalName = patient.getHospitalName();
                String Diagnosis = patient.getDiagnosis();
                String Details = patient.getDetails();
                String Prescription = patient.getPrescription();
                String Doctor = patient.getDoctor();
                String Treatment = patient.getTreatment();
                arrayList.add(new PatientDiagnosis(Date, HospitalName, Details, Diagnosis, Prescription, DocumentID, Doctor, Treatment));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this, arrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerRowAdapter);
    }

    @Override
    public void onItemClicked(PatientDiagnosis patientDiagnosis, View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_history, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        recyclerView.setVisibility(View.INVISIBLE);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        MaterialTextView dateMaterialTextView = popupView.findViewById(R.id.dateTextView);
        MaterialTextView hospitalNameMaterialTextView = popupView.findViewById(R.id.hospitalNameTextView);
        MaterialTextView diagnosisMaterialTextView = popupView.findViewById(R.id.diagnosisTextView);
        MaterialTextView detailsMaterialTextView = popupView.findViewById(R.id.detailsTextView);
        MaterialTextView prescriptionMaterialTextView = popupView.findViewById(R.id.prescriptionTextView);
        MaterialTextView documentIDMaterialTextView = popupView.findViewById(R.id.documentIDTextView);
        MaterialTextView treatmentMaterialTextView = popupView.findViewById(R.id.treatmentTextView);

        dateMaterialTextView.setText(patientDiagnosis.getDate());
        hospitalNameMaterialTextView.setText(patientDiagnosis.getHospitalName());
        diagnosisMaterialTextView.setText(patientDiagnosis.getDiagnosis());
        detailsMaterialTextView.setText(patientDiagnosis.getDetails());
        prescriptionMaterialTextView.setText(patientDiagnosis.getPrescription());
        documentIDMaterialTextView.setText(patientDiagnosis.getDocumentID());
        treatmentMaterialTextView.setText(patientDiagnosis.getTreatment());
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }
}