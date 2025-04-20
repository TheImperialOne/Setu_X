package com.imperial.setux.hospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.imperial.setux.R;
import com.imperial.setux.medicalRecord.MedicalRecord;
import com.imperial.setux.medicalRecord.MedicalRecordAdapter;
import com.imperial.setux.patient.RecyclerRowAdapter;
import com.imperial.setux.patient.SelectListener;
import com.imperial.setux.patient.PatientDiagnosis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientMedicalHistory extends AppCompatActivity {
    private RecyclerView rvMedicalRecords;
    private List<MedicalRecord> recordList;
    private MedicalRecordAdapter adapter;
    private static final String BASE_URL = "http://192.168.0.102:3000/api/records/";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical_history);

        // Initialize views
        rvMedicalRecords = findViewById(R.id.recycleView);
        rvMedicalRecords.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        recordList = new ArrayList<>();
        adapter = new MedicalRecordAdapter(recordList, this);
        rvMedicalRecords.setAdapter(adapter);

        // Show loading dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading records...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Fetch records
        fetchRecordsFromBackend();
    }

    private void fetchRecordsFromBackend() {
        SharedPreferences preferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);
        String walletAddress = preferences.getString("wallet", null);

        if (walletAddress == null) {
            Toast.makeText(this, "Wallet address not found!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        String url = BASE_URL + walletAddress;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONArray recordsArray = response.getJSONArray("records");
                        for (int i = 0; i < recordsArray.length(); i++) {
                            JSONObject recordObj = recordsArray.getJSONObject(i);

                            MedicalRecord record = new MedicalRecord(
                                    recordObj.getString("date"),
                                    recordObj.getString("hospitalName"),
                                    recordObj.getString("doctor"),
                                    recordObj.getString("diagnosis"),
                                    recordObj.getString("details"),
                                    recordObj.getString("treatment"),
                                    recordObj.getString("prescription"),
                                    recordObj.optString("fileUrl", null)
                            );

                            recordList.add(record);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing records", Toast.LENGTH_SHORT).show();
                        Log.e("JSON_PARSE", "Error: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("API_ERROR", "Fetch failed: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch records", Toast.LENGTH_SHORT).show();

                    // Optional: Show retry button or logic here
                });

        // Add timeout and retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }
}