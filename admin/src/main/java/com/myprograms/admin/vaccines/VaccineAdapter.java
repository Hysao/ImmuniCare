package com.myprograms.admin.vaccines;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.List;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder> {

    private List<Vaccines> vaccineList;
    private Context context;

    public VaccineAdapter(Context context, List<Vaccines> vaccineList) {
        this.context = context;
        this.vaccineList = vaccineList;
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccines, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        Vaccines vaccine = vaccineList.get(position);
        Context context = holder.itemView.getContext();

        holder.vaccineNameTextView.setText(vaccine.getName());
        holder.stockTextView.setText(String.format("Stock: %s", vaccine.getStock()));
        holder.manufacturedDateTextView.setText(String.format("MFD: %s", vaccine.getManufacturedDate()));
        holder.expiryDateTextView.setText(String.format("EXP: %s", vaccine.getExpiryDate()));

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, VaccineEditActivity.class);
            intent.putExtra("vaccineName", vaccine.getName());
            context.startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(v -> {
            deleteVaccine(vaccine, position);
        });
    }

    private void deleteVaccine(Vaccines vaccine, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vaccines")
                .document(vaccine.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    vaccineList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Vaccine deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete vaccine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return vaccineList.size();
    }

    public static class VaccineViewHolder extends RecyclerView.ViewHolder {

        public TextView vaccineNameTextView, stockTextView, manufacturedDateTextView, expiryDateTextView;
        public MaterialButton editBtn, deleteBtn;

        public VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            vaccineNameTextView = itemView.findViewById(R.id.vaccineName);
            stockTextView = itemView.findViewById(R.id.vaccinesStocks);
            manufacturedDateTextView = itemView.findViewById(R.id.vaccineMFD);
            expiryDateTextView = itemView.findViewById(R.id.vaccineEXP);
            editBtn = itemView.findViewById(R.id.vaccineEdit);
            deleteBtn = itemView.findViewById(R.id.vaccineDelete);
        }
    }

}
