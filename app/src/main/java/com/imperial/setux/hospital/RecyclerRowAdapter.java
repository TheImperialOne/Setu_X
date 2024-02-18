package com.imperial.setux.hospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.imperial.setux.R;
import com.imperial.setux.patient.Patient;

import java.util.ArrayList;

public class RecyclerRowAdapter extends RecyclerView.Adapter<com.imperial.setux.hospital.RecyclerRowAdapter.ViewHolder> {
    Context context;
    ArrayList<Patient> patientArrayList;
    private final com.imperial.setux.hospital.SelectListener selectListener;
    public RecyclerRowAdapter(Context context, ArrayList<Patient> patientArrayList, SelectListener selectListener){
        this.context = context;
        this.patientArrayList = patientArrayList;
        this.selectListener = selectListener;
    }
    @NonNull
    @Override
    public com.imperial.setux.hospital.RecyclerRowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_row, parent, false);
        com.imperial.setux.hospital.RecyclerRowAdapter.ViewHolder viewHolder = new com.imperial.setux.hospital.RecyclerRowAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.imperial.setux.hospital.RecyclerRowAdapter.ViewHolder holder, int position) {
        holder.aadhaarNo.setText(patientArrayList.get(position).getAadhaarNo());
        holder.patientName.setText(patientArrayList.get(position).getName());
        holder.cardView.setOnClickListener(view->{
            selectListener.onItemClicked(patientArrayList.get(position), view);
        });
    }

    @Override
    public int getItemCount() {
        return patientArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView aadhaarNo, patientName;
        CardView cardView;
        public ViewHolder(View itemView){
            super(itemView);

            aadhaarNo = itemView.findViewById(R.id.date);
            patientName = itemView.findViewById(R.id.hospital_name);
            cardView = itemView.findViewById(R.id.cardViewRoot);
        }
    }
}