package com.imperial.setux;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PatientDashboard extends AppCompatActivity {
    Button btn;
    TextView textViewData;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    String getAadhaar= getIntent().getStringExtra("aadhaar");
    DocumentReference documentReference = firebaseFirestore.collection("Users").document(String.valueOf("286549103713"));
    CollectionReference historyReference = documentReference.collection("Medical History");
    private static final String TAG = "PatientDashboard";
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
        setContentView(R.layout.activity_patient_dashboard);
        btn = findViewById(R.id.Logout);
        textViewData = findViewById(R.id.text_view_data);
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                        Toast.makeText(PatientDashboard.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PatientDashboard.this, "Error!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                });
        btn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
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
                Patient row = documentSnapshot.toObject(Patient.class);
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