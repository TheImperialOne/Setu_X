package com.imperial.setux.medicalRecord;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imperial.setux.R;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.RecordViewHolder> {

    private List<MedicalRecord> records;
    private Context context;

    public MedicalRecordAdapter(List<MedicalRecord> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_row, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        MedicalRecord record = records.get(position);
        holder.tvDate.setText("Date: " + record.getDate());
        holder.tvDoctor.setText("Doctor: " + record.getDoctor());
        holder.tvHospital.setText("Hospital: " + record.getHospital());
        holder.tvDiagnosis.setText("Diagnosis: " + record.getDiagnosis());
        holder.tvDiagnosis.setText("Treatment: " + record.getTreatment());

        holder.tvPrescription.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Prescription")
                    .setMessage(record.getPrescription())
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDoctor, tvHospital, tvDiagnosis, tvTreatment, tvPrescription;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
        }
    }
}
