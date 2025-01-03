package com.myprograms.immunicare.calendar;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private RecyclerView recyclerView, calendarRecyclerView;
    private ImageButton backBtn;

    private List<Reminder> remindersList = new ArrayList<>();
    private List<String> daysOfMonth = new ArrayList<>();

    private TextView monthName;
    private int currentMonth, currentYear;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference remindersRef = db.collection("reminders");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        recyclerView = findViewById(R.id.upcomingEventRecycler);
        backBtn = findViewById(R.id.backBtn);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthName = findViewById(R.id.monthName);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        for (int i = 1; i <= 31; i++) {
            daysOfMonth.add(String.valueOf(i));
        }

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);



        backBtn.setOnClickListener(v -> {
            finish();
        });

        populateCalendar();

        fetchEvents();

        findViewById(R.id.prevMonthBtn).setOnClickListener(v -> {
            if (currentMonth == 0) {
                currentMonth = 11;  // December
                currentYear--;
            } else {
                currentMonth--;
            }
            populateCalendar();
        });

        findViewById(R.id.nextMonthBtn).setOnClickListener(v -> {
            if (currentMonth == 11) {
                currentMonth = 0;
                currentYear++;
            } else {
                currentMonth++;
            }
            populateCalendar();
        });



    }


    private void populateCalendar() {

        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);
        int currentYear = today.get(Calendar.YEAR);

        // Set calendar to the first day of the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, 1);

        // Get the total number of days in the current month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Get the first day of the month (1 = Sunday, 7 = Saturday)
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        // Clear any previous data from the daysOfMonth list
        daysOfMonth.clear();

        // Add empty spaces for padding before the 1st day of the month
        for (int i = 1; i < firstDayOfMonth; i++) {
            daysOfMonth.add("");  // Empty space for padding
        }

        // Add the actual days of the month to the list
        for (int i = 1; i <= daysInMonth; i++) {
            daysOfMonth.add(String.valueOf(i));
        }

        // Set the month name (e.g., "January 2025")
        String monthNameText = new DateFormatSymbols().getMonths()[currentMonth] + " " + currentYear;
        monthName.setText(monthNameText);

        // Fetch the reminder dates asynchronously
        getReminderDates(new OnReminderDatesFetchedListener() {
            @Override
            public void onReminderDatesFetched(List<String> reminderDates) {
                CalendarAdapter adapter = new CalendarAdapter(daysOfMonth, currentDay, reminderDates, currentMonth, currentYear);
                calendarRecyclerView.setAdapter(adapter);
            }
        });
    }




    private void getReminderDates(final OnReminderDatesFetchedListener listener) {
        List<String> reminderDates = new ArrayList<>();
        remindersRef.whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    remindersList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Reminder reminder = document.toObject(Reminder.class);
                        assert reminder != null;
                        reminderDates.add(reminder.getDate()); // assuming getDate() returns the date in "day/month/year" format
                        remindersList.add(reminder);
                    }
                    // Notify listener once data is fetched
                    listener.onReminderDatesFetched(reminderDates);
                    updateRecyclerView(remindersList); // Show all events initially
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error fetching events", e);
                });
    }

    // Interface for callback when reminder dates are fetched
    public interface OnReminderDatesFetchedListener {
        void onReminderDatesFetched(List<String> reminderDates);
    }



    private void fetchEvents() {

        remindersRef.whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    remindersList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Reminder reminder = document.toObject(Reminder.class);
                        remindersList.add(reminder);
                    }
                    updateRecyclerView(remindersList); // Show all events initially
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error fetching events", e);
                });
    }

    private void updateRecyclerView(List<Reminder> filteredReminders) {
        EventAdapter adapter = new EventAdapter(filteredReminders, CalendarActivity.this);
        recyclerView.setAdapter(adapter);
    }





}