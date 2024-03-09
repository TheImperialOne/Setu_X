package com.imperial.setux.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;
import com.imperial.setux.patient.Patient;
import com.imperial.setux.patient.PatientDashboard;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class HospitalDashboardActivity extends AppCompatActivity implements SelectListener {
    MaterialTextView hospitalName, hospitalRegn, hospitalEmail, patientCount;
    String getEmail;
    Button btnLogOut;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button addNewPatient;
    CollectionReference collectionReference, patientReference;
    DocumentReference hospitalReference;
    String HospitalName, HospitalEmail, Registration;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Patient> arrayList = new ArrayList<>();
    private final String TAG = "HospitalDashboardActivity";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String REGISTRATION_NO = "Registration";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
        hospitalName = findViewById(R.id.name);
        hospitalRegn = findViewById(R.id.regn);
        hospitalEmail = findViewById(R.id.email);
        patientCount = findViewById(R.id.count);
        addNewPatient = findViewById(R.id.addNew);
        btnLogOut = findViewById(R.id.logout_button);
        recyclerView = findViewById(R.id.recycleView);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        HospitalEmail =  user.getEmail();
        getEmail = getIntent().getStringExtra("email");
        collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");
        hospitalReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail);
        patientReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail).collection("Patient Data");
        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        });
        addNewPatient.setOnClickListener(view -> {
            startActivity(new Intent(HospitalDashboardActivity.this, VerifyPatientActivity.class).putExtra("HospitalName", HospitalName).putExtra("HospitalEmail", HospitalEmail));
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "HospitalEmail: " + HospitalEmail);
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        } else{
            hospitalReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail);
            hospitalReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            HospitalName = documentSnapshot.getString(HOSPITALNAME);
                            Registration = documentSnapshot.getString(REGISTRATION_NO);
                            hospitalName.setText(HospitalName);
                            hospitalRegn.setText(Registration);
                            hospitalEmail.setText(HospitalEmail);
                        } else {
                            Toast.makeText(getApplicationContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });
            Query query = db.collection("Hospitals").document(HospitalEmail).collection("Patient Data");
            AggregateQuery countQuery = query.count();
            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    patientCount.setText((String.valueOf(snapshot.getCount())));
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            });
        }
        patientReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            arrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String Name = documentSnapshot.getString(NAME);
                String Aadhaar = documentSnapshot.getString(AADHAAR);
                String DOB = documentSnapshot.getString(DateOfBirth);
                String Phone = documentSnapshot.getString(PHONE);
                String Gender = documentSnapshot.getString(GENDER);
                String BloodGroup = documentSnapshot.getString(BLOOD);
                arrayList.add(new Patient(Aadhaar, BloodGroup,DOB, Gender, Name, Phone));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this,arrayList,this);
        recyclerView.setAdapter(recyclerRowAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

       /* collectionReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Hospital hospital = documentSnapshot.toObject(Hospital.class);
                hospital.setDocumentId(documentSnapshot.getId());
                HospitalName = hospital.getHospitalName();
                HospitalEmail = hospital.getEmail();
                Registration = hospital.getRegistration();
                hospitalName.setText(HospitalName);
                hospitalRegn.setText(Registration);
                hospitalEmail.setText(HospitalEmail);
            }
            Query query = db.collection("Hospitals").document(HospitalEmail).collection("Patient Data");
            AggregateQuery countQuery = query.count();
            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    patientCount.setText((String.valueOf(snapshot.getCount())));
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            });
        });*/
    }


    private long pressedTime;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    public void onItemClicked(Patient patient, View view) {
        Log.d(TAG, "Aadhaar: " + patient.getAadhaarNo());
        startActivity(new Intent(getApplicationContext(), ViewHospitalPatients.class).putExtra("aadhaar",patient.getAadhaarNo()));
    }
}