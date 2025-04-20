package com.imperial.setux.hospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imperial.setux.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    private static final String UPLOAD_URL = "http://192.168.0.102:3000/api/uploadRecord";

    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    private MaterialButton doneButton, uploadFiles;
    private MaterialButton verifyPatientBtn;
    private ImageView imageView;
    private MaterialTextView uploadedFileTextView;
    private String lastVerifiedPatientEmail; // Store last verified email
    private CardView goBack;
    private Bitmap bitmap;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseFirestore firestore;

    String hospitalName, hospitalEmail, patientEmail;
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    String date = df.format(new Date());
    private static final String TAG = "AddDiagnosisActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diagnosis);

        FirebaseApp.initializeApp(this);

        editDiagnosis = findViewById(R.id.diagnosisInput);
        editDetails = findViewById(R.id.detailsInput);
        editPrescription = findViewById(R.id.prescriptionInput);
        editDoctor = findViewById(R.id.doctorInput);
        editTreatment = findViewById(R.id.treatmentInput);
        doneButton = findViewById(R.id.done_button);
        uploadFiles = findViewById(R.id.upload_files);
        imageView = findViewById(R.id.imgView);
        uploadedFileTextView = findViewById(R.id.uploadedFileText);
        goBack = findViewById(R.id.back);
        verifyPatientBtn = findViewById(R.id.verifyPatientBtn);
        firestore = FirebaseFirestore.getInstance();

        hospitalName = getIntent().getStringExtra("hospitalName");
        hospitalEmail = getIntent().getStringExtra("hospitalEmail");
        patientEmail = getIntent().getStringExtra("patientEmail");

        uploadFiles.setOnClickListener(v -> {
            SelectImage();
            uploadedFileTextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        });

        verifyPatientBtn.setOnClickListener(view -> {

            if (!Patterns.EMAIL_ADDRESS.matcher(patientEmail).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfPatientExists(patientEmail);
        });

        doneButton.setOnClickListener(view -> {
            checkAccessApproval();
            if (bitmap == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImageToFirebaseAndSendData();
        });

        goBack.setOnClickListener(view -> onBackPressed());
    }

    private void checkIfPatientExists(String email) {
        firestore.collection("Patients")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        lastVerifiedPatientEmail = email;
                        sendAccessRequestToPatient(email);
                    } else {
                        Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error accessing database", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendAccessRequestToPatient(String patientEmail) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String hospitalEmail = currentUser.getEmail();
        if (hospitalEmail == null || hospitalEmail.isEmpty()) {
            Toast.makeText(this, "Failed to get hospital email", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("hospitalEmail", hospitalEmail);
        request.put("timestamp", System.currentTimeMillis());
        request.put("status", "pending");

        DocumentReference reqRef = firestore
                .collection("Patients")
                .document(patientEmail)
                .collection("UploadRequests")
                .document(hospitalEmail);

        reqRef.set(request)
                .addOnSuccessListener(unused -> {
                    verifyPatientBtn.setVisibility(View.GONE);
                    Toast.makeText(this, "Request sent to patient", Toast.LENGTH_SHORT).show();
                    doneButton.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send request", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAccessApproval() {
        if (lastVerifiedPatientEmail == null) {
            doneButton.setVisibility(View.GONE);
            verifyPatientBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Verify a patient first", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String hospitalEmail = currentUser.getEmail();

        firestore.collection("Patients")
                .document(lastVerifiedPatientEmail)
                .collection("UploadRequests")
                .document(hospitalEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("approved".equalsIgnoreCase(status)) {
                            navigateToPatientDetails(lastVerifiedPatientEmail);
                        } else {
                            Toast.makeText(this, "Access still pending", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No access request found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking access", Toast.LENGTH_SHORT).show();
                });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseAndSendData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String fileName = hospitalEmail + "-" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("medical_records/" + fileName);

        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String fileUrl = uri.toString();
            progressDialog.setMessage("Sending data to server...");
            sendDataToBackend(fileUrl, progressDialog);
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendDataToBackend(String fileUrl, ProgressDialog progressDialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        String message = obj.has("message") ? obj.getString("message") : "Uploaded";
                        Toast.makeText(AddDiagnosisActivity.this, message, Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patientEmail", patientEmail);
                params.put("date", date);
                params.put("doctor", editDoctor.getText().toString());
                params.put("details", editDetails.getText().toString());
                params.put("prescription", editPrescription.getText().toString());
                params.put("diagnosis", editDiagnosis.getText().toString());
                params.put("treatment", editTreatment.getText().toString());
                params.put("hospitalName", hospitalName);
                params.put("fileUrl", fileUrl);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void navigateToPatientDetails(String patientEmail) {
        Intent intent = new Intent(this, AddPatientInfoActivity.class);
        intent.putExtra("patientEmail", patientEmail);
        startActivity(intent);
    }
}
