package com.imperial.setux.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.RecyclerRowAdapter;
import com.imperial.setux.RowModel;

import java.util.ArrayList;

public class MedicalHistoryActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference ;
    CollectionReference historyReference;
    RecyclerView recyclerView;
    ArrayList<RowModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        String getAadhaar= getIntent().getStringExtra("aadhaar");
        documentReference = firebaseFirestore.collection("Users").document(String.valueOf("286549103713"));
        historyReference = documentReference.collection("Medical History");
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PatientDiagnosis row = documentSnapshot.toObject(PatientDiagnosis.class);
                row.setDocumentId(documentSnapshot.getId());

                String documentId = row.getDocumentId();
                String Date = row.getDate();
                String Hospital = row.getHospital();
                arrayList.add(new RowModel(Date, Hospital));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this, arrayList);
        recyclerView.setAdapter(recyclerRowAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        historyReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PatientDiagnosis row = documentSnapshot.toObject(PatientDiagnosis.class);
                row.setDocumentId(documentSnapshot.getId());

                String documentId = row.getDocumentId();
                String Date = row.getDate();
                String Hospital = row.getHospital();
                arrayList.add(new RowModel(Date, Hospital));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this, arrayList);
        recyclerView.setAdapter(recyclerRowAdapter);
    }
}