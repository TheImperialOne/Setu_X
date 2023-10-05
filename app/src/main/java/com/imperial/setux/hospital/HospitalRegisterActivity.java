package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HospitalRegisterActivity extends AppCompatActivity {

    TextInputEditText etRegEmail, etRegPassword, inputHospitalName, inputRegistration, etRegPasswordConfirm, inputAdmin;
    RadioGroup radioGroup;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HospitalRegisterActivity";
    private static final String NAME = "HospitalName";
    private static final String REGISTRATION = "Registration";
    private static final String IS_ADMIN = "IsAdmin";
    private static final String ADMIN_NO = "AdminNo";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("6969", "4200", "7272", "5995"));
    TextView tvLoginHere;
    Button btnRegister;
    FirebaseAuth mAuth;
    String isAdmin = null;
    RadioButton yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_register);

        etRegEmail = findViewById(R.id.signup_email);
        etRegPassword = findViewById(R.id.signup_password);
        etRegPasswordConfirm = findViewById(R.id.signup_passwordConfirm);
        inputHospitalName = findViewById(R.id.inputHospitalName);
        inputRegistration = findViewById(R.id.inputRegistrationNo);
        radioGroup = findViewById(R.id.groupradio);
        inputAdmin = findViewById(R.id.inputAdminNo);
        tvLoginHere = findViewById(R.id.loginRedirectText);
        btnRegister = findViewById(R.id.signup_button);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);

        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(view -> createUser());

        tvLoginHere.setOnClickListener(view -> startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class)));
    }

    private void createUser() {
        String name = Objects.requireNonNull(inputHospitalName.getText()).toString();
        String registration = Objects.requireNonNull(inputRegistration.getText()).toString();
        String email = Objects.requireNonNull(etRegEmail.getText()).toString();
        String password = Objects.requireNonNull(etRegPassword.getText()).toString();
        String passwordConfirm = Objects.requireNonNull(etRegPasswordConfirm.getText()).toString();
        String adminNo = Objects.requireNonNull(inputAdmin.getText()).toString();
        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        } else if (name.isEmpty()) {
            inputHospitalName.setError("Hospital name cannot be empty");
            inputHospitalName.requestFocus();
        } else if (registration.isEmpty()) {
            inputRegistration.setError("Registration number cannot be empty");
            inputRegistration.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        } else if (TextUtils.isEmpty(passwordConfirm)) {
            etRegPasswordConfirm.setError("Password cannot be empty");
            etRegPasswordConfirm.requestFocus();
        } else if (!password.equals(passwordConfirm)) {
            Toast.makeText(getApplicationContext(), "Passwords must match!", Toast.LENGTH_SHORT).show();
        } else if (isAdmin.equals(null)) {
            Toast.makeText(getApplicationContext(), "Admin rights cannot be null", Toast.LENGTH_SHORT).show();
        } else if (isAdmin.equals("Yes") && adminNo.isEmpty()) {
            inputAdmin.setError("Admin number cannot be empty");
            inputAdmin.requestFocus();
        } else if (isAdmin.equals("Yes") && !arrayList.contains(adminNo)) {
            inputAdmin.setError("Please enter valid Admin number");
            inputAdmin.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put(NAME, name);
                    record.put(REGISTRATION, registration);
                    record.put(EMAIL, email);
                    record.put(PASSWORD, password);
                    record.put(IS_ADMIN, isAdmin);
                    if (isAdmin.equals(true)) record.put(ADMIN_NO, adminNo);
                    db.collection("Hospitals").document(email).set(record)
                            .addOnSuccessListener(aVoid -> Toast.makeText(HospitalRegisterActivity.this, "Record saved", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                Toast.makeText(HospitalRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            });
                    Toast.makeText(HospitalRegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class));
                } else {
                    Toast.makeText(HospitalRegisterActivity.this, "Registration Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void yes(View view) {
        isAdmin = "Yes";
        inputAdmin.setVisibility(View.VISIBLE);
    }

    public void no(View view) {
        isAdmin = "No";
        inputAdmin.setVisibility(View.GONE);
    }
}