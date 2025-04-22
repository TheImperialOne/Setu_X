package com.imperial.setux.patient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.imperial.setux.R;
import com.imperial.setux.medicalRecord.MedicalRecord;
import com.imperial.setux.medicalRecord.MedicalRecordAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryActivity extends AppCompatActivity {
    private static final String TAG = "PatientMedicalHistory";
    private static final String BASE_URL = "http://192.168.251.206:3000/api/records/full/";

    private RecyclerView rvMedicalRecords;
    private List<MedicalRecord> recordList;
    private MedicalRecordAdapter adapter;
    private ProgressDialog progressDialog;
    private String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // Get patient email from intent
        patientEmail = getIntent().getStringExtra("userEmail");
        if (patientEmail == null || patientEmail.isEmpty()) {
            showToastAndFinish("No patient email provided");
            return;
        }

        initializeViews();
        setupRecyclerView();
        fetchRecordsFromBackend();
    }

    private void initializeViews() {
        rvMedicalRecords = findViewById(R.id.recycleView);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading records...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setupRecyclerView() {
        recordList = new ArrayList<>();
        adapter = new MedicalRecordAdapter(recordList, this);
        rvMedicalRecords.setLayoutManager(new LinearLayoutManager(this));
        rvMedicalRecords.setAdapter(adapter);
    }

    private void fetchRecordsFromBackend() {
        // Keep the colon prefix and don't encode the @ symbol
        String url = BASE_URL + ":" + patientEmail;
        Log.d(TAG, "Final request URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            if (!response.getBoolean("success")) {
                                showToast("Failed to fetch records: " + response.optString("error"));
                                return;
                            }

                            JSONArray recordsArray = response.getJSONArray("records");
                            recordList.clear();

                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject recordObj = recordsArray.getJSONObject(i);
                                MedicalRecord record = new MedicalRecord(
                                        recordObj.getString("date"),
                                        recordObj.optString("hospitalName", "Unknown Hospital"),
                                        recordObj.getString("doctor"),
                                        recordObj.optString("diagnosis", ""),
                                        recordObj.optString("details", ""),
                                        recordObj.optString("treatment", ""),
                                        recordObj.optString("prescription", ""),
                                        recordObj.optString("fileURL", "")
                                );
                                recordList.add(record);
                            }

                            adapter.notifyDataSetChanged();

                            if (recordList.isEmpty()) {
                                showToast("No medical records found");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error", e);
                            showToast("Error processing records");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        String errorMsg = "Network error";
                        if (error.networkResponse != null) {
                            errorMsg = "Server error: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Server response: " + responseBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error response", e);
                            }
                        }
                        showToast(errorMsg);
                        Log.e(TAG, "API request failed", error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                2, // Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private void parseResponse(JSONObject response) throws JSONException {
        if (!response.has("success") || !response.getBoolean("success")) {
            showToast("Failed to fetch records");
            return;
        }

        if (!response.has("records")) {
            showToast("No records found");
            return;
        }

        JSONArray recordsArray = response.getJSONArray("records");
        recordList.clear();

        for (int i = 0; i < recordsArray.length(); i++) {
            JSONObject recordObj = recordsArray.getJSONObject(i);
            MedicalRecord record = new MedicalRecord(
                    recordObj.optString("date", ""),
                    recordObj.optString("hospitalName", "Unknown Hospital"),
                    recordObj.optString("doctor", ""),
                    recordObj.optString("diagnosis", ""),
                    recordObj.optString("details", ""),
                    recordObj.optString("treatment", ""),
                    recordObj.optString("prescription", ""),
                    recordObj.optString("fileURL", recordObj.optString("filebaseURL", ""))
            );
            recordList.add(record);
        }

        if (recordList.isEmpty()) {
            showToast("No medical records available");
        }

        adapter.notifyDataSetChanged();
    }

    private void handleNetworkError(VolleyError error) {
        String errorMsg = "Network error";
        if (error.networkResponse != null) {
            errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
            if (error.networkResponse.data != null) {
                errorMsg += ": " + new String(error.networkResponse.data);
            }
        }
        Log.e(TAG, errorMsg, error);
        showToast(errorMsg);
    }

    private void handleJsonError(JSONException e) {
        Log.e(TAG, "JSON parsing error", e);
        showToast("Error processing records");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToastAndFinish(String message) {
        showToast(message);
        finish();
    }
}