package com.myprograms.immunicare.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;



import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> reminderDates;
    private final OnItemListener onItemListener;
    private LocalDate selectedDate;


    public CalendarAdapter(ArrayList<String> daysInMonth, ArrayList<String> reminderDates, CalendarActivity onItemListener, java.time.LocalDate selectedDate) {
        this.daysOfMonth = daysInMonth;
        this.reminderDates = reminderDates;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);

        if (selectedDate != null && reminderDates != null) {
            String formattedDate = formatReminderDate(day);
            if (reminderDates.contains(formattedDate)) {
                holder.dayOfMonth.setBackgroundResource(R.drawable.highlight_circle);
            } else {
                holder.dayOfMonth.setBackgroundResource(0);
            }
        }
    }

    private String monthYearFromDate(java.time.LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return date.format(formatter);
        }
        return "";
    }

    private String formatReminderDate(String day) {
        if (!day.isEmpty() && selectedDate != null) {
            return monthYearFromDate(selectedDate).toUpperCase().substring(0, 3) + " " + day + " " + selectedDate.getYear();
        }
        return "";
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}