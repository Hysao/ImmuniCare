package com.myprograms.admin.schedule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.myprograms.admin.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context context;
    private List<Schedule> schedulesList;

    public ScheduleAdapter(Context context, List<Schedule> schedulesList) {
        this.context = context;
        this.schedulesList = schedulesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Schedule schedule = schedulesList.get(position);
        String status = schedule.getStatus();
        String userId = schedule.getUserId();
        String documentId = schedule.getDocumentId();

        if (status.equals("pending")) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.immuniCareGrey));
        } else if (status.equals("approved")) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.immuniCareGreen));
        }else {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.immuniCareRed));
        }


        holder.userName.setText(schedule.getUserName());
        holder.userEmail.setText(schedule.getUserEmail());
        holder.visit.setText(schedule.getVisit());
        holder.vaccines.setText(schedule.getEachVaccine());
        holder.date.setText(schedule.getDate());

        android.util.Log.d("ScheduleAdapter", "userId: " + userId + ", documentId: " + documentId);

        if (status == null || userId == null || documentId == null) {
            android.util.Log.e("ScheduleAdapter", "Missing required data in schedule.");
        }

        holder.cardView.setOnClickListener(v -> {
            if (userId != null && documentId != null) {
                Intent intent = new Intent(context, ScheduleDetailsActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("documentId", documentId);
                context.startActivity(intent);
            } else {
                android.util.Log.e("ScheduleAdapter", "userId or documentId is null.");
            }
        });


    }

    @Override
    public int getItemCount() {
        return schedulesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userEmail, visit, vaccines, date;
        public MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            visit = itemView.findViewById(R.id.visit);
            vaccines = itemView.findViewById(R.id.vaccines);
            date = itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.request);
        }
    }
}
