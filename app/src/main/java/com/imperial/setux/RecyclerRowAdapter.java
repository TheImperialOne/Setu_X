package com.imperial.setux;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.imperial.setux.patient.PatientDiagnosis;

import java.util.ArrayList;

public class RecyclerRowAdapter extends RecyclerView.Adapter<RecyclerRowAdapter.ViewHolder> {
    Context context;
    ArrayList<PatientDiagnosis> patientDiagnosisArrayList;
    private final SelectListener selectListener;
    public RecyclerRowAdapter(Context context, ArrayList<PatientDiagnosis> patientDiagnosisArrayList, SelectListener selectListener){
        this.context = context;
        this.patientDiagnosisArrayList = patientDiagnosisArrayList;
        this.selectListener = selectListener;
    }
    @NonNull
    @Override
    public RecyclerRowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerRowAdapter.ViewHolder holder, int position) {
        holder.date.setText(patientDiagnosisArrayList.get(position).getDate());
        holder.hospitalName.setText(patientDiagnosisArrayList.get(position).getHospitalName());
        holder.cardView.setOnClickListener(view->{
            selectListener.onItemClicked(patientDiagnosisArrayList.get(position), view);
        });
    }

    @Override
    public int getItemCount() {
        return patientDiagnosisArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date, hospitalName;
        CardView cardView;
        public ViewHolder(View itemView){
            super(itemView);

            date = itemView.findViewById(R.id.date);
            hospitalName = itemView.findViewById(R.id.hospital_name);
            cardView = itemView.findViewById(R.id.cardViewRoot);
        }
    }
}