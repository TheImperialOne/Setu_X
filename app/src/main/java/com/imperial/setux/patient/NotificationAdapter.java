package com.imperial.setux.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.imperial.setux.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<String> hospitalEmails;
    private String patientEmail;
    private Context context;

    public NotificationAdapter(Context context, ArrayList<String> hospitalEmails, String patientEmail) {
        this.context = context;
        this.hospitalEmails = hospitalEmails;
        this.patientEmail = patientEmail;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        String hospitalEmail = hospitalEmails.get(position);
        String message = hospitalEmail + " is requesting access to your records.";
        holder.requestMessage.setText(message);

        holder.btnApprove.setOnClickListener(v -> updateStatus(hospitalEmail, "approved"));
        holder.btnReject.setOnClickListener(v -> updateStatus(hospitalEmail, "rejected"));
    }

    private void updateStatus(String hospitalEmail, String status) {
        FirebaseFirestore.getInstance()
                .collection("Patients")
                .document(patientEmail)
                .collection("AccessRequests")
                .document(hospitalEmail)
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Request " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return hospitalEmails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView requestMessage;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            requestMessage = itemView.findViewById(R.id.requestMessage);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
