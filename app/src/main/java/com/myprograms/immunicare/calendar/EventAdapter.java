package com.myprograms.immunicare.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;
import com.myprograms.immunicare.calendar.Reminder;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Reminder> reminders;
    private Context context;

    public EventAdapter(List<Reminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.dateTextView.setText(reminder.getDate());
        holder.descriptionTextView.setText(reminder.getDescription());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, descriptionTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitle);
            dateTextView = itemView.findViewById(R.id.eventDate);
            descriptionTextView = itemView.findViewById(R.id.eventDescription);
        }
    }
}
