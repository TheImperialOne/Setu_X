package com.imperial.setux.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imperial.setux.LocaleHelper;
import com.imperial.setux.R;
import com.imperial.setux.patient.PatientDashboard;

import java.util.Objects;

public class HospitalLoginActivity extends AppCompatActivity {
    SharedPreferences loginPreferences;
    private static final String SHARED_PREF_NAME = "loginPreferences", GET_EMAIL = "getHospitalEmail", GET_LANGUAGE = "getLanguage";
    MaterialTextView loginToYourAccount;
    TextInputEditText etLoginEmail;
    TextInputEditText etLoginPassword;
    TextView tvRegisterHere;
    Button btnLogin;

    Context context;
    Resources resources;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_login);
        loginToYourAccount = findViewById(R.id.loginToYourAccount);
        etLoginEmail = findViewById(R.id.login_email);
        etLoginPassword = findViewById(R.id.login_password);
        tvRegisterHere = findViewById(R.id.signUpRedirectText);
        btnLogin = findViewById(R.id.login_button);
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });
        tvRegisterHere.setOnClickListener(view -> {
            startActivity(new Intent(HospitalLoginActivity.this, HospitalRegisterActivity.class));
            finish();
        });

    }

    private void loginUser() {
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    context = LocaleHelper.setLocale(HospitalLoginActivity.this, "en");
                    resources = context.getResources();
                    loginToYourAccount.setText(resources.getString(R.string.login_to_your_account));
                    SharedPreferences.Editor editor = loginPreferences.edit();
                    editor.putString(GET_EMAIL, email);
                    editor.putBoolean(GET_LANGUAGE, false);
                    editor.apply();
                    Toast.makeText(HospitalLoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HospitalLoginActivity.this, HospitalDashboardActivity.class).putExtra("email",email));
                } else {
                    Toast.makeText(HospitalLoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}