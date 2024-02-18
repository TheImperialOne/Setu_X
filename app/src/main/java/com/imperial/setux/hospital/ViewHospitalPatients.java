package com.imperial.setux.hospital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.patient.Patient;
import com.imperial.setux.patient.PatientDiagnosis;
import com.imperial.setux.patient.RecyclerRowAdapter;
import com.imperial.setux.patient.SelectListener;

import java.util.ArrayList;
import java.util.Objects;

public class ViewHospitalPatients extends AppCompatActivity implements SelectListener {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference historyReference;
    String hospitalEmail, getAadhaar;
    RecyclerView recyclerView;
    ArrayList<PatientDiagnosis> arrayList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String TAG = "ViewHospitalPatients";
    CardView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hospital_patients);
        getAadhaar = getIntent().getStringExtra("aadhaar");
        hospitalEmail =  user.getEmail();
        Log.d(TAG, "HospitalEmail: " + hospitalEmail);
        String bucketID = getIntent().getStringExtra("bucketID");
        assert hospitalEmail != null;
        historyReference = firebaseFirestore.collection("Hospitals").document(Objects.requireNonNull(user.getEmail())).collection("Patient Data").document(getAadhaar).collection("Medical History");
        recyclerView = findViewById(R.id.recycleView);
        goBack = findViewById(R.id.back);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        goBack.setOnClickListener(view -> {
            onBackPressed();
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

    @SuppressLint("ClickableViewAccessibility")
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
        //hospitalNameMaterialTextView.setText(patientDiagnosis.getHospitalName());
        hospitalNameMaterialTextView.setVisibility(View.GONE);
        diagnosisMaterialTextView.setText(patientDiagnosis.getDiagnosis());
        detailsMaterialTextView.setText(patientDiagnosis.getDetails());
        prescriptionMaterialTextView.setText(patientDiagnosis.getPrescription());
        documentIDMaterialTextView.setText(patientDiagnosis.getDocumentID());
        treatmentMaterialTextView.setText(patientDiagnosis.getTreatment());
        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            recyclerView.setVisibility(View.VISIBLE);
            return true;
        });
    }
}