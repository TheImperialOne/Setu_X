package com.imperial.setux.patient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.LocaleHelper;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;

public class PatientDashboard extends AppCompatActivity {
    SwitchMaterial languageSwitch;
    Button btnLogout, btnMedicalHistory;
    SharedPreferences loginPreferences;
    private static final String SHARED_PREF_NAME = "loginPreferences", GET_AADHAAR = "getAadhaar", GET_LANGUAGE = "getLanguage";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
    DocumentReference documentReference;
    MaterialTextView setDOB, setBloodGroup, setGender, setPhoneNumber, setAadhaarNumber, setAddress, setName;
    MaterialTextView dateOfBirth, bloodGroup, gender, phoneNumber, aadhaarNumber, address;

    private static final String TAG = "PatientDashboard";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";
    private static final String ADDRESS = "Address";
    private String getAadhaar;
    Context context;
    Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        getAadhaar = loginPreferences.getString(GET_AADHAAR, null);
        if(getAadhaar != null){
            documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String Name = documentSnapshot.getString(NAME);
                            String Aadhaar = documentSnapshot.getString(AADHAAR);
                            String DOB = documentSnapshot.getString(DateOfBirth);
                            String Phone = documentSnapshot.getString(PHONE);
                            String Gender = documentSnapshot.getString(GENDER);
                            String BloodGroup = documentSnapshot.getString(BLOOD);
                            String Address = documentSnapshot.getString(ADDRESS);
                            setName.setText(Name);
                            setAadhaarNumber.setText(Aadhaar);
                            setPhoneNumber.setText(Phone);
                            setDOB.setText(DOB);
                            setGender.setText(Gender);
                            setBloodGroup.setText(BloodGroup);
                            setAddress.setText(Address);
                            if(loginPreferences.getBoolean(GET_LANGUAGE, false)){
                                languageSwitch.toggle();
                                context = LocaleHelper.setLocale(PatientDashboard.this, "hi");
                                resources = context.getResources();
                                SharedPreferences.Editor editor = loginPreferences.edit();
                                editor.putBoolean(GET_LANGUAGE, true);
                                editor.apply();
                                editor.commit();
                                btnMedicalHistory.setText(resources.getString(R.string.view_medical_history));
                                btnLogout.setText(resources.getString(R.string.logout));
                                aadhaarNumber.setText(resources.getString(R.string.aadhaar_number));
                                dateOfBirth.setText(resources.getString(R.string.date_of_birth));
                                bloodGroup.setText(resources.getString(R.string.blood_group));
                                gender.setText(resources.getString(R.string.gender));
                                address.setText(resources.getString(R.string.address));
                                phoneNumber.setText(resources.getString(R.string.phone_number));
                            }
                        } else {
                            Toast.makeText(PatientDashboard.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PatientDashboard.this, "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });
        }
        dateOfBirth = findViewById(R.id.dateOfBirth);
        bloodGroup = findViewById(R.id.bloodGroupText);
        gender = findViewById(R.id.genderText);
        address = findViewById(R.id.addressText);
        aadhaarNumber = findViewById(R.id.aadhaarnumber);
        phoneNumber = findViewById(R.id.phonenumber);
        btnLogout = findViewById(R.id.logout_button);
        languageSwitch = findViewById(R.id.lang_switch);
        getAadhaar= getIntent().getStringExtra("aadhaar");
        if(getAadhaar != null){
            documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String Name = documentSnapshot.getString(NAME);
                            String Aadhaar = documentSnapshot.getString(AADHAAR);
                            String DOB = documentSnapshot.getString(DateOfBirth);
                            String Phone = documentSnapshot.getString(PHONE);
                            String Gender = documentSnapshot.getString(GENDER);
                            String BloodGroup = documentSnapshot.getString(BLOOD);
                            String Address = documentSnapshot.getString(ADDRESS);
                            setName.setText(Name);
                            setAadhaarNumber.setText(Aadhaar);
                            setPhoneNumber.setText(Phone);
                            setDOB.setText(DOB);
                            setGender.setText(Gender);
                            setBloodGroup.setText(BloodGroup);
                            setAddress.setText(Address);
                            if(loginPreferences.getBoolean(GET_LANGUAGE, false)){
                                languageSwitch.toggle();
                                context = LocaleHelper.setLocale(PatientDashboard.this, "hi");
                                resources = context.getResources();
                                SharedPreferences.Editor editor = loginPreferences.edit();
                                editor.putBoolean(GET_LANGUAGE, true);
                                editor.apply();
                                editor.commit();
                                btnMedicalHistory.setText(resources.getString(R.string.view_medical_history));
                                btnLogout.setText(resources.getString(R.string.logout));
                                aadhaarNumber.setText(resources.getString(R.string.aadhaar_number));
                                dateOfBirth.setText(resources.getString(R.string.date_of_birth));
                                bloodGroup.setText(resources.getString(R.string.blood_group));
                                gender.setText(resources.getString(R.string.gender));
                                address.setText(resources.getString(R.string.address));
                                phoneNumber.setText(resources.getString(R.string.phone_number));
                            }
                        } else {
                            Toast.makeText(PatientDashboard.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PatientDashboard.this, "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });
        }
        btnMedicalHistory = findViewById(R.id.viewHistoryButton);
        setName = findViewById(R.id.name);
        setDOB = findViewById(R.id.DOB);
        setBloodGroup = findViewById(R.id.bloodGroup);
        setGender = findViewById(R.id.gender);
        setPhoneNumber = findViewById(R.id.phoneNumber);
        setAadhaarNumber = findViewById(R.id.aadhaarNumber);
        setAddress = findViewById(R.id.address);
        btnMedicalHistory.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(), MedicalHistoryActivity.class).putExtra("aadhaar",getAadhaar));
        });
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                context = LocaleHelper.setLocale(PatientDashboard.this, "hi");
                resources = context.getResources();
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean(GET_LANGUAGE, true);
                editor.apply();
                editor.commit();
                btnMedicalHistory.setText(resources.getString(R.string.view_medical_history));
                btnLogout.setText(resources.getString(R.string.logout));
                aadhaarNumber.setText(resources.getString(R.string.aadhaar_number));
                dateOfBirth.setText(resources.getString(R.string.date_of_birth));
                bloodGroup.setText(resources.getString(R.string.blood_group));
                gender.setText(resources.getString(R.string.gender));
                address.setText(resources.getString(R.string.address));
                phoneNumber.setText(resources.getString(R.string.phone_number));
            }else{
                context = LocaleHelper.setLocale(PatientDashboard.this, "en");
                resources = context.getResources();
                btnMedicalHistory.setText(resources.getString(R.string.view_medical_history));
                btnLogout.setText(resources.getString(R.string.logout));
                aadhaarNumber.setText(resources.getString(R.string.aadhaar_number));
                dateOfBirth.setText(resources.getString(R.string.date_of_birth));
                bloodGroup.setText(resources.getString(R.string.blood_group));
                gender.setText(resources.getString(R.string.gender));
                address.setText(resources.getString(R.string.address));
                phoneNumber.setText(resources.getString(R.string.phone_number));
            }
        });
        btnLogout.setOnClickListener(view->{
            context = LocaleHelper.setLocale(PatientDashboard.this, "en");
            resources = context.getResources();
            btnLogout.setText(resources.getString(R.string.logout));
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.remove(GET_AADHAAR);
            editor.clear();
            editor.apply();
            editor.commit();
            finish();
            Toast.makeText(getBaseContext(), "Logged out!", Toast.LENGTH_SHORT).show();
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