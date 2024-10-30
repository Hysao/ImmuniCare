package com.myprograms.immunicare.user.setting.reminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;
import com.myprograms.immunicare.calendar.CalendarViewHolder;

import java.util.ArrayList;

class ReminderCalendarAdapter extends RecyclerView.Adapter<ReminderCalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final ReminderCalendarAdapter.OnItemListener onItemListener;

    public ReminderCalendarAdapter(ArrayList<String> daysOfMonth, ReminderCalendarAdapter.OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ReminderCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new ReminderCalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderCalendarViewHolder holder, int position)
    {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
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