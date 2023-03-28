package com.imperial.setux;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HospitalRegisterActivity extends AppCompatActivity {

    TextInputEditText etRegEmail, etRegPassword, inputHospitalName, inputRegistration;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HospitalRegisterActivity";
    private static final String NAME = "Name";
    private static final String REGISTRATION = "Registration";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    TextView tvLoginHere;
    Button btnRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_register);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        inputHospitalName = findViewById(R.id.inputName);
        inputRegistration = findViewById(R.id.inputRegn);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> createUser());

        tvLoginHere.setOnClickListener(view -> startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class)));
    }

    private void createUser() {
        String name = Objects.requireNonNull(inputHospitalName.getText()).toString();
        String registration = Objects.requireNonNull(inputRegistration.getText()).toString();
        String email = Objects.requireNonNull(etRegEmail.getText()).toString();
        String password = Objects.requireNonNull(etRegPassword.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put(NAME, name);
                    record.put(REGISTRATION, registration);
                    record.put(EMAIL, email);
                    record.put(PASSWORD, password);
                    db.collection("Hospitals").add(record)
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
}