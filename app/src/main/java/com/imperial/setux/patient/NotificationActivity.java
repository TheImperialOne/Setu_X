package com.imperial.setux.patient;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<String> hospitalEmailList = new ArrayList<>();
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.notificationRecycler);
        userEmail = getIntent().getStringExtra("userEmail");

        if (userEmail != null) {
            fetchNotifications(userEmail);
        } else {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNotifications(String email) {
        FirebaseFirestore.getInstance()
                .collection("Patients")
                .document(email)
                .collection("AccessRequests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    hospitalEmailList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String hospitalEmail = doc.getId(); // assuming doc ID = hospital email
                        String status = doc.getString("status");

                        if (status != null && status.equals("pending")) {
                            hospitalEmailList.add(hospitalEmail);
                        }
                    }
                    adapter = new NotificationAdapter(NotificationActivity.this, hospitalEmailList, userEmail);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationActivity", "Error: " + e.getMessage());
                    Toast.makeText(this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                });
    }
}
