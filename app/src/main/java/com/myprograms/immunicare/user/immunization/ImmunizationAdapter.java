package com.myprograms.immunicare.user.immunization;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;
import com.myprograms.immunicare.user.ChildDataActivity;
import com.myprograms.immunicare.user.ImmunizationRecordActivity;
import com.myprograms.immunicare.user.setting.reminder.Children;

import java.util.List;

public class ImmunizationAdapter extends RecyclerView.Adapter<ImmunizationAdapter.ImmunizationViewHolder> {

    private List<Children> immunizationList;
    private Context context;

    // Constructor
    public ImmunizationAdapter(List<Children> immunizationList, Context context) {
        this.immunizationList = immunizationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImmunizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_immunization, parent, false); // Replace layout with your custom item layout
        return new ImmunizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImmunizationViewHolder holder, int position) {

        Children child = immunizationList.get(position);
        holder.childName.setText(child.getChildName());

        holder.cardView.setOnClickListener(v -> {

            String documentId = child.getDocumentId();
            Intent intent = new Intent(context, ImmunizationRecordActivity.class);
            intent.putExtra("documentId", documentId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return immunizationList.size();
    }

    public static class ImmunizationViewHolder extends RecyclerView.ViewHolder {
        public TextView childName;
        public CardView cardView;

        public ImmunizationViewHolder(@NonNull View itemView) {
            super(itemView);

            childName = itemView.findViewById(R.id.childName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
