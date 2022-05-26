package com.ask2784.fieldmanagement.databases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ask2784.fieldmanagement.databinding.FieldsItemsBinding;

import java.util.ArrayList;

public class FieldDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Fields> fieldsArrayList;
    private final OnClickListener onClickListener;

    public FieldDetailsAdapter(ArrayList<Fields> fieldsArrayList, OnClickListener onClickListener) {
        this.fieldsArrayList = fieldsArrayList;
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new FieldViewHolder(FieldsItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            FieldViewHolder fieldViewHolder = (FieldViewHolder) holder;
            Fields field = fieldsArrayList.get(0);
            fieldViewHolder.binding.setFields(field);
            fieldViewHolder.binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return fieldsArrayList.size();
    }

    public class FieldViewHolder extends RecyclerView.ViewHolder {
        FieldsItemsBinding binding;

        public FieldViewHolder(@NonNull FieldsItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> onClickListener.onViewClick(getAdapterPosition()));
        }
    }

    public class FieldDetailsViewHolder extends RecyclerView.ViewHolder {
        public FieldDetailsViewHolder(@NonNull View itemView1) {
            super(itemView1);
        }
    }
}
