package com.ask2784.fieldmanagement.databases.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ask2784.fieldmanagement.databases.OnClickListener;
import com.ask2784.fieldmanagement.databases.models.FieldDetails;
import com.ask2784.fieldmanagement.databases.models.Fields;
import com.ask2784.fieldmanagement.databinding.FieldDetailsBinding;
import com.ask2784.fieldmanagement.databinding.FieldsItemsBinding;

import java.util.ArrayList;

public class FieldDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Fields> fieldsArrayList;
    private final OnClickListener onClickListener;
    private final ArrayList<FieldDetails> fieldDetailsList;
    private static final int VIEW_ONE = 0;
    private static final int VIEW_TWO = 1;

    public FieldDetailsAdapter(ArrayList<Fields> fieldsArrayList, OnClickListener onClickListener, ArrayList<FieldDetails> fieldDetailsList) {
        this.fieldsArrayList = fieldsArrayList;
        this.onClickListener = onClickListener;
        this.fieldDetailsList = fieldDetailsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ONE) {
            return new FieldViewHolder(FieldsItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == VIEW_TWO) {
            return new FieldDetailsViewHolder(FieldDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);
        if (itemType == VIEW_ONE) {
            FieldViewHolder fieldViewHolder = (FieldViewHolder) holder;
            Fields field = fieldsArrayList.get(0);
            fieldViewHolder.binding.setFields(field);
            fieldViewHolder.binding.executePendingBindings();
        } else if (itemType == VIEW_TWO) {
            FieldDetailsViewHolder fieldDetailsViewHolder = (FieldDetailsViewHolder) holder;
            FieldDetails fieldDetail = fieldDetailsList.get(position-1);
            fieldDetailsViewHolder.fieldDetailsBinding.setFieldDetails(fieldDetail);
            fieldDetailsViewHolder.fieldDetailsBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return fieldsArrayList.size() + fieldDetailsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == VIEW_ONE)
            return VIEW_ONE;
        else return VIEW_TWO;
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
        FieldDetailsBinding fieldDetailsBinding;

        public FieldDetailsViewHolder(@NonNull FieldDetailsBinding fieldDetailsBinding) {
            super(fieldDetailsBinding.getRoot());
            this.fieldDetailsBinding = fieldDetailsBinding;
            fieldDetailsBinding.getRoot().setOnClickListener(view -> onClickListener.onViewClick(getAdapterPosition()));
        }
    }
}
