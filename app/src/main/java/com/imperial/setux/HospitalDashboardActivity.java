package com.imperial.setux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HospitalDashboardActivity extends AppCompatActivity {

    private static final String TAG = "HospitalDashboardActivity";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String EMAIL = "Email";
    private static final String REGISTRATION = "Registration";
    //String getEmail= getIntent().getStringExtra("email");
    Button btnLogOut;
    FirebaseAuth mAuth;
    Button addNewPatient;
    TextView textViewData;
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
        textViewData = findViewById(R.id.textView);
        addNewPatient = findViewById(R.id.addPatient);
        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        final String[] HospitalName = new String[1];
        final String[] Registration = new String[1];
        final String[] Email = new String[1];

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        });
        addNewPatient.setOnClickListener(view -> {
            startActivity(new Intent(HospitalDashboardActivity.this, VerifyUserActivity.class).putExtra("Hospital", Hospital.class));
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
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Hospital hospital = documentSnapshot.toObject(Hospital.class);
                hospital.setDocumentId(documentSnapshot.getId());

                String documentId = hospital.getDocumentId();
                String HospitalName = hospital.getHospitalName();
                String Email = hospital.getEmail();
                String Registration = hospital.getRegistration();
                textViewData.setText("Name: " + HospitalName + "\n" + "Registration: " + Registration + "\n" + "Email: " + Email);
            }
        });
    }
}