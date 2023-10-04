package com.imperial.setux.hospital;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.amazonaws.SdkClientException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDiagnosisActivity extends AppCompatActivity {
    String hospitalName;
    String getAadhaar;
    String bucketID;
    private static final String TAG = "AddDiagnosisActivity";
    private static final String DATE = "Date";
    private static final String HOSPITALNAME = "HospitalName";
    private static final String DETAILS = "Details";
    private static final String DIAGNOSIS = "Diagnosis";
    private static final String PRESCRIPTION = "Prescription";
    private static final String DOCTOR = "Doctor";
    private static final String TREATMENT = "Treatment";

    private EditText editDiagnosis, editDetails, editPrescription, editDoctor, editTreatment;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private Button doneButton;
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
        getAadhaar = getIntent().getStringExtra("aadhaar");
        bucketID = getIntent().getStringExtra("bucketID");
        goBack = findViewById(R.id.back);

        doneButton.setOnClickListener(view -> {
            String Diagnosis = editDiagnosis.getText().toString();
            String Details = editDetails.getText().toString();
            String Prescription = editPrescription.getText().toString();
            String Doctor = editDoctor.getText().toString();
            String Treatment = editTreatment.getText().toString();
            JSONObject jsonObject = new JSONObject();
            //Inserting key-value pairs into the json object
            try {
                jsonObject.put("Date", date);
                jsonObject.put("HospitalName", hospitalName);
                jsonObject.put("Diagnosis", Diagnosis);
                jsonObject.put("Details", Details);
                jsonObject.put("Treatment", Treatment);
                jsonObject.put("Doctor", Doctor);
                jsonObject.put("Prescription", Prescription);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String filename = date + getRandomString(6) + ".json"; // Name of your JSON file
            String jsonString = jsonObject.toString();
            File file = new File(getApplicationContext().getFilesDir(), filename);
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            try {
                bufferedWriter.write(jsonString);
                bufferedWriter.close();
                uploadObject(file, filename, bucketID);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Map<String, Object> record = new HashMap<>();
            record.put(DATE, date);
            record.put(HOSPITALNAME, hospitalName);
            record.put(DIAGNOSIS, Diagnosis);
            record.put(DETAILS, Details);
            record.put(DOCTOR, Doctor);
            record.put(TREATMENT, Treatment);
            record.put(PRESCRIPTION, Prescription);

            db.collection("Users").document(getAadhaar).collection("Medical History").add(record).addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Record saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddDiagnosisActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    });
        });

        goBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    void uploadObject(File file, String filename, String bucketID) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("s3.filebase.com", "us-east-1"))
                    .build();

            s3Client.putObject(bucketID, filename, "Uploaded String Object");

            PutObjectRequest request = new PutObjectRequest(bucketID, filename, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", date);
            request.setMetadata(metadata);
            s3Client.putObject(request);
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    String getRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

}