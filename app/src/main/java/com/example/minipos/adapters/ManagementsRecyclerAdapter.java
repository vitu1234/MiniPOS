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
import com.example.minipos.activities.ProductsActivity;
import com.example.minipos.activities.ProductsCategoryActivity;
import com.example.minipos.activities.ProductsSupplierActivity;
import com.example.minipos.models.Management;

import java.util.List;

public class ManagementsRecyclerAdapter extends RecyclerView.Adapter<ManagementsRecyclerAdapter.MyViewHolder> {
    Context context;
    List<Management> managementList;

    public ManagementsRecyclerAdapter(Context context, List<Management> managementList) {
        this.context = context;
        this.managementList = managementList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.managements_recycler_line, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageViewManagement.setImageResource(managementList.get(position).getImg_url());
        holder.textViewName.setText(managementList.get(position).getManagement_name());
        holder.textViewDesc.setText(managementList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return managementList.size();
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
                Intent intent = new Intent(v.getContext(), ProductsActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 1) {
                //Do whatever you want here
                Intent intent = new Intent(v.getContext(), ProductsCategoryActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 2) {


            } else if (getLayoutPosition() == 3) {
                Intent intent = new Intent(v.getContext(), ProductsSupplierActivity.class);
                v.getContext().startActivity(intent);

            } else if (getLayoutPosition() == 4) {

            } else if (getLayoutPosition() == 5) {

            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
