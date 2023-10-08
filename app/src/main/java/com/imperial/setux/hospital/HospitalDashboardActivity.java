package com.imperial.setux.hospital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;

public class HospitalDashboardActivity extends AppCompatActivity {
    MaterialTextView hospitalName, hospitalRegn, hospitalEmail;
    String getEmail;
    Button btnLogOut;
    FirebaseAuth mAuth;
    Button addNewPatient;
    CollectionReference collectionReference;
    String HospitalName, Email, Registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
        hospitalName = findViewById(R.id.name);
        hospitalRegn = findViewById(R.id.regn);
        hospitalEmail = findViewById(R.id.email);
        addNewPatient = findViewById(R.id.addNew);
        btnLogOut = findViewById(R.id.logout_button);
        getEmail = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        });
        addNewPatient.setOnClickListener(view -> {
            startActivity(new Intent(HospitalDashboardActivity.this, VerifyPatientActivity.class).putExtra("HospitalName", HospitalName));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        }
        collectionReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Hospital hospital = documentSnapshot.toObject(Hospital.class);
                hospital.setDocumentId(documentSnapshot.getId());

                String documentId = hospital.getDocumentId();
                HospitalName = hospital.getHospitalName();
                Email = hospital.getEmail();
                Registration = hospital.getRegistration();
                hospitalName.setText(HospitalName);
                hospitalRegn.setText(Registration);
                hospitalEmail.setText(Email);
            }
        });
    }
    private long pressedTime;
    @Override
    public void onBackPressed(){
        if (pressedTime + 2000 > System.currentTimeMillis()){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}