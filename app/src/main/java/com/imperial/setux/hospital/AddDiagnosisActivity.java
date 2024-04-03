package com.imperial.setux.hospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imperial.setux.R;
import com.imperial.setux.patient.Patient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class AddDiagnosisActivity extends AppCompatActivity {
    MaterialTextView uploadedFileTextView;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private ImageView imageView;
    String hospitalName, hospitalEmail;
    String getAadhaar;
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";
    private static final String DOCTOR = "Doctor";
    private static final String TREATMENT = "Treatment";
    private static final String NAME = "Name";
    private static final String AADHAAR = "Aadhaar";
    private static final String DateOfBirth = "DateOfBirth";
    private static final String PHONE = "Phone";
    private static final String GENDER = "Gender";
    private static final String BLOODGROUP = "BloodGroup";
    private String Name, Aadhaar, DOB, Phone, Gender, BloodGroup;
    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    //
    DocumentReference documentReference;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private MaterialButton doneButton, uploadFiles;
    String date = df.format(new Date());
    CardView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diagnosis);
        editDiagnosis = findViewById(R.id.diagnosisInput);
        editDetails = findViewById(R.id.detailsInput);
        editPrescription = findViewById(R.id.prescriptionInput);
        editDoctor = findViewById(R.id.doctorInput);
        editTreatment = findViewById(R.id.treatmentInput);
        doneButton = findViewById(R.id.done_button);
        hospitalName = getIntent().getStringExtra("hospitalName");
        hospitalEmail = getIntent().getStringExtra("hospitalEmail");
        uploadedFileTextView = findViewById(R.id.uploadedFileText);
        getAadhaar = getIntent().getStringExtra("aadhaar");
        documentReference = firebaseFirestore.collection("Users").document(getAadhaar);
        goBack = findViewById(R.id.back);
        uploadFiles = findViewById(R.id.upload_files);
        imageView = findViewById(R.id.imgView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uploadFiles.setOnClickListener(v -> {
            SelectImage();
            uploadedFileTextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        });

        doneButton.setOnClickListener(view -> {
            String Diagnosis = editDiagnosis.getText().toString();
            String Details = editDetails.getText().toString();
            String Prescription = editPrescription.getText().toString();
            String Doctor = editDoctor.getText().toString();
            String Treatment = editTreatment.getText().toString();
            Map<String, Object> record = new HashMap<>();
            record.put(DATE, date);
            record.put(HOSPITALNAME, hospitalName);
            record.put(DIAGNOSIS, Diagnosis);
            record.put(DETAILS, Details);
            record.put(DOCTOR, Doctor);
            record.put(TREATMENT, Treatment);
            record.put(PRESCRIPTION, Prescription);
            db.collection("Users").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                        String documentID = documentReference.getId();
                        uploadImage(documentID, getAadhaar);
                        Toast.makeText(AddDiagnosisActivity.this, "Patient Record saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    });

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Patient patient = documentSnapshot.toObject(Patient.class);
                            Name = documentSnapshot.getString(NAME);
                            Aadhaar = documentSnapshot.getString(AADHAAR);
                            DOB = documentSnapshot.getString(DateOfBirth);
                            Phone = documentSnapshot.getString(PHONE);
                            Gender = documentSnapshot.getString(GENDER);
                            BloodGroup = documentSnapshot.getString(BLOODGROUP);
                            Map<String, Object> patientRecord = new HashMap<>();
                            patientRecord.put(NAME, Name);
                            patientRecord.put(AADHAAR, Aadhaar);
                            patientRecord.put(DateOfBirth, DOB);
                            patientRecord.put(PHONE, Phone);
                            patientRecord.put(GENDER, Gender);
                            patientRecord.put(BLOODGROUP, BloodGroup);
                            db.collection("Hospitals").document(hospitalEmail).collection("Patient Data").document(getAadhaar).set(patientRecord)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(AddDiagnosisActivity.this, "Hospital Record saved", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    });

                            db.collection("Hospitals").document(hospitalEmail).collection("Patient Data").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Hospital Record saved", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(AddDiagnosisActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.toString());
                    });


        });

        goBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage(String documentID, String getAadhaar) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/" + getAadhaar + "/" + documentID);

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast
                                        .makeText(AddDiagnosisActivity.this,
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(AddDiagnosisActivity.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int) progress + "%");
                            });
        }
    }
}