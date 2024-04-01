package com.imperial.setux.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
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
import com.imperial.setux.LocaleHelper;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;
import com.imperial.setux.patient.Patient;
import com.imperial.setux.patient.PatientDashboard;

import java.util.ArrayList;

public class HospitalDashboardActivity extends AppCompatActivity implements SelectListener {
    SharedPreferences loginPreferences;
    private static final String SHARED_PREF_NAME = "loginPreferences", GET_EMAIL = "getEmail", GET_LANGUAGE = "getLanguage";
    MaterialTextView setHospitalName, setHospitalRegn, setHospitalEmail, setPatientCount, hospitalName, hospitalEmail, hospitalRegn, patientsDiagnosed, welcome;
    SwitchMaterial languageSwitch;
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
    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        setHospitalName = findViewById(R.id.name);
        setHospitalRegn = findViewById(R.id.regn);
        setHospitalEmail = findViewById(R.id.email);
        setPatientCount = findViewById(R.id.count);
        welcome = findViewById(R.id.welcome);
        hospitalName = findViewById(R.id.hospitalname);
        hospitalRegn = findViewById(R.id.registration);
        hospitalEmail = findViewById(R.id.emailText);
        patientsDiagnosed = findViewById(R.id.patientDiagnosed);
        addNewPatient = findViewById(R.id.addNew);
        btnLogOut = findViewById(R.id.logout_button);
        recyclerView = findViewById(R.id.recycleView);
        languageSwitch = findViewById(R.id.lang_switch);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        HospitalEmail = user.getEmail();
        getEmail = getIntent().getStringExtra("email");
        collectionReference = FirebaseFirestore.getInstance().collection("Hospitals");
        hospitalReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail);
        patientReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail).collection("Patient Data");
        if (mAuth.getCurrentUser().getEmail() != null) {
            if (loginPreferences.getBoolean(GET_LANGUAGE, false)) {
                languageSwitch.toggle();
                context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "hi");
                resources = context.getResources();
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean(GET_LANGUAGE, true);
                editor.apply();
                editor.commit();
                welcome.setText(resources.getString(R.string.welcome));
                hospitalName.setText(resources.getString(R.string.hospital_name_colon));
                hospitalEmail.setText(resources.getString(R.string.email_colon));
                hospitalRegn.setText(resources.getString(R.string.registration_no_colon));
                patientsDiagnosed.setText(resources.getString(R.string.patients_diagnosed));
                addNewPatient.setText(resources.getString(R.string.add_new_patient));
                btnLogOut.setText(resources.getString(R.string.logout));
            }
        }
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "hi");
                resources = context.getResources();
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean(GET_LANGUAGE, true);
                editor.apply();
                editor.commit();
            } else {
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean(GET_LANGUAGE, false);
                editor.apply();
                editor.commit();
                context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "en");
                resources = context.getResources();
            }
            welcome.setText(resources.getString(R.string.welcome));
            hospitalName.setText(resources.getString(R.string.hospital_name_colon));
            hospitalEmail.setText(resources.getString(R.string.email_colon));
            hospitalRegn.setText(resources.getString(R.string.registration_no_colon));
            patientsDiagnosed.setText(resources.getString(R.string.patients_diagnosed));
            addNewPatient.setText(resources.getString(R.string.add_new_patient));
            btnLogOut.setText(resources.getString(R.string.logout));
        });
        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "en");
            resources = context.getResources();
            btnLogOut.setText(resources.getString(R.string.logout));
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.remove(GET_LANGUAGE);
            editor.clear();
            editor.apply();
            editor.commit();
            finish();
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
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if (user == null) {
            startActivity(new Intent(HospitalDashboardActivity.this, UserActivity.class));
        } else {
            hospitalReference = FirebaseFirestore.getInstance().collection("Hospitals").document(HospitalEmail);
            hospitalReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            HospitalName = documentSnapshot.getString(HOSPITALNAME);
                            Registration = documentSnapshot.getString(REGISTRATION_NO);
                            setHospitalName.setText(HospitalName);
                            setHospitalRegn.setText(Registration);
                            setHospitalEmail.setText(HospitalEmail);
                            if (loginPreferences.getBoolean(GET_LANGUAGE, false)) {
                                languageSwitch.toggle();
                                context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "hi");
                            } else {
                                context = LocaleHelper.setLocale(HospitalDashboardActivity.this, "en");
                            }
                            resources = context.getResources();
                            welcome.setText(resources.getString(R.string.welcome));
                            hospitalName.setText(resources.getString(R.string.hospital_name_colon));
                            hospitalEmail.setText(resources.getString(R.string.email_colon));
                            hospitalRegn.setText(resources.getString(R.string.registration_no_colon));
                            patientsDiagnosed.setText(resources.getString(R.string.patients_diagnosed));
                            addNewPatient.setText(resources.getString(R.string.add_new_patient));
                            btnLogOut.setText(resources.getString(R.string.logout));
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
                    setPatientCount.setText((String.valueOf(snapshot.getCount())));
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
                arrayList.add(new Patient(Aadhaar, BloodGroup, DOB, Gender, Name, Phone));
            }
        });
        RecyclerRowAdapter recyclerRowAdapter = new RecyclerRowAdapter(this, arrayList, this);
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
        startActivity(new Intent(getApplicationContext(), ViewHospitalPatients.class).putExtra("aadhaar", patient.getAadhaarNo()));
    }
}