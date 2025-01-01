package com.myprograms.admin.concerns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.admin.R;

import java.util.List;

public class ConcernAdapter extends RecyclerView.Adapter<ConcernAdapter.ConcernViewHolder> {

    private List<Concern> concernList;

    // Constructor
    public ConcernAdapter(List<Concern> concernList) {
        this.concernList = concernList;
    }

    @NonNull
    @Override
    public ConcernAdapter.ConcernViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concern, parent, false);
        return new ConcernViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcernAdapter.ConcernViewHolder holder, int position) {
        // Get the current concern
        Concern concern = concernList.get(position);

        // Bind data to the views
        holder.fullNameTextView.setText(concern.getFullName());
        holder.emailTextView.setText(concern.getEmail());
        holder.titleTextView.setText(concern.getTitle());
        holder.descTextView.setText(concern.getDesc());
        holder.timestampTextView.setText(concern.getTimestamp().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return concernList.size();
    }

    public static class ConcernViewHolder extends RecyclerView.ViewHolder {
        // UI Elements in the item layout
        TextView fullNameTextView, emailTextView, titleTextView, descTextView, timestampTextView;

        public ConcernViewHolder(View itemView) {
            super(itemView);

            // Initialize UI elements
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descTextView = itemView.findViewById(R.id.descTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
