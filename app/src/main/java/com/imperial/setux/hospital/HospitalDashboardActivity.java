package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;

public class HospitalDashboardActivity extends AppCompatActivity {
    String getEmail;
    Button btnLogOut;
    FirebaseAuth mAuth;
    Button addNewPatient;
    TextView textViewData;
    CollectionReference collectionReference;
    String HospitalName, Email, Registration, IsAdmin, AdminNo;
    private static final String TAG= "HospitalDashboardActivity.java";
    private static final String HOSPITAL_NAME= "HospitalName";
    private static final String EMAIL= "Email";
    private static final String IS_ADMIN= "IsAdmin";
    private static final String REGISTRATION= "Registration";
    private static final String ADMIN_NO= "AdminNo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
        textViewData = findViewById(R.id.textView);
        addNewPatient = findViewById(R.id.addPatient);
        btnLogOut = findViewById(R.id.btnLogout);
        getEmail = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();
        collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        });
        addNewPatient.setOnClickListener(view -> {
            startActivity(new Intent(HospitalDashboardActivity.this, VerifyPatientActivity.class).putExtra("HospitalName", HospitalName).putExtra("IsAdmin",IsAdmin));
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
                HospitalName = documentSnapshot.getString(HOSPITAL_NAME);
                Email = documentSnapshot.getString(EMAIL);
                Registration = documentSnapshot.getString(REGISTRATION);
                IsAdmin = documentSnapshot.getString(IS_ADMIN);
                assert IsAdmin != null;
                if(IsAdmin.equals("Yes")) {
                    AdminNo = documentSnapshot.getString(ADMIN_NO);
                    textViewData.setText("Name: " + HospitalName + "\n" + "Registration: " + Registration + "\n" + "Email: " + Email+ "\n" + "Is Admin: " + IsAdmin+ "\n" + "Admin Number: " + AdminNo);
                }else{
                    textViewData.setText("Name: " + HospitalName + "\n" + "Registration: " + Registration + "\n" + "Email: " + Email+ "\n" + "Is Admin: " + IsAdmin);
                }
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