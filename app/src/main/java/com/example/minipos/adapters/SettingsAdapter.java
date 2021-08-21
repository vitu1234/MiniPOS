package com.example.minipos.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.activities.AboutActivity;
import com.example.minipos.activities.AppSettingsActivity;
import com.example.minipos.activities.DatabaseSettingsActivity;
import com.example.minipos.activities.PrinterSettingsActivity;
import com.example.minipos.activities.ProfileActivity;
import com.example.minipos.activities.StoreSettingsActivity;
import com.example.minipos.models.StaticGeneralModel;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    Context context;
    List<StaticGeneralModel> staticGeneralModelList;

    public SettingsAdapter(Context context, List<StaticGeneralModel> staticGeneralModelList) {
        this.context = context;
        this.staticGeneralModelList = staticGeneralModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.managements_recycler_line, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageViewManagement.setImageResource(staticGeneralModelList.get(position).getImg_url());
        holder.textViewName.setText(staticGeneralModelList.get(position).getManagement_name());
        holder.textViewDesc.setText(staticGeneralModelList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return staticGeneralModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageViewManagement;
        TextView textViewName, textViewDesc;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewManagement = itemView.findViewById(R.id.imageViewManagementItem);
            textViewName = itemView.findViewById(R.id.managementName);
            textViewDesc = itemView.findViewById(R.id.managementDesc);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();

            //go through each item if you have few items within recycler view
            if (getLayoutPosition() == 0) {
                //Do whatever you want here
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 1) {
                //Do whatever you want here
                Intent intent = new Intent(v.getContext(), StoreSettingsActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 2) {
                //Do whatever you want here
                Intent intent = new Intent(v.getContext(), AppSettingsActivity.class);
                v.getContext().startActivity(intent);

            } else if (getLayoutPosition() == 3) {
                Intent intent = new Intent(v.getContext(), PrinterSettingsActivity.class);
                v.getContext().startActivity(intent);

            } else if (getLayoutPosition() == 4) {
                Intent intent = new Intent(v.getContext(), DatabaseSettingsActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 5) {
                Intent intent = new Intent(v.getContext(), AboutActivity.class);
                v.getContext().startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
