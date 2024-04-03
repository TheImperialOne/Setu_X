package com.imperial.setux.patient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.imperial.setux.R;
import com.imperial.setux.hospital.AddDiagnosisActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MedicalHistoryActivity extends AppCompatActivity implements SelectListener {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    CollectionReference historyReference;
    RecyclerView recyclerView;
    ArrayList<PatientDiagnosis> arrayList = new ArrayList<>();
    private String TAG = "MedicalHistoryActivity";
    CardView goBack;
    StorageReference storageReference;
    String getAadhaar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        getAadhaar = getIntent().getStringExtra("aadhaar");
        documentReference = firebaseFirestore.collection("Users").document(String.valueOf(getAadhaar));
        historyReference = documentReference.collection("Medical History");
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
        recyclerView.setAdapter(recyclerRowAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        ImageView imageView = popupView.findViewById(R.id.imgView);
        LinearLayout imageGroup = popupView.findViewById(R.id.imageGroup);
        Log.d("MedicalHistoryActivity.java", getAadhaar);

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://setux-1881.appspot.com/images/"+getAadhaar+"/"+patientDiagnosis.getDocumentID());
        /*storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://setux-1881.appspot.com/images/286549103713/frMiUKJLNzo7RRJh1VFe");*/

        try {
            File localFile = File.createTempFile("tempfile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                imageGroup.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "No documents to show", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dateMaterialTextView.setText(patientDiagnosis.getDate());
        hospitalNameMaterialTextView.setText(patientDiagnosis.getHospitalName());
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