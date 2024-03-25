package com.imperial.setux;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imperial.setux.hospital.HospitalLoginActivity;
import com.imperial.setux.patient.PatientLoginActivity;

public class UserActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    SwitchMaterial languageSwitch;
    MaterialTextView whoAreYou, selectOption;
    TextView hospitalAdmin, patient;
    ImageView hospitalCheck, patientCheck;
    CardView hospitalGroup, patientGroup;
    Button next;
    Context context;
    Resources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        hospitalCheck = findViewById(R.id.doctorCheck);
        patientCheck = findViewById(R.id.patientCheck);
        hospitalGroup = findViewById(R.id.hospitalGroup);
        patientGroup = findViewById(R.id.patientGroup);
        whoAreYou = findViewById(R.id.who);
        selectOption = findViewById(R.id.selectOption);
        hospitalAdmin = findViewById(R.id.hospitalAdmin);
        patient = findViewById(R.id.patient);
        next = findViewById(R.id.nextButton);
        languageSwitch = findViewById(R.id.lang_switch);
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                context = LocaleHelper.setLocale(UserActivity.this, "hi");
                resources = context.getResources();
                whoAreYou.setText(resources.getString(R.string.who_are_you));
                selectOption.setText(resources.getString(R.string.please_select_one_of_the_options));
                hospitalAdmin.setText(resources.getString(R.string.hospital_admin));
                patient.setText(resources.getString(R.string.patient));
                next.setText(resources.getString(R.string.next));
            }else{
                context = LocaleHelper.setLocale(UserActivity.this, "en");
                resources = context.getResources();
                whoAreYou.setText(resources.getString(R.string.who_are_you));
                selectOption.setText(resources.getString(R.string.please_select_one_of_the_options));
                hospitalAdmin.setText(resources.getString(R.string.hospital_admin));
                patient.setText(resources.getString(R.string.patient));
                next.setText(resources.getString(R.string.next));
            }
        });
        hospitalGroup.setOnClickListener(view->{
            if(hospitalCheck.getVisibility()== View.VISIBLE){
                hospitalCheck.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
            } else{
                hospitalCheck.setVisibility(View.VISIBLE);
                patientCheck.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        });
        patientGroup.setOnClickListener(view->{
            if(patientCheck.getVisibility()==View.VISIBLE){
                patientCheck.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
            } else{
                patientCheck.setVisibility(View.VISIBLE);
                hospitalCheck.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        });
        next.setOnClickListener(view->{
            if(hospitalCheck.getVisibility()==View.VISIBLE) startActivity(new Intent(getApplicationContext(), HospitalLoginActivity.class));
            else if(patientCheck.getVisibility()==View.VISIBLE) startActivity(new Intent(getApplicationContext(), PatientLoginActivity.class));
            else Toast.makeText(getApplicationContext(), "You must choose one of the options to proceed further!", Toast.LENGTH_LONG).show();
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