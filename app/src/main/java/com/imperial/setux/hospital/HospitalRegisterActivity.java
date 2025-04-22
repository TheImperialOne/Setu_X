package com.imperial.setux.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HospitalRegisterActivity extends AppCompatActivity {

    TextInputEditText etRegEmail, etRegPassword, inputHospitalName, inputRegistration, etRegPasswordConfirm;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HospitalRegisterActivity";
    private static final String NAME = "HospitalName";
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

        etRegEmail = findViewById(R.id.signup_email);
        etRegPassword = findViewById(R.id.signup_password);
        etRegPasswordConfirm = findViewById(R.id.signup_passwordConfirm);
        inputHospitalName = findViewById(R.id.inputHospitalName);
        inputRegistration = findViewById(R.id.inputRegistrationNo);
        tvLoginHere = findViewById(R.id.loginRedirectText);
        btnRegister = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> createUser());
        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class));
            finish();
        });
    }

    private void createUser() {
        String name = Objects.requireNonNull(inputHospitalName.getText()).toString();
        String registration = Objects.requireNonNull(inputRegistration.getText()).toString();
        String email = Objects.requireNonNull(etRegEmail.getText()).toString();
        String password = Objects.requireNonNull(etRegPassword.getText()).toString();
        String passwordConfirm = Objects.requireNonNull(etRegPasswordConfirm.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        } else if(TextUtils.isEmpty(passwordConfirm)){
            etRegPasswordConfirm.setError("Password cannot be empty");
            etRegPasswordConfirm.requestFocus();
        } else if (!password.equals(passwordConfirm)) {
            Toast.makeText(getApplicationContext(), "Passwords must match!", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put(NAME, name);
                    record.put(REGISTRATION, registration);
                    record.put(EMAIL, email);
                    record.put(PASSWORD, password);
                    db.collection("Hospitals").document(email).set(record)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(HospitalRegisterActivity.this, "Record saved", Toast.LENGTH_SHORT).show();

                                // 🔗 Register on blockchain via backend
                                registerHospitalOnChain(name, email);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(HospitalRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            });
                    Toast.makeText(HospitalRegisterActivity.this, "Hospital registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class));
                } else {
                    Toast.makeText(HospitalRegisterActivity.this, "Registration Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void registerHospitalOnChain(String hospitalName, String email) {
        OkHttpClient client = new OkHttpClient();

        JSONObject postData = new JSONObject();
        try {
            postData.put("name", hospitalName);  // ✅ Correct key
            postData.put("email", email);        // ✅ Required by backend
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                postData.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("http://192.168.251.206:3000/api/register/hospital")  // ✅ Add /api prefix as per route setup
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Blockchain registration failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Hospital registered on-chain.");
                } else {
                    Log.e(TAG, "❌ Failed to register hospital on-chain: " + response.body().string());
                }
            }
        });
    }



}