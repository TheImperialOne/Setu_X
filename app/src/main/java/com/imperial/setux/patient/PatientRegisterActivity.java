package com.imperial.setux.patient;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;
import com.imperial.setux.hospital.HospitalLoginActivity;
import com.imperial.setux.hospital.HospitalRegisterActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatientRegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, phoneEditText, dobEditText;
    private Spinner bloodGroupSpinner;
    private TextView tvLoginHere;
    private Button registerButton;

    private CardView maleCard, femaleCard;
    private ImageView maleCheck, femaleCheck;
    private String selectedGender = "";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fullNameEditText = findViewById(R.id.inputFullName);
        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        confirmPasswordEditText = findViewById(R.id.signup_passwordConfirm);
        phoneEditText = findViewById(R.id.inputPhone);
        dobEditText = findViewById(R.id.inputDob);
        bloodGroupSpinner = findViewById(R.id.bloodGroupDropdown);
        registerButton = findViewById(R.id.signup_button);

        maleCard = findViewById(R.id.cardMale);
        femaleCard = findViewById(R.id.cardFemale);
        maleCheck = findViewById(R.id.maleCheck);
        femaleCheck = findViewById(R.id.femaleCheck);
        tvLoginHere = findViewById(R.id.loginRedirectText);

        // Blood group dropdown setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(adapter);

        // Gender selection logic
        maleCard.setOnClickListener(view -> {
            selectedGender = "Male";
            maleCheck.setVisibility(View.VISIBLE);
            femaleCheck.setVisibility(View.INVISIBLE);
        });

        femaleCard.setOnClickListener(view -> {
            selectedGender = "Female";
            femaleCheck.setVisibility(View.VISIBLE);
            maleCheck.setVisibility(View.INVISIBLE);
        });

        // Date picker for DOB
        dobEditText.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PatientRegisterActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String dob = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        dobEditText.setText(dob);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(PatientRegisterActivity.this, PatientLoginActivity.class));
            finish();
        });
        registerButton.setOnClickListener(view -> registerPatient());
    }

    private void registerPatient() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(bloodGroup)
                || TextUtils.isEmpty(selectedGender) || !password.equals(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields and select a gender", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savePatientToFirestore(fullName, email, phone, dob, selectedGender, bloodGroup);
                    } else {
                        Toast.makeText(PatientRegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void savePatientToFirestore(String fullName, String email, String phone,
                                        String dob, String gender, String bloodGroup) {

        Map<String, Object> patient = new HashMap<>();
        patient.put("FullName", fullName);
        patient.put("Email", email);
        patient.put("Phone", phone);
        patient.put("DOB", dob);
        patient.put("Gender", gender);
        patient.put("BloodGroup", bloodGroup);

        db.collection("Patients").document(email)
                .set(patient)
                .addOnSuccessListener(unused -> {
                    registerPatientOnChain(email);
                    finish(); // or navigate to dashboard
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save patient: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        Toast.makeText(PatientRegisterActivity.this, "Patient registered successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PatientRegisterActivity.this, PatientLoginActivity.class));
    }

    private void registerPatientOnChain(String email) {
        OkHttpClient client = new OkHttpClient();

        // Create the JSON payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PatientRegisterActivity.this, "Error creating JSON", Toast.LENGTH_LONG).show();
            return;
        }

        // Build the request
        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://192.168.0.102:3000/api/register/patient")
                .post(body)
                .build();

        // Make the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(PatientRegisterActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(PatientRegisterActivity.this, "Patient registered successfully", Toast.LENGTH_LONG).show()
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(PatientRegisterActivity.this, "Blockchain reg failed: " + response.code(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }
}