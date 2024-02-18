package com.imperial.setux.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;

public class HospitalDashboardActivity extends AppCompatActivity {
    MaterialTextView hospitalName, hospitalRegn, hospitalEmail, patientCount;
    String getEmail;
    Button btnLogOut;
    FirebaseAuth mAuth;
    Button addNewPatient;
    CollectionReference collectionReference;
    String HospitalName, HospitalEmail, Registration;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "HospitalDashboardActivity";

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
        getEmail = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");

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
        }


        collectionReference.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
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
        });
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
}