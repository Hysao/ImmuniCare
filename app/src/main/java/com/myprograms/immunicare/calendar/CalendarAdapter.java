package com.myprograms.immunicare.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<String> daysOfMonth;
    private int currentDay;
    private List<String> reminderDates;
    private int currentMonth;
    private int currentYear;
    private int todayMonth, todayYear;

    public CalendarAdapter(List<String> daysOfMonth, int currentDay, List<String> reminderDates,
                           int currentMonth, int currentYear, int todayMonth, int todayYear) {
        this.daysOfMonth = daysOfMonth;
        this.currentDay = currentDay;
        this.reminderDates = reminderDates;
        this.currentMonth = currentMonth;
        this.currentYear = currentYear;
        this.todayMonth = todayMonth;
        this.todayYear = todayYear;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_day_item, parent, false);
        return new CalendarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayText.setText(day);

        // Reset to default styles
        holder.dayText.setBackgroundColor(Color.TRANSPARENT);
        holder.dayText.setTextColor(Color.BLACK);

        // Check if day is valid and non-empty
        if (!day.isEmpty()) {
            // Check if the displayed month/year matches today's month/year
            boolean isCurrentDay = Integer.parseInt(day) == currentDay &&
                    currentMonth == todayMonth &&
                    currentYear == todayYear;

            boolean isReminderDay = hasReminder(day);

            // Highlight current day
            if (isCurrentDay) {
                holder.dayText.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.immuniCareBlue));
                holder.dayText.setTextColor(Color.WHITE);
            }

            // Highlight reminder days (override or combine with current day logic)
            if (isReminderDay) {
                holder.dayText.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.immuniCareOrange));


                if (isCurrentDay) {
                    holder.dayText.setTextColor(Color.YELLOW);
                }
            }
        }
    }

    private boolean hasReminder(String day) {

        if (day.isEmpty()) return false;


        String dayMonthYear = day + "/" + (currentMonth + 1) + "/" + currentYear;


        for (String reminder : reminderDates) {
            if (reminder.equals(dayMonthYear)) {
                return true;
            }
        }

        return false;
    }



    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
