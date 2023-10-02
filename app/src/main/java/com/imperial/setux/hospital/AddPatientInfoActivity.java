package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.patient.Patient;
import com.imperial.setux.R;
import com.imperial.setux.RecyclerRowAdapter;
import com.imperial.setux.RowModel;
import com.imperial.setux.patient.PatientDiagnosis;

import java.util.ArrayList;

public class AddPatientInfoActivity extends AppCompatActivity {
    Button b1;
    TextView textViewData;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference = firebaseFirestore.collection("Users").document(String.valueOf("286549103713"));
    CollectionReference historyReference = documentReference.collection("Medical History");
    String hospitalName;
    String getAadhaar;
    private static final String TAG = "AddPatientInfoActivity";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";
    RecyclerView recyclerView;
    ArrayList<RowModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_info);
        textViewData = findViewById(R.id.text_view_data);
        hospitalName = getIntent().getStringExtra("hospitalName");
        getAadhaar = getIntent().getStringExtra("aadhaar");
        b1 = findViewById(R.id.addDiagnosis);
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        b1.setOnClickListener(view -> startActivity(new Intent(AddPatientInfoActivity.this, AddDiagnosisActivity.class).putExtra("hospitalName", hospitalName).putExtra("aadhaar",getAadhaar)));
        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String Name = documentSnapshot.getString(NAME);
                        String Aadhaar = documentSnapshot.getString(AADHAAR);
                        String DOB = documentSnapshot.getString(DateOfBirth);
                        String Phone = documentSnapshot.getString(PHONE);
                        String Gender = documentSnapshot.getString(GENDER);
                        String BloodGroup = documentSnapshot.getString(BLOOD);

                        textViewData.setText("Name: " + Name + "\n" + "Aadhaar: " + Aadhaar + "\n" + "Date of Birth: " + DOB + "\n" + "Phone: " + Phone + "\n" + "Gender: " + Gender + "\n" + "Blood Group: " + BloodGroup);

                    } else {
                        Toast.makeText(AddPatientInfoActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPatientInfoActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        historyReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PatientDiagnosis patient = documentSnapshot.toObject(PatientDiagnosis.class);
                patient.setDocumentId(documentSnapshot.getId());

                String documentId = patient.getDocumentId();
                String Date = patient.getDate();
                String Hospital = patient.getHospital();
                arrayList.add(new RowModel(Date, Hospital));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this, arrayList);
        recyclerView.setAdapter(recyclerRowAdapter);
    }
}