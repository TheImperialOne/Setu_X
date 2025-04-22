package com.imperial.setux.patient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.LocaleHelper;
import com.imperial.setux.R;
import com.imperial.setux.UserActivity;

public class PatientDashboard extends AppCompatActivity {
    // Views and UI components
    private SwitchMaterial languageSwitch;
    private MaterialButton btnLogout, btnMedicalHistory, btnNearbyHospitals;
    private MaterialTextView setDOB, setBloodGroup, setGender, setPhoneNumber, setName;
    private ImageView notifications;
    private MaterialTextView dateOfBirth, bloodGroup, gender, phoneNumber;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference patientDocumentRef;

    // SharedPreferences and localization
    private SharedPreferences loginPreferences;
    private static final String SHARED_PREF_NAME = "loginPreferences";
    private static final String GET_LANGUAGE = "getLanguage";
    private Context context;
    private Resources resources;

    // Constants
    private static final String TAG = "PatientDashboard";
    private static final String NAME = "Name";
    private static final String DateOfBirth = "DOB";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOOD = "BloodGroup";
    private static final String query = "geo:0,0?q=hospitals near me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        fetchAndStoreWalletAddress(email);

        if (currentUser != null) {
            // User is signed in, get their email
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                // Reference to the patient document using email
                patientDocumentRef = firebaseFirestore.collection("Patients").document(userEmail);
                fetchPatientDetails();
            } else {
                handleUserNotLoggedIn();
            }
        } else {
            handleUserNotLoggedIn();
        }

        setupLanguageSwitch();
        setupButtonListeners();
    }

    private void initializeViews() {
        loginPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        bloodGroup = findViewById(R.id.bloodGroupText);
        gender = findViewById(R.id.genderText);
        phoneNumber = findViewById(R.id.phonenumber);
        btnLogout = findViewById(R.id.logout_button);
        languageSwitch = findViewById(R.id.lang_switch);
        btnMedicalHistory = findViewById(R.id.viewHistoryButton);
        setName = findViewById(R.id.name);
        setDOB = findViewById(R.id.DOB);
        setBloodGroup = findViewById(R.id.bloodGroup);
        setGender = findViewById(R.id.gender);
        setPhoneNumber = findViewById(R.id.phoneNumber);
        btnNearbyHospitals = findViewById(R.id.nearbyHospitals);
        notifications = findViewById(R.id.notification_icon);
    }

    private void fetchPatientDetails() {
        patientDocumentRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, populate UI with patient data
                        String name = documentSnapshot.getString(NAME);
                        String dob = documentSnapshot.getString(DateOfBirth);
                        String phone = documentSnapshot.getString(PHONE);
                        String gender = documentSnapshot.getString(GENDER);
                        String bloodGroup = documentSnapshot.getString(BLOOD);

                        setName.setText(name);
                        setPhoneNumber.setText(phone);
                        setDOB.setText(dob);
                        setGender.setText(gender);
                        setBloodGroup.setText(bloodGroup);

                        // Handle language preference if needed
                        if (loginPreferences.getBoolean(GET_LANGUAGE, false)) {
                            languageSwitch.toggle();
                            updateLocale("hi");
                        }
                    } else {
                        Toast.makeText(this, "Patient record not found", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Patient document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading patient data", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error fetching patient document", e);
                });
    }

    private void handleUserNotLoggedIn() {
        Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserActivity.class));
        finish();
    }

    private void setupLanguageSwitch() {
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String langCode = isChecked ? "hi" : "en";
            updateLocale(langCode);

            // Save language preference
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putBoolean(GET_LANGUAGE, isChecked);
            editor.apply();
        });
    }

    private void updateLocale(String langCode) {
        context = LocaleHelper.setLocale(PatientDashboard.this, langCode);
        resources = context.getResources();

        // Update all text views with new locale
        btnMedicalHistory.setText(resources.getString(R.string.view_medical_history));
        btnLogout.setText(resources.getString(R.string.logout));
        dateOfBirth.setText(resources.getString(R.string.date_of_birth));
        bloodGroup.setText(resources.getString(R.string.blood_group));
        gender.setText(resources.getString(R.string.gender));
        phoneNumber.setText(resources.getString(R.string.phone_number));
    }

    private void setupButtonListeners() {
        notifications.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, NotificationActivity.class)
                        .putExtra("userEmail", currentUser.getEmail()));
            }
        });

        btnMedicalHistory.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, MedicalHistoryActivity.class)
                        .putExtra("patientEmail", currentUser.getEmail()));
            }
        });

        btnMedicalHistory.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, MedicalHistoryActivity.class)
                        .putExtra("userEmail", currentUser.getEmail()));
            }
        });

        btnNearbyHospitals.setOnClickListener(v -> openMap());

        btnLogout.setOnClickListener(view -> {
            // Reset to default language
            updateLocale("en");

            // Sign out from Firebase
            mAuth.signOut();

            // Clear shared preferences
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.clear();
            editor.apply();

            // Go to login screen
            startActivity(new Intent(this, UserActivity.class));
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    private void openMap() {
        Uri uri = Uri.parse(query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void fetchAndStoreWalletAddress(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Patients").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String walletAddress = documentSnapshot.getString("walletAddress");
                        if (walletAddress != null) {
                            // Store in SharedPreferences using 'loginPreferences'
                            SharedPreferences preferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("email", email);
                            editor.putString("walletAddress", walletAddress);
                            editor.apply();

                            Toast.makeText(this, "Wallet address saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Wallet address not found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No document found for this email!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch wallet address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}