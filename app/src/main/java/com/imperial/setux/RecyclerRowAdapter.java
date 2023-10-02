package com.imperial.setux;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerRowAdapter extends RecyclerView.Adapter<RecyclerRowAdapter.ViewHolder> {
    Context context;
    ArrayList<RowModel> rowModelArrayList;
    public RecyclerRowAdapter(Context context, ArrayList<RowModel> rowModelArrayList){
        this.context = context;
        this.rowModelArrayList = rowModelArrayList;
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
        holder.date.setText(rowModelArrayList.get(position).date);
        holder.hospitalName.setText(rowModelArrayList.get(position).hospitalName);
    }

    @Override
    public int getItemCount() {
        return rowModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date, hospitalName;
        public ViewHolder(View itemView){
            super(itemView);

            date = itemView.findViewById(R.id.date);
            hospitalName = itemView.findViewById(R.id.hospital_name);

        }
    }
}